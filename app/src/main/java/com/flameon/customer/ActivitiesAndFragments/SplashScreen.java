package com.flameon.customer.ActivitiesAndFragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flameon.customer.BuildConfig;
import com.flameon.customer.Constants.GpsUtils;
import com.flameon.customer.Constants.PreferenceClass;
import com.flameon.customer.GoogleMapWork.MapsActivity;
import com.flameon.customer.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Locale;

/**
 * Created by RaoMudassar on 12/4/17.
 */

public class SplashScreen extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {



    public static String VERSION_CODE;

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    TextView welcome_location_txt;
    private RelativeLayout main_welcome_screen_layout, main_splash_layout, welcome_search_div;

    //  ProgressDialog pd;
    String getCurrentLocationAddress;



    SharedPreferences sharedPreferences;
    double latitude, longitude;

    private Button welcome_show_restaurants_btn;


    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSION_DATA_ACCESS_CODE = 2;



    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            setContentView(R.layout.splash);
            sharedPreferences = getSharedPreferences(PreferenceClass.user, MODE_PRIVATE);
            getCurrentLocationAddress = sharedPreferences.getString(PreferenceClass.CURRENT_LOCATION_ADDRESS, "");

            VERSION_CODE = BuildConfig.VERSION_NAME;

            final String android_id = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            SharedPreferences.Editor editor2 = sharedPreferences.edit();
            editor2.putString(PreferenceClass.UDID, android_id).commit();


            buildGoogleApiClient();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(5000);
            locationRequest.setFastestInterval(1000);


            welcome_location_txt = findViewById(R.id.welcome_location_txt);
            main_welcome_screen_layout = findViewById(R.id.main_welcome_screen_layout);
            main_splash_layout = findViewById(R.id.main_splash_layout);
            welcome_search_div = findViewById(R.id.welcome_search_div);
            welcome_show_restaurants_btn = findViewById(R.id.welcome_show_restaurants_btn);
            welcome_show_restaurants_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    MapsActivity.SAVE_LOCATION = false;
                    finish();
                }
            });


            welcome_search_div.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(SplashScreen.this, MapsActivity.class);
                    startActivityForResult(i, PERMISSION_DATA_ACCESS_CODE);

                }
            });


            if (!getCurrentLocationAddress.isEmpty()) {

                main_welcome_screen_layout.setVisibility(View.GONE);
                main_splash_layout.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        String getUserType = sharedPreferences.getString(PreferenceClass.USER_TYPE, "");
                        boolean getLoINSession = sharedPreferences.getBoolean(PreferenceClass.IS_LOGIN, false);

                        if (!getLoINSession) {
                            Intent i = new Intent(SplashScreen.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        } else {

                            if (getUserType.equalsIgnoreCase("user")) {
                                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }

                        }
                    }
                }, SPLASH_TIME_OUT);

            } else {

                displayLocation();

            }

        }catch (Exception e){

        }

    }



    @SuppressWarnings("deprecation")
    private void displayLocation() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!GpsStatus) {

            new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
                @Override
                public void gpsStatus(boolean isGPSEnable) {

                }
            });


        }
        else if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            buildGoogleApiClient();
            createLocationRequest();

        } else {

            ActivityCompat.requestPermissions(SplashScreen.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        }

    }



    private boolean checkPlayServices() {

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }

            return false;
        }

        return true;

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_DATA_ACCESS_CODE) {

            if(resultCode == RESULT_OK) {
                latitude = Double.parseDouble(data.getStringExtra("lat"));
                longitude = Double.parseDouble(data.getStringExtra("lng"));


                //  Geocoder gcd = new Geocoder(this, Locale.getDefault());
                Address locationAddress;

                locationAddress=getAddress(latitude,longitude);
                if(locationAddress!=null)
                {

                    String city="";
                    if(locationAddress.getLocality()!=null && !locationAddress.getLocality().equals("null"))
                        city = ""+locationAddress.getLocality();

                    String country="";
                    if(locationAddress.getCountryName()!=null && !locationAddress.getCountryName().equals("null"))
                        country = ""+locationAddress.getCountryName();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(PreferenceClass.CURRENT_LOCATION_LAT_LONG,latitude+","+longitude);
                    editor.putString(PreferenceClass.CURRENT_LOCATION_ADDRESS,city+" " +country);
                    editor.putString(PreferenceClass.LATITUDE,String.valueOf(latitude));
                    editor.putString(PreferenceClass.LONGITUDE,String.valueOf(longitude));
                    editor.commit();

                    welcome_location_txt.setText(getCurrentLocationAddress);
                    welcome_location_txt.setText(city+" " +country);

                }


            }


        }
        else if(requestCode==3){
            displayLocation();
        }


        }

    public Address getAddress(double latitude,double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude,longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }







    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayLocation();
                }
            }
        }

    }

















    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private int UPDATE_INTERVAL = 3000;
    private int FATEST_INTERVAL = 3000;
    private int DISPLACEMENT = 0;
    private FusedLocationProviderClient mFusedLocationClient;
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }


    LocationCallback locationCallback;
    protected void startLocationUpdates() {
        mGoogleApiClient.connect();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback= new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            Address locationAddress;

                            locationAddress = getAddress(latitude, longitude);
                            if (locationAddress != null) {

                                String city = "";
                                if (locationAddress.getLocality() != null && !locationAddress.getLocality().equals("null"))
                                    city = "" + locationAddress.getLocality();

                                String country = "";
                                if (locationAddress.getCountryName() != null && !locationAddress.getCountryName().equals("null"))
                                    country = "" + locationAddress.getCountryName();


                                if (!getCurrentLocationAddress.isEmpty()) {
                                    welcome_location_txt.setText(getCurrentLocationAddress);
                                } else {

                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(PreferenceClass.CURRENT_LOCATION_LAT_LONG, latitude + "," + longitude);
                                    editor.putString(PreferenceClass.CURRENT_LOCATION_ADDRESS, city + " " + country);
                                    editor.putString(PreferenceClass.LATITUDE, String.valueOf(latitude));
                                    editor.putString(PreferenceClass.LONGITUDE, String.valueOf(longitude));
                                    editor.commit();

                                    welcome_location_txt.setText(getCurrentLocationAddress);
                                    welcome_location_txt.setText(city + " " + country);
                                }

                            }

                        } else {

                            welcome_location_txt
                                    .setText("Kalma Chowk, Lahore");

                        }


                        stopLocationUpdates();


                }
            }
        };

        mFusedLocationClient.requestLocationUpdates(mLocationRequest,locationCallback
                , Looper.myLooper());

    }


    protected void stopLocationUpdates() {
        if(mFusedLocationClient!=null && locationCallback!=null)
        mFusedLocationClient.removeLocationUpdates(locationCallback);
    }


    @Override
    public void onDestroy() {
        if (mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    public void onConnected(Bundle arg0) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }


}
