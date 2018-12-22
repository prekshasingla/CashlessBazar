package com.example.prekshasingla.cashlessbazar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.navigation.Navigation;

public class ProductDetailFragment extends Fragment {

    private String itemID;
    private Product product;

    TextView nameView, mrpView, cbtpView, descriptionView;
    ImageView imgView;
    LinearLayout descLayout;
    ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product_detail, container, false);


        Bundle args = getArguments();
        itemID = args.getString("item_id");
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please Wait");
        dialog.show();

        nameView = rootView.findViewById(R.id.product_name);
        mrpView = rootView.findViewById(R.id.product_mrp);
        cbtpView = rootView.findViewById(R.id.product_cbtp);
        imgView = rootView.findViewById(R.id.product_image);
        descriptionView = rootView.findViewById(R.id.product_description);
        descLayout = rootView.findViewById(R.id.description_layout);
        descLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (descriptionView.getVisibility() == View.GONE) {
                    descriptionView.setVisibility(View.VISIBLE);
                } else
                    descriptionView.setVisibility(View.GONE);

            }
        });
        tokenRequest();


        return rootView;
    }

    private void updateUI() {

        nameView.setText(product.getName());
        if (product.getMrp() != 0) {
            mrpView.setText(" \u20B9 " + product.getMrp() + "");
            mrpView.setPaintFlags(mrpView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        cbtpView.setText(" \u20B9 " + product.getCbtp() + "");
        Picasso.with(getActivity()).load(product.getImg()).into(imgView);
        descriptionView.setText(product.getDesc());

    }

    public void tokenRequest() {
//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://api2.cashlessbazar.com/token",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        if (response != null && !response.equals("")) {

                            try {
                                JSONObject tokenResponse = new JSONObject(response);
                                String token = tokenResponse.getString("access_token");
                                if (token != null)

                                    getProduct(token);

                                else
                                    Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("There may be some error");
                            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    getActivity().onBackPressed();
                                }
                            });

                            builder.create();
                            builder.show();
                        }
                        // Do something with the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("There may be some error");
                        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getActivity().onBackPressed();
                            }
                        });

                        builder.create();
                        builder.show();
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
                return "application/x-www-form-urlencoded";
            }

        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void getProduct(final String token) {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Configuration.urlProductDescription + itemID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            if (responseObject.getString("resultType").equalsIgnoreCase("success")) {

                                JSONObject data = responseObject.getJSONObject("data");
                                product = new Product();
                                product.setName(data.getString("name"));
                                product.setMrp(Double.parseDouble(data.getString("mrp")));
                                product.setCbtp(Double.parseDouble(data.getString("cbtp")));
                                JSONArray images = data.getJSONArray("product_image");
                                product.setImg(((JSONObject) images.get(0)).getString("url"));
                                product.setDesc(data.getString("shortdescription"));

                                updateUI();

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("There may be some error");
                                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        getActivity().onBackPressed();
                                    }
                                });

                                builder.create();
                                builder.show();
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
                        dialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("There may be some error");
                        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getActivity().onBackPressed();
                            }
                        });

                        builder.create();
                        builder.show();
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


}
