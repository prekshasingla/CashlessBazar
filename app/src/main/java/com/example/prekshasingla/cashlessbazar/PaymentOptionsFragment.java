package com.example.prekshasingla.cashlessbazar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.instamojo.android.activities.PaymentActivity;
import com.instamojo.android.callbacks.JusPayRequestCallback;
import com.instamojo.android.helpers.Constants;
import com.instamojo.android.models.Card;
import com.instamojo.android.models.Errors;
import com.instamojo.android.models.Order;
import com.instamojo.android.network.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class PaymentOptionsFragment extends Fragment {


    private boolean cardFlag = true;

    public PaymentOptionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_payment_options, container, false);
        rootView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        final LinearLayout cardLayout = rootView.findViewById(R.id.card_layout);
        final View card = getLayoutInflater().inflate(R.layout.card_layout, null, false);

        final EditText cardNumber = card.findViewById(R.id.card_number);
        final EditText cardCVV = card.findViewById(R.id.card_cvv);
        final EditText cardExpiry = card.findViewById(R.id.card_expiry);
        final EditText cardName = card.findViewById(R.id.card_name);
        Button proceed = card.findViewById(R.id.card_layout_proceed);
        rootView.findViewById(R.id.card_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardFlag) {
                    cardLayout.addView(card);
                    cardFlag = false;
                }
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Card card = new Card();
                card.setCardNumber(cardNumber.getText().toString());
                card.setDate(cardExpiry.getText().toString());
                card.setCardHolderName(cardName.getText().toString());
                card.setCvv(cardCVV.getText().toString());

                //Validate the card here
//                if (!isValidCard(card)) {
//                    return;
//                }
                fetchTokenAndTransactionID();
                //Get order details form Juspay
//                proceedWithCard(order, card);
            }
        });
        return rootView;
    }

    private void fetchTokenAndTransactionID() {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, Configration.urlPayFetchToken,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject loginResponse=new JSONObject(response);


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
                params.put("Authorization","bearer "+"FueJXpiZo-wp57ztDPsY7QEVo-J46YZiNi6GJRwypJYyqoSN8hsvCfbDrHmz6lTDmPeErUoJJvbpqitnfHENrnY4WXdRbMJggF9M9ADbdxVInASWuqNo0-fhgk9eJwQnJAVmlUoec8gu0KWh_S1mtzqnEnNjtE7vEE5pyX5um5q0Kj5Qff7xINn4RBgsHie8mhW3WWRCS8xkGF4adZHrotN56boWwXVEwIcL_goMLyRg0L_qrlSO8nNvclNS2QYUi73k8o1kVs6HHBMKCIV3tGi306BWzx-4o7UUsdQtTcyAfQBLTQ2TnHPnfj9O3pWv");
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }

            public String getBodyContentType()
            {
                return "application/x-www-form-urlencoded";
            }

        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

    }

    private boolean isValidCard(Card card) {
        if (!card.isCardValid()) {

            if (!card.isCardNameValid()) {
                showErrorToast("Card Holders Name is invalid");
            }

            if (!card.isCardNumberValid()) {
                showErrorToast("Card Number is invalid");
            }

            if (!card.isDateValid()) {
                showErrorToast("Expiry date is invalid");
            }

            if (!card.isCVVValid()) {
                showErrorToast("CVV is invalid");
            }

            return false;
        }

        return true;


    }
    private void proceedWithCard(Order order, Card card) {
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                getString(com.instamojo.android.R.string.please_wait), true, false);
        Request request = new Request(order, card, new JusPayRequestCallback() {
            @Override
            public void onFinish(final Bundle bundle, final Exception error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (error != null) {
                            if (error instanceof Errors.ConnectionError) {
                                Log.e("App", "No internet");
                            } else if (error instanceof Errors.ServerError) {
                                Log.e("App", "Server Error. trya again");
                            } else {
                                Log.e("App", error.getMessage());
                            }
                            return;
                        }
                        startPaymentActivity(bundle);
                    }
                });
            }
        });
        request.execute();
    }
    private void startPaymentActivity(Bundle bundle) {
        // Start the payment activity
        //Do not change this unless you know what you are doing
        Intent intent = new Intent(getActivity(), PaymentActivity.class);
//        intent.putExtras(getIntent());
        intent.putExtra(Constants.PAYMENT_BUNDLE, bundle);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }
    private void showErrorToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
