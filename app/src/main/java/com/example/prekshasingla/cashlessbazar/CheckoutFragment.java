package com.example.prekshasingla.cashlessbazar;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class CheckoutFragment extends Fragment {


    private TextView walletBalance, addFundsText, makePaymentBtn;
    ProgressBar walletProgress;
    String totalPrice;
    ProgressDialog dialog;

    public CheckoutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        walletProgress.setVisibility(View.VISIBLE);
        addFundsText.setVisibility(View.GONE);
        tokenRequest(1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_checkout, container, false);

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);

        walletBalance = rootview.findViewById(R.id.wallet_balance);
        walletProgress = rootview.findViewById(R.id.balance_progress);
        walletProgress.setVisibility(View.VISIBLE);
        walletProgress.setIndeterminate(true);

        addFundsText = rootview.findViewById(R.id.add_funds_text);
        addFundsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WalletActivity.class);
                getActivity().startActivity(intent);
            }
        });
        Bundle args = getArguments();
        totalPrice = args.getString("totalPrice");
        makePaymentBtn = rootview.findViewById(R.id.payment_btn);
        makePaymentBtn.setText("Make Payment ( " + getActivity().getResources().getString(R.string.rupee)
                + totalPrice + " )");
        makePaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tokenRequest(2);
                dialog.show();
            }
        });
        tokenRequest(1);
        return rootview;
    }

    public void tokenRequest(final int type) {
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
                                    if (type == 1)
                                        getCurrentBalance(token);
                                    else
                                        makePayment(token);

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
                        dialog.dismiss();
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

    private void makePayment(final String token) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlMakePayment,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            if (responseObject.getString("resultType").equalsIgnoreCase("success")) {
                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                                View dialogView = getLayoutInflater().inflate(R.layout.success_dialog_layout, null);
                                builder.setView(dialogView)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                getActivity().onBackPressed();
                                            }
                                        });


                                final android.support.v7.app.AlertDialog alertDialog = builder.create();
                                TextView text = dialogView.findViewById(R.id.success_desc);
                                text.setText("Your purchase is successfull");
//
                                builder.show();

                            } else {
                                Toast.makeText(getActivity(), responseObject.getString("message"), Toast.LENGTH_SHORT).show();
                                getActivity().onBackPressed();
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
                        builder.setMessage("There may be some error, try again");
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
                params.put("regno", SharedPreferenceUtils.getInstance(getActivity()).getCId() + "");
                params.put("CookiesId", SharedPreferenceUtils.getInstance(getActivity()).getCartCookiesId() + "");

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

    private void getCurrentBalance(final String token) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlUserInfo,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject responseObject = new JSONObject(response);
                            if (responseObject.getString("resultType").equalsIgnoreCase("success")) {

                                JSONObject data = responseObject.getJSONObject("data");
                                JSONObject wallet = data.getJSONObject("wallet");
                                walletBalance.setText(wallet.getString("CBTP_Balance"));
                                walletProgress.setVisibility(View.GONE);
                                if (Float.parseFloat(wallet.getString("CBTP_Balance")) < Float.parseFloat(totalPrice)) {
                                    addFundsText.setVisibility(View.VISIBLE);
                                    makePaymentBtn.setBackgroundColor(getActivity().getResources().getColor(R.color.separator_line_grey));
                                    makePaymentBtn.setEnabled(false);
                                    walletBalance.setVisibility(View.VISIBLE);
                                } else {
                                    addFundsText.setVisibility(View.GONE);
                                    makePaymentBtn.setBackgroundColor(getActivity().getResources().getColor(R.color.intro1));
                                    makePaymentBtn.setEnabled(true);
                                }

                            } else {
                                Toast.makeText(getActivity(), responseObject.getString("message"), Toast.LENGTH_SHORT).show();
                                getActivity().onBackPressed();
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Cannot update wallet information, try again");
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
                params.put("Regno", SharedPreferenceUtils.getInstance(getActivity()).getCId() + "");

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
}
