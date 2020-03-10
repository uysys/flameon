package com.flameon.customer.ActivitiesAndFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flameon.customer.Constants.Config;
import com.flameon.customer.Constants.PreferenceClass;
import com.flameon.customer.Models.DealsModel;
import com.flameon.customer.R;
import com.flameon.customer.Utils.RelateToFragment_OnBack.RootFragment;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by qboxus on 10/18/2019.
 */

public class DealsDetailFragment extends RootFragment {

    ImageView back_icon,close_icon;
    SimpleDraweeView deals_bg_img;

    Button increament_btn,decrement_btn;
    TextView inc_dec_tv;
    int present_count = 1;
    SharedPreferences dealsDetailPref;
    String deals_name,deals_desc,deals_price,deals_hotel_name,deals_image,deals_symbol;
    TextView deals_menu_item_title_tv,deal_name_tv,deal_amount_tv,deal_hotel_name_tv,deal_desc_tv;
    RelativeLayout deals_order_now_div;
    public static boolean FLAG_DEALS_DETAIL_FRAGMENT;


    DealsModel dealsModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_deals_main, container, false);


        Bundle bundle=getArguments();
        if(bundle!=null){
            dealsModel=(DealsModel) bundle.getSerializable("data");
        }

        dealsDetailPref = getContext().getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);

        FrameLayout deals_main_container = view.findViewById(R.id.deals_main_container);
        deals_main_container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        init(view);
        SharedPreferences.Editor editor = dealsDetailPref.edit();
        editor.putInt(PreferenceClass.DEALS_QUANTITY,1).commit();
        return view;

    }

    public void init(View v){
        deals_desc = dealsModel.deal_desc;
        deals_name =dealsModel.deal_name;
        deals_price =dealsModel.deal_price;
        deals_hotel_name =dealsModel.restaurant_name;
        deals_image =dealsModel.deal_image;
        deals_symbol =dealsModel.deal_symbol;

        deals_menu_item_title_tv = v.findViewById(R.id.deals_menu_item_title_tv);
        deal_name_tv = v.findViewById(R.id.deal_name_tv);
        deal_amount_tv = v.findViewById(R.id.deal_amount_tv);
        deal_hotel_name_tv = v.findViewById(R.id.deal_hotel_name_tv);
        deal_desc_tv = v.findViewById(R.id.deal_desc_tv);
        deals_bg_img = v.findViewById(R.id.deals_bg_img);

        deal_desc_tv.setText(deals_desc);
        deal_amount_tv.setText(deals_symbol+""+deals_price);
        deal_hotel_name_tv.setText(deals_hotel_name);
        deal_name_tv.setText(deals_name);
        deals_menu_item_title_tv.setText(deals_name);


        Uri uri = Uri.parse(Config.imgBaseURL+deals_image);
        deals_bg_img.setImageURI(uri);



        back_icon = v.findViewById(R.id.back_icon);
        close_icon = v.findViewById(R.id.clos_menu_deals_detail);
        increament_btn = v.findViewById(R.id.plus_btn);
        decrement_btn = v.findViewById(R.id.minus_btn);
        inc_dec_tv = v.findViewById(R.id.inc_dec_tv);
        deals_order_now_div = v.findViewById(R.id.deals_order_now_div);


        increament_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    String presentValStr=inc_dec_tv.getText().toString();
                    present_count=Integer.parseInt(presentValStr);
                    present_count++;
                    inc_dec_tv.setText(String.valueOf(present_count));
                    SharedPreferences.Editor editor = dealsDetailPref.edit();
                    editor.putInt(PreferenceClass.DEALS_QUANTITY,present_count).commit();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                 }
            }
        });

        decrement_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    String presentValStr=inc_dec_tv.getText().toString();
                    present_count=Integer.parseInt(presentValStr);
                    if(presentValStr.equalsIgnoreCase(String.valueOf(Integer.parseInt("1")))) {
                      //  Toast.makeText(getContext(),"Can not Less than 1",Toast.LENGTH_LONG).show();
                    }
                    else {
                        present_count--;
                    }
                    inc_dec_tv.setText(String.valueOf(present_count));
                    SharedPreferences.Editor editor = dealsDetailPref.edit();
                    editor.putInt(PreferenceClass.DEALS_QUANTITY,present_count).commit();

                }
                catch(Exception e)
                {
                    e.printStackTrace();
                 //   Toast.makeText(getContext(),"Some error :(",Toast.LENGTH_LONG).show();
                }
            }
        });



        if(DealsFragment.FLAG_DEAL_FRAGMENT){
            back_icon.setVisibility(View.VISIBLE);
            close_icon.setVisibility(View.GONE);
            DealsFragment.FLAG_DEAL_FRAGMENT = false;
        }
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              getActivity().onBackPressed();

            }
        });

        deals_order_now_div.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DealOrderFragment dealOrderFragment = new DealOrderFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                Bundle bundle=new Bundle();
                bundle.putSerializable("data",dealsModel);
                dealOrderFragment.setArguments(bundle);
                transaction.addToBackStack(null);
                transaction.add(R.id.deals_main_container, dealOrderFragment,"parent").commit();
                FLAG_DEALS_DETAIL_FRAGMENT = true;
            }
        });

    }
}
