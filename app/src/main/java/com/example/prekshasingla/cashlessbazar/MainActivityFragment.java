package com.example.prekshasingla.cashlessbazar;

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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.navigation.NavController;
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
    List<Product> featuredItems;
    RecyclerView mBestSellingRecyclerView;
    RecyclerViewAdapter mBestSellingAdapter;
    List<Product> bestSellingItems;
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

        LinearLayout topHomePay=rootView.findViewById(R.id.top_home_pay);
        topHomePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(getActivity(), QRActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout topHomeWallet=rootView.findViewById(R.id.top_home_wallet);
        topHomeWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WalletActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout topHomeEvents=rootView.findViewById(R.id.top_home_events);
        topHomeEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EventsActivity.class);
                startActivity(intent);
            }
        });



        featuredItems=new ArrayList<>();
        bestSellingItems=new ArrayList<>();



        bannerViewpager = rootView.findViewById(R.id.trending_viewpager);
        homeBannerPagerAdapter=new HomeBannerPagerAdapter(getActivity().getSupportFragmentManager(),mBannerImages ,getActivity());

        bannerViewpager.setAdapter(homeBannerPagerAdapter);

         final Handler handler=new Handler();
         final  int delay = 5000; //milliseconds

        tokenRequest(1); //1 for featured, 2 for best selling, 3 for most selling


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

        Product item=new Product();
        item.setImg("https://cashlessbazar.com/images/homepage2018/block-02-gift-vouchers-125percent.jpg");
        item.setName("Gift Vouchers");
        item.setPrice(450f);
        item.setDesc("This is the description of the item");
        featuredItems.add(item);
        bestSellingItems.add(item);

        Product item1=new Product();
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


    public void tokenRequest(final int reqCode){
//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://api2.cashlessbazar.com/token",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response!=null && !response.equals("")){

                            try {
                                JSONObject tokenResponse=new JSONObject(response);
                                String token= tokenResponse.getString("access_token");
                                if(token != null)

                                    if(reqCode==1)
                                       getRequest(token,Configration.urlFeatured );
                                    else if(reqCode==2)
                                        getRequest(token,Configration.urlBestSelling);
                                    else if(reqCode==3)
                                        getRequest(token,Configration.urlMostSelling);

                                else
                                    Toast.makeText(getActivity(),"Could not connect, please try again later",Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                            Toast.makeText(getActivity(),"Could not connect, please try again later",Toast.LENGTH_SHORT).show();

                        // Do something with the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username","developer");
                params.put("password","SPleYwIt");
                params.put("grant_type", "password");

                return params;
            }

//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String,String> params = new HashMap<String, String>();
////                params.put("Content-Type","application/x-www-form-urlencoded");
//                //params.put("Authorization","bearer kZnREUlqOg4CSoqmN-fvrR53Gyp6JGUG9VQh-w4J9fu0ZwAVSdsJNkzA00bw-ZsOWX6ZTuEOxCGoGqxEJz_xk-PXvZ3UnI0zEmjCbmkvsA8cyFzvtRVtpbFFNwo5SWh85D1MtVHIaKBWzJur14LQjCuFW2WX87B-UsyDZbxmgMSdxJbqgiD3cVKipsMThQJDtM6ZM1-V1OM-rL75O0t6r3Ew36Ve6HkebmcKKyrssRJeP4rgyD9m3prKJs5lr_pFTRhkYq2hi07pcIjwCet1wRe9NQo4k8xp9FF5n4U-1gScdP4JXPoikp4HG9QAPrm5");
//                return params;
//            }

            public String getBodyContentType()
            {
                return "application/x-www-form-urlencoded"+"PageNumber=1&PageSize=10";
            }

        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }


    public void getRequest(final String token, String url){
//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject loginResponse=new JSONObject(response);
                            if(loginResponse.get("data")!=null){
                                Toast.makeText(getActivity(),"fetched",Toast.LENGTH_SHORT).show();

                            }
                            else
                                Toast.makeText(getActivity(),"Unable to fetch",Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // Do something with the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                })
        {
//            @Override
//            protected Map<String,String> getParams(){
//                Map<String,String> params = new HashMap<String, String>();
//                params.put("LoginId","8447707717");
//                params.put("password","111111");
//
//                return params;
//            }



            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("Authorization","bearer "+token);
                return params;
            }

            public String getBodyContentType()
            {
                return "application/x-www-form-urlencoded";
            }

        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }



}

