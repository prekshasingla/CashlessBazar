package com.example.prekshasingla.cashlessbazar;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {


    NavController navController;
    NavigationView navigationView;
    ViewPager bannerViewpager;
    HomeBannerPagerAdapter homeBannerPagerAdapter;
    int page = 0;
    static private List<String> mBannerImages;
    RecyclerView mFeaturedRecyclerView;
    RecyclerViewAdapter mFeaturedAdapter;
    List<ItemRecyclerView> featuredItems;
    RecyclerView mBestSellingRecyclerView;
    RecyclerViewAdapter mBestSellingAdapter;
    List<ItemRecyclerView> bestSellingItems;
//    NavOptions navOptions;


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

        

//        navigationView.setupWithNavController(navController);


//        navOptions = NavOptions.Builder()
//                .setEnterAnim(R.anim.nav_default_enter_anim)
//                .setExitAnim(R.anim.slide_out_left)
//                .setPopEnterAnim(R.anim.slide_in_left)
//                .setPopExitAnim(R.anim.slide_out_right)
//                .build();


        featuredItems=new ArrayList<>();
        bestSellingItems=new ArrayList<>();

        TextView textView=rootView.findViewById(R.id.textview);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.recentOrdersFragment);
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

        mFeaturedRecyclerView = (RecyclerView) rootView.findViewById(R.id.featured_recycler);
        mBestSellingRecyclerView=(RecyclerView) rootView.findViewById(R.id.best_selling_recycler);

        ItemRecyclerView item=new ItemRecyclerView();
        item.setImg("https://cashlessbazar.com/images/homepage2018/block-02-gift-vouchers-125percent.jpg");
        item.setName("Gift Vouchers");
        item.setPrice(450f);
        item.setDesc("This is the description of the item");
        featuredItems.add(item);
        bestSellingItems.add(item);

        ItemRecyclerView item1=new ItemRecyclerView();
        item1.setImg("https://cashlessbazar.com/images/homepage2018/block-01-mobile-recharge-125percent.jpg");
        item1.setName("Mobile Recharge");
        item.setPrice(450f);
        item.setDesc("This is the description of the item");
        featuredItems.add(item1);
        bestSellingItems.add(item1);

        item.setImg("https://cashlessbazar.com/images/homepage2018/block-02-gift-vouchers-125percent.jpg");
        item.setName("Gift Vouchers");
        item.setPrice(450f);
        item.setDesc("This is the description of the item");
        featuredItems.add(item);
        bestSellingItems.add(item);

        item.setImg("https://cashlessbazar.com/images/homepage2018/block-02-gift-vouchers-125percent.jpg");
        item.setName("Gift Vouchers");
        item.setPrice(450f);
        item.setDesc("This is the description of the item");
        featuredItems.add(item);
        bestSellingItems.add(item);

        mFeaturedAdapter=new RecyclerViewAdapter(featuredItems, getActivity(),navController);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mFeaturedRecyclerView.setLayoutManager(mLayoutManager);
        mFeaturedRecyclerView.setAdapter(mFeaturedAdapter);

        mBestSellingAdapter=new RecyclerViewAdapter(bestSellingItems, getActivity(),navController);
        LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mBestSellingRecyclerView.setLayoutManager(mLayoutManager1);
        mBestSellingRecyclerView.setAdapter(mBestSellingAdapter);

        return rootView;
    }
}
