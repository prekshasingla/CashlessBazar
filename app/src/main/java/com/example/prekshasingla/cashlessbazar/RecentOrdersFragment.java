package com.example.prekshasingla.cashlessbazar;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecentOrdersFragment extends Fragment {


    RecyclerView mRecentOrdersRecyclerView;
    RecentOrdersRecyclerAdapter mRecentOrdersAdapter;
    List<RecentOrdersItem> recentOrdersItems;
    NavController navController;

    public RecentOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_recent_orders, container, false);
        navController = Navigation.findNavController(getActivity(), R.id.fragment);

        Intent intent=getActivity().getIntent();
        String screenName=intent.getStringExtra("screen");
        TextView screenNameTextView=(TextView)rootView.findViewById(R.id.screen_name);
        screenNameTextView.setText(screenName);
        recentOrdersItems=new ArrayList<>();

        mRecentOrdersRecyclerView= (RecyclerView) rootView.findViewById(R.id.recent_order_recycler);

        RecentOrdersItem item=new RecentOrdersItem();
        item.setImageURL("https://cashlessbazar.com/productImage/29631811.jpg");
        item.setName("Pizza Margharita");
        item.setQuantity(1);
        item.setPrice(600f);
        item.setCustomerName("John Phillipe");
        item.setAddress("Pitampura, Delhi");
        item.setStatus("New Order");

        recentOrdersItems.add(item);
        recentOrdersItems.add(item);
        recentOrdersItems.add(item);

        mRecentOrdersAdapter=new RecentOrdersRecyclerAdapter(recentOrdersItems, getActivity(),navController);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecentOrdersRecyclerView.setLayoutManager(mLayoutManager);
        mRecentOrdersRecyclerView.setAdapter(mRecentOrdersAdapter);

        return rootView;
    }

}
