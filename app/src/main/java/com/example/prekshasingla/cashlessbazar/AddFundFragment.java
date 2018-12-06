package com.example.prekshasingla.cashlessbazar;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.navigation.Navigation;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFundFragment extends Fragment {


    public AddFundFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview= inflater.inflate(R.layout.fragment_add_fund, container, false);
        rootview.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getActivity(), R.id.fragment).navigateUp();
            }
        });
        final SharedPreferenceUtils sharedPreferenceUtils=SharedPreferenceUtils.getInstance(getActivity());

        final AppCompatEditText amount=rootview.findViewById(R.id.amount);

        TextView walletBalance= rootview.findViewById(R.id.wallet_balance);
        walletBalance.setText(SharedPreferenceUtils.getInstance(getActivity()).getCBTPBalance()+"");

        Button addMoney=rootview.findViewById(R.id.add_money);
        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!amount.getText().toString().trim().equals(""))
                {
                    ((WalletActivity)getActivity()).callInstamojoPay(sharedPreferenceUtils.getEmail(),
                            sharedPreferenceUtils.getPhone(),amount.getText().toString().trim(),"Indplas_event",
                            sharedPreferenceUtils.getName());

                }
                else{
                    amount.setError("enter amount");
                }
//                Intent intent=new Intent(getActivity(),PaymentActivity.class);
//                getActivity().startActivity(intent);
            }
        });

        return rootview;
    }

}
