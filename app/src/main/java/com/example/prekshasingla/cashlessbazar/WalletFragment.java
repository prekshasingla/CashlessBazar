package com.example.prekshasingla.cashlessbazar;


import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import instamojo.library.InstamojoPay;
import instamojo.library.InstapayListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class WalletFragment extends Fragment {

    InstapayListener listener;
    public WalletFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview= inflater.inflate(R.layout.fragment_wallet, container, false);
        rootview.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        final SharedPreferenceUtils sharedPreferenceUtils=SharedPreferenceUtils.getInstance(getActivity());
        TextView walletBalance= rootview.findViewById(R.id.wallet_balance);
        final AppCompatEditText amount=rootview.findViewById(R.id.amount);

        walletBalance.setText(SharedPreferenceUtils.getInstance(getActivity()).getCBTPBalance()+"");

        final LinearLayout addLayout=rootview.findViewById(R.id.add_container);
        Button addMoney=rootview.findViewById(R.id.add_money);
        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!amount.getText().toString().trim().equals(""))
                {
                    ((WalletActivity)getActivity()).callInstamojoPay(sharedPreferenceUtils.getEmail(),
                            sharedPreferenceUtils.getPhone(),amount.getText().toString().trim(),"wallet",
                            sharedPreferenceUtils.getName());
                   
                }else{
                    amount.setError("enter amount");
                }
//                Intent intent=new Intent(getActivity(),PaymentActivity.class);
//                getActivity().startActivity(intent);
            }
        });
        rootview.findViewById(R.id.add_money_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addLayout.setVisibility(View.VISIBLE);

            }
        });
        return rootview;
    }




}
