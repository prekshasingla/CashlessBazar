package com.example.prekshasingla.cashlessbazar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
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
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {


    NavController navController;
    NavigationView navigationView;
    ViewPager bannerViewpager;
    HomeBannerPagerAdapter homeBannerPagerAdapter;
    int page = 0;
    static private List<Banner> mBannerImages;
    RecyclerView mFeaturedRecyclerView;
    RecyclerViewAdapter mFeaturedAdapter;
    List<Product> featuredItems;
    RecyclerView mBestSellingRecyclerView;
    RecyclerViewAdapter mBestSellingAdapter;
    List<Product> bestSellingItems;
    RecyclerView mMostSellingRecyclerView;
    RecyclerViewAdapter mMostSellingAdapter;
    List<Product> mostSellingItems;
    private String firebaseToken = null;
    CirclePageIndicator indicator;
//    NavOptions navOptions;


    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBannerImages = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        navController = Navigation.findNavController(getActivity(), R.id.fragment);

        final SharedPreferenceUtils sharedPreferenceUtils = SharedPreferenceUtils.getInstance(getApplicationContext());

        LinearLayout topHomePay = rootView.findViewById(R.id.top_home_pay);
        topHomePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferenceUtils.getName() != null) {

                    Intent intent = new Intent(getActivity(), QRActivity.class);
                    intent.putExtra("screen", "payment");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), LoginSignupActivity.class);
                    startActivity(intent);
                }
            }
        });
        LinearLayout topHomeWallet = rootView.findViewById(R.id.top_home_wallet);
        topHomeWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferenceUtils.getName() != null) {

                    Intent intent = new Intent(getActivity(), WalletActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), LoginSignupActivity.class);
                    startActivity(intent);
                }
            }
        });
        LinearLayout topHomeEvents = rootView.findViewById(R.id.top_home_events);
        topHomeEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferenceUtils.getName() != null) {

                    Intent intent = new Intent(getActivity(), EventsActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), LoginSignupActivity.class);
                    startActivity(intent);
                }
            }
        });

        LinearLayout topHomeBarter = rootView.findViewById(R.id.top_home_barter);
        topHomeBarter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferenceUtils.getName() != null) {

                    Intent intent = new Intent(getActivity(), AddProductActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), LoginSignupActivity.class);
                    startActivity(intent);
                }
            }
        });


        featuredItems = new ArrayList<>();
        bestSellingItems = new ArrayList<>();
        mostSellingItems = new ArrayList<>();


        bannerViewpager = rootView.findViewById(R.id.trending_viewpager);
        homeBannerPagerAdapter = new HomeBannerPagerAdapter(getActivity().getSupportFragmentManager(), mBannerImages, getActivity());

        bannerViewpager.setAdapter(homeBannerPagerAdapter);
         indicator = (CirclePageIndicator) rootView.findViewById(R.id.indicator);


        final Handler handler = new Handler();
        final int delay = 5000; //milliseconds


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
        mBestSellingRecyclerView = (RecyclerView) rootView.findViewById(R.id.best_selling_recycler);
        mMostSellingRecyclerView = (RecyclerView) rootView.findViewById(R.id.mostselling_recycler);


        mFeaturedAdapter = new RecyclerViewAdapter(featuredItems, getActivity(), navController);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mFeaturedRecyclerView.setLayoutManager(mLayoutManager);
        mFeaturedRecyclerView.setAdapter(mFeaturedAdapter);

        mBestSellingAdapter = new RecyclerViewAdapter(bestSellingItems, getActivity(), navController);
        LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mBestSellingRecyclerView.setLayoutManager(mLayoutManager1);
        mBestSellingRecyclerView.setAdapter(mBestSellingAdapter);

        mMostSellingAdapter = new RecyclerViewAdapter(mostSellingItems, getActivity(), navController);
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mMostSellingRecyclerView.setLayoutManager(mLayoutManager2);
        mMostSellingRecyclerView.setAdapter(mMostSellingAdapter);

        tokenRequest();



        return rootView;
    }




    public void tokenRequest() {
//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://api2.cashlessbazar.com/token",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response != null && !response.equals("")) {

                            try {
                                JSONObject tokenResponse = new JSONObject(response);
                                String token = tokenResponse.getString("access_token");
                                if (token != null)

                                {
                                    getRequest(token, Configuration.urlFeatured + "PageNumber=1&PageSize=10", 1);
                                    getRequest(token, Configuration.urlBestSelling + "PageNumber=1&PageSize=10", 2);
                                    getRequest(token, Configuration.urlMostSelling + "PageNumber=1&PageSize=10", 3);

                                    if (SharedPreferenceUtils.getInstance(getActivity()).getCId() != 0) {
                                        firebaseToken = FirebaseInstanceId.getInstance().getToken();
                                        if (firebaseToken != null) {
                                            updateToken(token);
                                        }
                                    }
                                    getBanners(token);

                                } else
                                    Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else
                            Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();

                        // Do something with the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", "developer");
                params.put("password", "SPleYwIt");
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

            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void getBanners(final String token) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Configuration.urlBannersHome,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject responseObject = new JSONObject(response);
                            if (responseObject.getString("resultType").equalsIgnoreCase("success")) {

                                JSONArray data= responseObject.getJSONArray("data");
                                for(int i=0;i<data.length();i++){
                                    Banner banner= new Banner();
                                    banner.image=((JSONObject)data.get(i)).getString("BannerImage");
                                    banner.url=((JSONObject)data.get(i)).getString("VisitUrl");
                                    banner.name=((JSONObject)data.get(i)).getString("Name");
                                    mBannerImages.add(banner);
                                }
                                homeBannerPagerAdapter.notifyDataSetChanged();
                                indicator.initViewPager(bannerViewpager);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error

                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("Authorization", "bearer " + token);
                return params;
            }

            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void updateToken(final String token) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlUpdateToken,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject responseObject = new JSONObject(response);
                            if (responseObject.getString("resultType").equalsIgnoreCase("success")) {


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("regno", SharedPreferenceUtils.getInstance(getActivity()).getCId() + "");
                params.put("token", firebaseToken);

                return params;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("Authorization", "bearer " + token);
                return params;
            }

            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }


    public void getRequest(final String token, String url, final int reqCode) {
//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject loginResponse = new JSONObject(response);
                            if (loginResponse.get("data") != null) {
                                JSONArray itemsJson = loginResponse.getJSONArray("data");
                                for (int i = 0; i < itemsJson.length(); i++) {
                                    Product item = new Product();
                                    JSONObject itemsObject = itemsJson.getJSONObject(i);
                                    item.setId(itemsObject.getInt("id"));
                                    item.setName(itemsObject.getString("name"));
                                    item.setDesc(itemsObject.getString("description"));
                                    item.setMrp(itemsObject.getDouble("mrp"));
                                    item.setCbtp(itemsObject.getDouble("cbtp"));
                                    item.setProductType(itemsObject.getInt("product_Type"));
                                    item.setProductTypeName(itemsObject.getString("product_type_name"));
                                    item.setImg(itemsObject.getJSONObject("store_img_url").getString("url"));
                                    JSONArray categoryObject = itemsObject.getJSONArray("category_data");
                                    item.setCategoryId(((JSONObject) categoryObject.get(0)).getInt("id"));
                                    item.setCategoryName(((JSONObject) categoryObject.get(0)).getString("name"));
                                    item.setProductMode(itemsObject.getInt("product_mode"));
                                    item.setProductModeName(itemsObject.getString("product_mode_name"));
                                    if (reqCode == 1)
                                        featuredItems.add(item);
                                    else if (reqCode == 2)
                                        bestSellingItems.add(item);
                                    else if (reqCode == 3)
                                        mostSellingItems.add(item);

                                }

                                if (reqCode == 1)
                                    mFeaturedAdapter.notifyDataSetChanged();
                                else if (reqCode == 2)
                                    mBestSellingAdapter.notifyDataSetChanged();
                                else if (reqCode == 3)
                                    mMostSellingAdapter.notifyDataSetChanged();

                            }


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
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("Authorization", "bearer " + token);
                return params;
            }

            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }
public class Banner{
        String name;
        String image;
        String url;
    }

}

