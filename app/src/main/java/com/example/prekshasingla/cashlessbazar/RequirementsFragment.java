package com.example.prekshasingla.cashlessbazar;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequirementsFragment extends Fragment {


    RecyclerView mRecentOrdersRecyclerView;
    RecentOrdersRecyclerAdapter mRecentOrdersAdapter;
    List<RecentOrdersItem> recentOrdersItems;
    NavController navController;


    public RequirementsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.fragment_requirements, container, false);
        rootView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        navController = Navigation.findNavController(getActivity(), R.id.fragment);

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

        Button postButton=rootView.findViewById(R.id.button_post_requirement);
        Button sellButton=rootView.findViewById(R.id.button_sell_product);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.postRequirementFragment);

            }
        });

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.sellProductFragment);

            }
        });


        return rootView;
    }

}
