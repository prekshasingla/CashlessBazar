package com.example.prekshasingla.cashlessbazar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class ProductDetailFragment extends Fragment {

    private String itemID;
    private Product product;

    TextView nameView, mrpView, cbtpView, descriptionView, about;
    ImageView imgView;
    LinearLayout descLayout;
    ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product_detail, container, false);
        rootView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        itemID = getActivity().getIntent().getStringExtra("item_id");
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please Wait");
        dialog.show();

        nameView = rootView.findViewById(R.id.product_name);
        mrpView = rootView.findViewById(R.id.product_mrp);
        cbtpView = rootView.findViewById(R.id.product_cbtp);
        imgView = rootView.findViewById(R.id.product_image);
        descriptionView = rootView.findViewById(R.id.short_desc);
        about = rootView.findViewById(R.id.about);

        rootView.findViewById(R.id.add_cart_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPreferenceUtils.getInstance(getActivity()).getName() != null) {
                    tokenRequest(1);
                } else {
                    Intent intent = new Intent(getActivity(), LoginSignupActivity.class);
                    startActivity(intent);
                }

            }
        });
        tokenRequest(0);

        rootView.findViewById(R.id.cart_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPreferenceUtils.getInstance(getActivity()).getName() != null) {
                    NavController controller=Navigation.findNavController(getActivity(),R.id.fragment);
                    controller.navigate(R.id.cartFragment);
                } else {
                    Intent intent = new Intent(getActivity(), LoginSignupActivity.class);
                    startActivity(intent);
                }

            }
        });

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
        descriptionView.setText(product.getShortDesc());
        about.setText(product.getFullDesc());

    }

    public void tokenRequest(final int code) {
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
                                    if (code == 0)
                                        getProduct(token);
                                    else
                                        addToCart(token);

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

    private void addToCart(final String token) {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlCartAdd,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            if (responseObject.getString("resultType").equalsIgnoreCase("success")) {
                                JSONObject data = responseObject.getJSONObject("data");
                                JSONArray cart = data.getJSONArray("cart");
                                String cookieId = cart.getJSONObject(0).getString("cookiesId");
                                SharedPreferenceUtils.getInstance(getActivity()).setCartCookiesId(cookieId);
                                NavController controller=Navigation.findNavController(getActivity(),R.id.fragment);
                                controller.navigate(R.id.cartFragment);
                            } else {
                                Toast.makeText(getActivity(), responseObject.getString("message"), Toast.LENGTH_SHORT).show();
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
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (SharedPreferenceUtils.getInstance(getActivity()).getCartCookiesId() != null)
                    params.put("cookiesId", SharedPreferenceUtils.getInstance(getActivity()).getCartCookiesId());
                else {
//                    params.put("cookiesId", "0");
                }

                params.put("productid", itemID);
                params.put("qunatity", "1");
                params.put("type", "add");
                params.put("amount", product.getCbtp() + "");
                params.put("regno", SharedPreferenceUtils.getInstance(getActivity()).getCId() + "");
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
                                product.setShortDesc(data.getString("shortdescription"));
                                product.setFullDesc(data.getString("fulldescription"));

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
