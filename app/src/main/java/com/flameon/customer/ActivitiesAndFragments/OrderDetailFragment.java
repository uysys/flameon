package com.flameon.customer.ActivitiesAndFragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flameon.customer.Adapters.ExpandableListAdapter;
import com.flameon.customer.Constants.AllConstants;
import com.flameon.customer.Constants.ApiRequest;
import com.flameon.customer.Constants.Callback;
import com.flameon.customer.Constants.Config;
import com.flameon.customer.Constants.PreferenceClass;
import com.flameon.customer.GoogleMapWork.TrackingActivity;
import com.flameon.customer.Models.MenuItemExtraModel;
import com.flameon.customer.Models.MenuItemModel;
import com.flameon.customer.Models.NewOrderModelClass;
import com.flameon.customer.R;
import com.flameon.customer.Utils.CustomExpandableListView;
import com.flameon.customer.Utils.FontHelper;
import com.flameon.customer.Utils.RelateToFragment_OnBack.RootFragment;
import com.flameon.customer.Utils.TabLayoutUtils;
import com.gmail.samehadar.iosdialog.CamomileSpinner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by qboxus on 10/18/2019.
 */

public class OrderDetailFragment extends RootFragment {

    ImageView back_icon;
    TextView order_title_tv,inst_tv,hotel_name_tv,hotel_phone_number_tv,hotel_add_tv,total_amount_tv,payment_method_tv,total_tip_tv,
            order_user_name_tv,order_user_address_tv,order_user_number_tv,total_delivery_fee_tv,tax_tv,total_tex_tv,total_discount_tv,sub_total_amount_tv;
    SharedPreferences orderSharedPreferences;
    ExpandableListAdapter listAdapter;
    CustomExpandableListView customExpandableListView;
    ArrayList<MenuItemModel> listDataHeader;
    ArrayList<MenuItemExtraModel> listChildData;
    private ArrayList<ArrayList<MenuItemExtraModel>> ListChild;
    String order_id,user_id;


    private RelativeLayout track_order_div,accept_div,decline_div;
    public static boolean FLAG_ACCEPT;
    private static int pick_up;
    String order_status="";
    ScrollView scrolView;
    CamomileSpinner orderProgress;
    RelativeLayout transparent_layer,progressDialog;
    String delivery;
    public static boolean CALLBACK_ORDERFRAG,ACCPT_DEC_FLAG;
    public static String serverkey;


    View view;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.order_detail_fragment, container, false);
        context=getContext();

        orderSharedPreferences = getContext().getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);
        FrameLayout frameLayout = view.findViewById(R.id.order_detail_container);
        FontHelper.applyFont(getContext(),frameLayout, AllConstants.verdana);

        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        pick_up = 0;
        order_id = orderSharedPreferences.getString(PreferenceClass.ORDER_ID,"");
        user_id = orderSharedPreferences.getString(PreferenceClass.pre_user_id,"");
        init(view);
        getserverkey();
        getOrderDetailItems();


        customExpandableListView = (CustomExpandableListView ) view.findViewById(R.id.custon_list_order_items);
        customExpandableListView .setExpanded(true);
        customExpandableListView.setGroupIndicator(null);

        customExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return true; // This way the expander cannot be collapsed
            }
        });

        return view;
    }

    @SuppressLint("ResourceAsColor")
    public void init(View v){

        sub_total_amount_tv = v.findViewById(R.id.sub_total_amount_tv);
        orderProgress = v.findViewById(R.id.orderProgress);
        orderProgress.start();
        progressDialog = v.findViewById(R.id.progressDialog);
        transparent_layer = v.findViewById(R.id.transparent_layer);
        String order_title = orderSharedPreferences.getString(PreferenceClass.ORDER_HEADER,"");
        String order_inst = orderSharedPreferences.getString(PreferenceClass.ORDER_INS,"");
        delivery = orderSharedPreferences.getString("delivery_","");
        order_title_tv = v.findViewById(R.id.order_title_tv);

        track_order_div = v.findViewById(R.id.track_order_div);

        order_title_tv.setText(order_title.replaceAll("&amp;", "&"));

        accept_div = v.findViewById(R.id.accept_div);
        decline_div = v.findViewById(R.id.decline_div);
        scrolView = v.findViewById(R.id.scrolView);
        /// All Text from API
        hotel_name_tv = v.findViewById(R.id.order_hotel_name);
        hotel_add_tv = v.findViewById(R.id.order_hotel_address);
        hotel_phone_number_tv = v.findViewById(R.id.order_hotel_number);
        payment_method_tv = v.findViewById(R.id.payment_method_tv);
        total_amount_tv = v.findViewById(R.id.total_amount_tv);
        inst_tv = v.findViewById(R.id.inst_tv);
        inst_tv.setText(order_inst);
        order_user_name_tv = v.findViewById(R.id.order_user_name_tv);
        order_user_address_tv = v.findViewById(R.id.order_user_address_tv);
        order_user_number_tv = v.findViewById(R.id.order_user_number_tv);
        total_delivery_fee_tv = v.findViewById(R.id.total_delivery_fee_tv);
        total_tip_tv = v.findViewById(R.id.total_tip_tv);
        tax_tv = v.findViewById(R.id.tax_tv);
        total_tex_tv = v.findViewById(R.id.total_tex_tv);
        total_discount_tv=v.findViewById(R.id.total_discount_tv);

        /// End
        track_order_div.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pick_up==1 || (order_status!=null && order_status.equals("2"))){
                 }
                else {
                    getActivity().startActivity(new Intent(getContext(), TrackingActivity.class));
                }
            }
        });




        back_icon = v.findViewById(R.id.back_icon);
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().onBackPressed();

            }
        });


        accept_div.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FLAG_ACCEPT = true;
                customDialog();
                ACCPT_DEC_FLAG = true;

            }
        });

        decline_div.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FLAG_ACCEPT = false;
                customDialog();
                ACCPT_DEC_FLAG = true;


            }
        });


    }


    public void getOrderDetailItems(){

        listDataHeader = new ArrayList<MenuItemModel>();
        ListChild = new ArrayList<>();

         JSONObject orderJsonObject = new JSONObject();
        try {
            orderJsonObject.put("order_id",order_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,false);
        transparent_layer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);

        ApiRequest.Call_Api(context, Config.SHOW_ORDER_DETAIL, orderJsonObject, new Callback() {
            @Override
            public void Responce(String resp) {

                try {
                    JSONObject jsonResponse = new JSONObject(resp);

                    int code_id  = Integer.parseInt(jsonResponse.optString("code"));

                    if(code_id == 200) {

                        JSONObject json = new JSONObject(jsonResponse.toString());
                        JSONArray jsonArray = json.getJSONArray("msg");

                        for (int i=0;i<jsonArray.length();i++){

                            JSONObject allJsonObject = jsonArray.getJSONObject(i);
                            JSONObject orderJsonObject = allJsonObject.optJSONObject("Order");
                            JSONObject userInfoObj = allJsonObject.optJSONObject("UserInfo");
                            JSONObject userAddressObj = allJsonObject.optJSONObject("Address");
                            JSONObject restaurantJsonObject = allJsonObject.optJSONObject("Restaurant");
                            JSONObject taxObj = restaurantJsonObject.optJSONObject("Tax");
                            JSONObject restaurantCurrencuObj = restaurantJsonObject.getJSONObject("Currency");

                            String currency_symbol= restaurantCurrencuObj.optString("symbol");

                            String first_name = userInfoObj.optString("first_name");
                            String last_name = userInfoObj.optString("last_name");
                            order_user_name_tv.setText(first_name+" "+last_name);
                            order_user_number_tv.setText(userInfoObj.optString("phone"));
                            String street_user = userAddressObj.optString("street");
                            String city_user = userAddressObj.optString("city");



                            if(delivery.equalsIgnoreCase("0")){
                                order_user_address_tv.setText("Pick Up");
                            }
                            else {
                                order_user_address_tv.setText(street_user + ", " + city_user);
                            }

                            order_status=orderJsonObject.optString("status","");
                            if(order_user_address_tv.getText().toString().equalsIgnoreCase("Pick Up")){
                                track_order_div.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.trackColor));
                                pick_up = 1;
                            }

                            else if(order_status.equals("2")){
                                track_order_div.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.trackColor));

                            }


                            inst_tv.setText(orderJsonObject.optString("instructions"));
                            total_amount_tv.setText(currency_symbol+orderJsonObject.optString("price"));

                            String getPaymentMethodTV = orderJsonObject.optString("cod");
                            if(getPaymentMethodTV.equalsIgnoreCase("0")) {
                                payment_method_tv.setText("Credit Card");
                            }
                            else {
                                payment_method_tv.setText("Cash On Delivery");
                            }

                            hotel_name_tv.setText(restaurantJsonObject.optString("name"));
                            hotel_phone_number_tv.setText(restaurantJsonObject.optString("phone"));
                            JSONObject restaurantAddress = restaurantJsonObject.getJSONObject("RestaurantLocation");
                            String street = restaurantAddress.optString("street");
                            String city = restaurantAddress.optString("city");

                            hotel_add_tv.setText(street+", "+city);

                            String tax = orderJsonObject.optString("tax");

                            total_discount_tv.setText(currency_symbol+""+orderJsonObject.optString("discount"));

                            String delivery_fee = orderJsonObject.optString("delivery_fee");
                            total_delivery_fee_tv.setText(currency_symbol+delivery_fee);
                            String tax_free = restaurantJsonObject.optString("tax_free");
                            String rider_tip = orderJsonObject.optString("rider_tip");
                            if(rider_tip.equalsIgnoreCase("")){
                                rider_tip = "0.0";
                            }
                            String taxPercent = taxObj.optString("tax");
                            if(tax_free.equalsIgnoreCase("1")) {
                                tax_tv.setText("(" + "0" + "%)");
                                total_tex_tv.setText(currency_symbol+" 0.0");
                            }
                            else {
                                tax_tv.setText("(" + taxPercent + "%)");
                                total_tex_tv.setText(currency_symbol+" "+tax);
                            }

                            String subTotal = orderJsonObject.optString("sub_total");
                            sub_total_amount_tv.setText(currency_symbol+" "+subTotal);
                            total_tip_tv.setText(currency_symbol+" "+rider_tip);


                            JSONArray menuItemArray = allJsonObject.getJSONArray("OrderMenuItem");

                            for (int j=0;j<menuItemArray.length();j++) {

                                JSONObject alljsonJsonObject2 = menuItemArray.getJSONObject(j);
                                MenuItemModel menuItemModel = new MenuItemModel();
                                menuItemModel.setItem_name(alljsonJsonObject2.optString("name"));
                                menuItemModel.setItem_price(currency_symbol + alljsonJsonObject2.optString("price"));
                                menuItemModel.setId(alljsonJsonObject2.optString("id"));
                                menuItemModel.setOrder_id(alljsonJsonObject2.optString("order_id"));
                                menuItemModel.setOrder_quantity(alljsonJsonObject2.optString("quantity"));

                                listDataHeader.add(menuItemModel);

                                listChildData = new ArrayList<>();

                                JSONArray extramenuItemArray = alljsonJsonObject2.getJSONArray("OrderMenuExtraItem");
                                if(extramenuItemArray!=null&& extramenuItemArray.length()>0){
                                    for (int k = 0; k < extramenuItemArray.length(); k++) {
                                        if (extramenuItemArray.length() != 0) {
                                            JSONObject allJsonObject3 = extramenuItemArray.getJSONObject(k);
                                            MenuItemExtraModel menuItemExtraModel = new MenuItemExtraModel();

                                            menuItemExtraModel.setExtra_item_name(allJsonObject3.optString("name"));
                                            menuItemExtraModel.setPrice(allJsonObject3.optString("price"));
                                            menuItemExtraModel.setQuantity(allJsonObject3.optString("quantity"));
                                            menuItemExtraModel.setCurrency(currency_symbol);

                                            listChildData.add(menuItemExtraModel);

                                        }

                                    }

                                }
                                ListChild.add(listChildData);
                            }
                        }

                        listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, ListChild);


                        customExpandableListView.setAdapter(listAdapter);
                        for(int l=0; l < listAdapter.getGroupCount(); l++)
                            if(ListChild.size()!=0) {
                                customExpandableListView.expandGroup(l);
                            }
                    }

                }catch (Exception e){
                    e.getMessage();

                }


                TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,true);
                transparent_layer.setVisibility(View.GONE);
                progressDialog.setVisibility(View.GONE);
            }
        });

    }


    public void getserverkey(){
        DatabaseReference mref= FirebaseDatabase.getInstance().getReference();

        final Query query2 =mref.child("restaurant").child(user_id).child("CurrentOrders").orderByChild("order_id").equalTo(order_id);
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot nodeDataSnapshot : dataSnapshot.getChildren()) {
                    serverkey = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    public void customDialog(){

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialog);


        final EditText reasonTxt = (EditText)dialog.findViewById(R.id.ed_message);
        Button done_btn = (Button)dialog.findViewById(R.id.done_btn);
        Button cancel_btn = (Button)dialog.findViewById(R.id.cancel_btn);

        if(FLAG_ACCEPT){
            reasonTxt.setHint("Type rider instructions (Optional)");
        }
        else {
            reasonTxt.setHint("Type rider instructions");
        }

        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (!FLAG_ACCEPT) {
                    FLAG_ACCEPT = false;
                    if(reasonTxt.getText().toString().isEmpty()){

                        Toast.makeText(getContext(),"Type rider instructions",Toast.LENGTH_LONG).show();

                    }
                    else {
                        DatabaseReference delete_order=FirebaseDatabase.getInstance().getReference();

                        delete_order.child("restaurant").child(user_id).child("CurrentOrders").child(serverkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                setAccDecStatus(reasonTxt.getText().toString());
                                dialog.dismiss();
                            }
                        });
                    }
                }
                else {
                    final DatabaseReference add_to_onother=FirebaseDatabase.getInstance().getReference()
                            .child("restaurant").child(user_id);
                    add_to_onother.child("CurrentOrders").child(serverkey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            NewOrderModelClass modelClass=dataSnapshot.getValue(NewOrderModelClass.class);
                            HashMap<String,String> map=new HashMap<>();
                            map.put("deal",modelClass.getDeal());
                            map.put("order_id",modelClass.getOrder_id());
                            map.put("status",modelClass.getStatus());

                            add_to_onother.child("PendingOrders").child(serverkey)
                                    .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {


                                    add_to_onother.child("CurrentOrders").child(serverkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            setAccDecStatus(reasonTxt.getText().toString());
                                            dialog.dismiss(); }
                                    });

                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                FLAG_ACCEPT = false;
                ACCPT_DEC_FLAG = false;
            }
        });

        dialog.show();

    }

    public void setAccDecStatus(String text){

        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,false);
        transparent_layer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss");
        String currentDateandTime = sdf.format(new Date());

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("order_id", order_id);
            jsonObject.put("key",serverkey);
            jsonObject.put("time", currentDateandTime);
            if (FLAG_ACCEPT) {
                jsonObject.put("status", "1");
                jsonObject.put("rejected_reason", "");
                jsonObject.put("accepted_reason", text);

            }
            else {
                jsonObject.put("status", "2");
                jsonObject.put("rejected_reason", text);
                jsonObject.put("accepted_reason", "");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context, Config.ACCEPT_DECLINE_STATUS, jsonObject, new Callback() {
            @Override
            public void Responce(String resp) {


                TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,true);
                transparent_layer.setVisibility(View.GONE);
                progressDialog.setVisibility(View.GONE);

                try {
                    JSONObject jsonObject1 = new JSONObject(resp);

                    int code = Integer.parseInt(jsonObject1.optString("code"));

                    if(code==200){

                        scrolView.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                       }
            }
        });

    }


}
