package com.flameon.customer.ActivitiesAndFragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flameon.customer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Select_DateTime_F extends Fragment {


    public Select_DateTime_F() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_date_time, container, false);
    }

}
