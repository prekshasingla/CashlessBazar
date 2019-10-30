package com.example.prekshasingla.cashlessbazar;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class OnBoardingFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;


    public OnBoardingFragment() {
        // Required empty public constructor
    }


    public static OnBoardingFragment newInstance(String param1) {
        OnBoardingFragment fragment = new OnBoardingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_on_boarding, container, false);
        TextView text1= (TextView) rootView.findViewById(R.id.onboarding_text1);
        TextView text2= (TextView) rootView.findViewById(R.id.onboarding_text2);
        TextView text3= (TextView) rootView.findViewById(R.id.onboarding_text3);
        TextView text4= (TextView) rootView.findViewById(R.id.onboarding_text4);

        ImageView image1= (ImageView) rootView.findViewById(R.id.onboarding_image1);
        if(getArguments().get(ARG_PARAM1).equals("screen1")){
            text1.setText("BARTER SYSTEM");
            text2.setText("Exchange your existing goods for something you need most.");
            text3.setText("SKIP");
            text4.setText("List your products online and trade them against CB Trade Points and use them for making purchases.");
            rootView.setBackgroundColor(getResources().getColor(R.color.intro1));
            image1.setBackgroundResource(R.drawable.barter_image);
        }else if(getArguments().get(ARG_PARAM1).equals("screen2")){
            text1.setText("GIFT VOUCHERS");
//            text2.setText("Live stream your event with ease");
            text2.setVisibility(View.INVISIBLE);
            text3.setText("SKIP");
            text4.setText("Gift your young ones a token of love with gift vouchers of leading brands.");
            rootView.setBackgroundColor(getResources().getColor(R.color.intro2));
            image1.setBackgroundResource(R.drawable.gift_voucher);
        }else if(getArguments().get(ARG_PARAM1).equals("screen3")){
            text1.setText("LOYALTY CARD");
            text2.setText("Get rewards on your purchases");
            text3.setText("SKIP");
            text4.setText("Earn upto 10x rewards on making purchases via cashlessbazar.com");
            rootView.setBackgroundColor(getResources().getColor(R.color.intro3));
            image1.setBackgroundResource(R.drawable.loyalty);
        }else if(getArguments().get(ARG_PARAM1).equals("screen4")){
            text1.setText("PAY WITH US AND EARN REWARDS");
            text2.setText("Pay and recieve payment by scanning QR code");
            text3.setText("SKIP");
            text4.setText("Run your own reward wallet for your customers. Contact for integrating your own reward system");
            rootView.setBackgroundColor(getResources().getColor(R.color.intro4));
            image1.setBackgroundResource(R.drawable.rewards);
        }else if(getArguments().get(ARG_PARAM1).equals("screen5")){
            text1.setText("ONLINE / RETAIL SHOPPING");
            text2.setText("One stop solution for online and retail shopping. Just pay with your cashlessbazar.com wallet and explore the possibilities in your hand");
            text3.setText("SKIP");
            text4.setText("Run your own reward wallet for your customers. Contact for integrating your own reward system");
            rootView.setBackgroundColor(getResources().getColor(R.color.intro5));
            image1.setBackgroundResource(R.drawable.shopping);
        }else{
            text1.setText("FOOD COURT");
            text2.setText("Your cbtp wallet is one stop solution to our offline food courts");
            text3.setText("CONTINUE");
            rootView.setBackgroundColor(getResources().getColor(R.color.intro6));
            image1.setBackgroundResource(R.drawable.food_court);
        }
        text3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
        return rootView;
    }

}
