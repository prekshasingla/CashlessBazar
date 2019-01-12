package com.example.prekshasingla.cashlessbazar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;


public class RequirementDetailFragment extends Fragment {

    ProgressBar progressBar;
    LinearLayout layout;
    TextView title, desc, mrp, price, unit, min_qty, max_qty, locationInfo, shippingInfo, screenName,
            contactButton, mrpText, priceText, shippingText;
    LinearLayout locationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_requirement_detail, container, false);
        rootview.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        progressBar = rootview.findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        layout = rootview.findViewById(R.id.layout);
        layout.setVisibility(View.GONE);

        screenName = rootview.findViewById(R.id.screen_name);
        title = rootview.findViewById(R.id.title);
        desc = rootview.findViewById(R.id.desc);
        mrp = rootview.findViewById(R.id.mrp);
        price = rootview.findViewById(R.id.price);
        unit = rootview.findViewById(R.id.unit);
        min_qty = rootview.findViewById(R.id.qty_min);
        max_qty = rootview.findViewById(R.id.qty_max);
        locationInfo = rootview.findViewById(R.id.location_info);
        shippingInfo = rootview.findViewById(R.id.shipping_info);
        contactButton = rootview.findViewById(R.id.contact_btn);
        mrpText = rootview.findViewById(R.id.mrp_text);
        priceText = rootview.findViewById(R.id.price_text);
        locationView = rootview.findViewById(R.id.location_view);
        shippingText = rootview.findViewById(R.id.shipping_text);


        Intent intent = getActivity().getIntent();
        String adid = intent.getStringExtra("adid");
        tokenRequest(adid);


        return rootview;
    }

    public void tokenRequest(final String adid) {
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
                                    getProduct(token, adid);

                                else
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


            public String getBodyContentType() {
                return "application/json";
            }

        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void getProduct(final String token, String adid) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Configuration.urlRequirementProductDetail + adid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject loginResponse = new JSONObject(response);
                            if (loginResponse.getString("resultType").equalsIgnoreCase("success")) {
                                JSONObject data = loginResponse.getJSONObject("data");
                                updateUI(data);
                            } else {
                                Toast.makeText(getActivity(), loginResponse.getString("message"), Toast.LENGTH_SHORT).show();
                                getActivity().onBackPressed();
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


        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

    }

    private void updateUI(JSONObject data) {

        progressBar.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);
        try {
            final Bundle args = new Bundle();
            title.setText(data.getString("title"));
            desc.setText(data.getString("description"));


            unit.setText(data.getString("unitName"));


            if (data.getString("serviceName").equalsIgnoreCase("offer")) {
                screenName.setText("Offering");
                contactButton.setText("Contact Seller");
                mrp.setText("\u20B9 " + data.getString("mrp"));
                mrp.setPaintFlags(mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mrpText.setVisibility(View.VISIBLE);
                priceText.setText("Offer Price");
                price.setText("\u20B9 " + data.getString("offerprice"));
                min_qty.setText("Minimum : " + data.getString("minimumquantity"));
                max_qty.setText("Maximum : " + data.getString("maximumquantity"));
                locationInfo.setText(data.getString("locationInfo"));
                shippingInfo.setText(data.getString("shippingInfo"));

                args.putString("service", "offering");
                args.putString("adid", data.getString("adid"));
            } else {
                screenName.setText("Looking");
                contactButton.setText("Contact Buyer");
                mrp.setVisibility(View.GONE);
                mrpText.setVisibility(View.GONE);
                priceText.setText("Budget : ");
                price.setText(data.getString("bugetPrice"));
                min_qty.setText("Minimum : " + data.getString("quantity"));
                max_qty.setVisibility(View.GONE);
                locationView.setVisibility(View.GONE);
                shippingText.setText("Delivery Preference");
                shippingInfo.setText(data.getString("deliveryPreference"));
                args.putString("service", "looking");
                args.putString("adid", data.getString("adid"));


            }
            contactButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavController controller = Navigation.findNavController(getActivity(), R.id.fragment);
                    controller.navigate(R.id.requirementContactFragment, args);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
