package com.example.prekshasingla.cashlessbazar;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;


/**
 * A simple {@link Fragment} subclass.
 */
public class RechargeFragment extends Fragment {


    public RechargeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview= inflater.inflate(R.layout.fragment_recharge, container, false);
        rootview.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        CardView mobile=rootview.findViewById(R.id.recharge_mobile);
        CardView dth=rootview.findViewById(R.id.recharge_dth);
        CardView datacard=rootview.findViewById(R.id.recharge_datacard);

        final NavController navController=Navigation.findNavController(getActivity(),R.id.fragment);
        mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args= new Bundle();
                args.putString("type","mobile");
                navController.navigate(R.id.rechargeFragment2,args);
            }
        });
        dth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args= new Bundle();
                args.putString("type","dth");
                navController.navigate(R.id.rechargeFragment2,args);
            }
        });
        datacard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args= new Bundle();
                args.putString("type","datacard");
                navController.navigate(R.id.rechargeFragment2,args);
            }
        });


        return rootview;
    }

}
