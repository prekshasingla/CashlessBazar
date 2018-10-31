package com.example.prekshasingla.cashlessbazar;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class WithdrawFundsFragment extends Fragment {


    public WithdrawFundsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_withdraw_funds, container, false);
        final LinearLayout addLayout=rootView.findViewById(R.id.add_container);
        Button addMoney=rootView.findViewById(R.id.withdraw_money);
        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),PaymentActivity.class);
                getActivity().startActivity(intent);
            }
        });
        rootView.findViewById(R.id.add_money_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addLayout.setVisibility(View.VISIBLE);

            }
        });

        return  rootView;
    }

}
