package com.example.prekshasingla.cashlessbazar;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


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
        ImageView eventImage=rootView.findViewById(R.id.event_image);

        Picasso.with(getActivity()).load("https://storage.googleapis.com/ehimages/2017/4/5/" +
                "img_08e5e2af7d75af167105bce7d3793e98_1491386523571_processed_original.jpg").into(eventImage);
        return rootView;
    }


}
