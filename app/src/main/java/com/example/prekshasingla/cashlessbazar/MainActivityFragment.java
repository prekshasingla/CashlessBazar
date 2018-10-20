package com.example.prekshasingla.cashlessbazar;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {


    NavController navController;
    ViewPager bannerViewpager;
    HomeBannerPagerAdapter homeBannerPagerAdapter;
    int page = 0;


    static private List<String> mBannerImages;


    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBannerImages = new ArrayList<>();
        mBannerImages.add("https://cashlessbazar.com/images/newsletter/TIECONBANNER.png");
        mBannerImages.add("https://cashlessbazar.com/images/newsletter/TIECONBANNER.png");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        navController = Navigation.findNavController(getActivity(), R.id.fragment);

        TextView textView=rootView.findViewById(R.id.textview);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.loginSignupActivity);
                }
        });

        bannerViewpager = rootView.findViewById(R.id.trending_viewpager);
        homeBannerPagerAdapter=new HomeBannerPagerAdapter(getActivity().getSupportFragmentManager(),mBannerImages ,getActivity());

        bannerViewpager.setAdapter(homeBannerPagerAdapter);

         final Handler handler=new Handler();
         final  int delay = 5000; //milliseconds


        Runnable runnable = new Runnable() {
            public void run() {
                if (homeBannerPagerAdapter.getCount() == page) {
                    page = 0;
                } else {
                    page++;
                }
                bannerViewpager.setCurrentItem(page, true);
                handler.postDelayed(this, delay);
            }
        };
        runnable.run();
        return rootView;
    }
}
