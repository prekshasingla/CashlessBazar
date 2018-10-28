package com.example.prekshasingla.cashlessbazar;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class PaymentOptionsFragment extends Fragment {


    public PaymentOptionsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView= inflater.inflate(R.layout.fragment_payment_options, container, false);
        final LinearLayout cardLayout= rootView.findViewById(R.id.card_layout);
        rootView.findViewById(R.id.card_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View card= getLayoutInflater().inflate(R.layout.card_layout,null,false);
                cardLayout.addView(card);
            }
        });
        return rootView;
    }



}
