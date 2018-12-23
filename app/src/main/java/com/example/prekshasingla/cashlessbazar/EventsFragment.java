package com.example.prekshasingla.cashlessbazar;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;


public class EventsFragment extends Fragment {


    public EventsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_events, container, false);
        rootView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        ImageView eventImage1=rootView.findViewById(R.id.event_image1);
        ImageView eventImage2=rootView.findViewById(R.id.event_image2);
        ImageView eventImage3=rootView.findViewById(R.id.event_image3);

        TextView bookTickets1= rootView.findViewById(R.id.book1);
        TextView bookTickets2= rootView.findViewById(R.id.book2);
        TextView bookTickets3= rootView.findViewById(R.id.book3);

        bookTickets1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args=new Bundle();
                args.putString("event_name","DILBAR NIGHT 2018");
                args.putString("event_url","https://cashlessbazar.com/NYE/Dilbarnight");
                NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
                navController.navigate(R.id.eventDetailFragment,args);
            }
        });
        bookTickets2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args=new Bundle();
                args.putString("event_name","UNITED -  THE GREAT GATSBY PARTY 2018");
                args.putString("event_url", "https://cashlessbazar.com/unitedwestand");
                NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
                navController.navigate(R.id.eventDetailFragment,args);
            }
        });
        bookTickets3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args=new Bundle();
                args.putString("event_name","SOCIAL ELEGANZA 2018");
                args.putString("event_url", "https://cashlessbazar.com/social");
                NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
                navController.navigate(R.id.eventDetailFragment,args);
            }
        });


        Picasso.with(getActivity()).load("https://cashlessbazar.com/images/home-slider-banners/cbbanner1.png").into(eventImage1);
        Picasso.with(getActivity()).load("https://cashlessbazar.com/images/home-slider-banners/cb2.png").into(eventImage2);
        Picasso.with(getActivity()).load("https://www.cashlessbazar.com/images/Newsletter/Socialbanner.JPG").into(eventImage3);


        return rootView;
    }


}
