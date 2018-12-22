package com.example.prekshasingla.cashlessbazar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import com.instamojo.android.activities.PaymentDetailsActivity;
import com.instamojo.android.callbacks.JusPayRequestCallback;
import com.instamojo.android.callbacks.OrderRequestCallBack;
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
    private ProgressDialog dialog;
    private Card cardDetails;

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
        final EditText cardExpiryMonth = card.findViewById(R.id.card_expiry_month);
        final EditText cardExpiryYear = card.findViewById(R.id.card_expiry_year);
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
                dialog = new ProgressDialog(getActivity());
                dialog.setIndeterminate(true);
                dialog.setMessage("please wait...");
                dialog.setCancelable(false);
                dialog.show();
                cardDetails = new Card();
                cardDetails.setCardNumber(cardNumber.getText().toString());
                cardDetails.setDate(cardExpiryMonth.getText().toString() + "/" + cardExpiryYear.getText().toString());
                cardDetails.setCardHolderName(cardName.getText().toString());
                cardDetails.setCvv(cardCVV.getText().toString());

                //Validate the card here
                if (!isValidCard(cardDetails)) {
                    dialog.dismiss();
                    return;
                } else {
                    dialog.dismiss();
                    SharedPreferenceUtils sharedPreferenceUtils=SharedPreferenceUtils.getInstance(getActivity());
                    ((com.example.prekshasingla.cashlessbazar.PaymentActivity) getActivity()).callInstamojoPay(
                            sharedPreferenceUtils.getEmail(),sharedPreferenceUtils.getPhone(),"500",
                            "wallet",sharedPreferenceUtils.getName()
                    );
//                    tokenRequest();
//                fetchTokenAndTransactionID();
                    //Get order details form Juspay
//                proceedWithCard(order, card);
                }
            }
        });
        return rootView;
    }

    private void fetchTokenAndTransactionID(final String token) {

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, Configuration.urlPayFetchToken,
                new Response.Listener<String>() {
                    private String transactionID;
                    private String accessToken;

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            accessToken = responseObject.getString("access_token");
                            transactionID = responseObject.getString("transactionId");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // Do something with the response
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                createOrder(accessToken, transactionID);
                            }
                        });

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Some error occured. Try again later.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }) {
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
                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("Authorization", "bearer " + token);
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }

            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

    }

    private void createOrder(String accessToken, String transactionID) {

        String name = SharedPreferenceUtils.getInstance(getActivity()).getName();
        final String email = SharedPreferenceUtils.getInstance(getActivity()).getEmail();
        String phone = SharedPreferenceUtils.getInstance(getActivity()).getPhone();
        String amount = "500";// amountBox.getText().toString();
        String description = "hi";//descriptionBox.getText().toString();

        amount = String.format("%.2f", Double.parseDouble(amount));
        //Create the Order
        Order order = new Order(accessToken, transactionID, name, email, phone, amount, description);
        Request request = new Request(order, new OrderRequestCallBack() {
            @Override
            public void onFinish(final Order order, final Exception error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (error != null) {
                            if (error instanceof Errors.ConnectionError) {
                                showErrorToast("No internet connection");
                            } else if (error instanceof Errors.ServerError) {
                                showErrorToast("Server Error. Try again");
                            } else if (error instanceof Errors.AuthenticationError) {
                                showErrorToast("Access token is invalid or expired. Please Update the token!!");
                            } else if (error instanceof Errors.ValidationError) {
                                // Cast object to validation to pinpoint the issue
                                Errors.ValidationError validationError = (Errors.ValidationError) error;

                                if (!validationError.isValidTransactionID()) {
                                    showErrorToast("Transaction ID is not Unique");
                                    return;
                                }

                                if (!validationError.isValidRedirectURL()) {
                                    showErrorToast("Redirect url is invalid");
                                    return;
                                }

                                if (!validationError.isValidWebhook()) {
                                    showErrorToast("Webhook url is invalid");
                                    return;
                                }


                                if (!validationError.isValidAmount()) {
                                    showErrorToast("Amount is either less than Rs.9 or has more than two decimal places");
                                    return;
                                }


                            } else {
                                showErrorToast(error.getMessage());
                            }
                            return;
                        }
//
                        proceedWithCard(order, cardDetails);
//                        startCustomUI(order);
//        startPreCreatedUI(order);
                    }
                });
            }
        });
        request.execute();
    }

    private void startPreCreatedUI(Order order) {
        //Using Pre created UI
        Intent intent = new Intent(getActivity(), PaymentDetailsActivity.class);
        intent.putExtra(Constants.ORDER, order);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    private boolean isValidCard(Card card) {
        if (TextUtils.isEmpty(card.getCardHolderName())) {
            Toast.makeText(getActivity(), "Enter Name on Card", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(card.getCardNumber())) {
            Toast.makeText(getActivity(), "Enter Card Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(card.getCvv())) {
            Toast.makeText(getActivity(), "Enter CVV number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(card.getMonth()) || TextUtils.isEmpty(card.getYear())) {
            Toast.makeText(getActivity(), "Enter Expiry Date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!card.isCardValid()) {

            if (card.getCardNumber().length() == 1) {
                showErrorToast("Card Number is invalid");
                return false;
            }
            if (!card.isCardNameValid()) {
                showErrorToast("Card Holders Name is invalid");
                return false;
            }

            if (!card.isCardNumberValid()) {
                showErrorToast("Card Number is invalid");
                return false;
            }

            if (!card.isDateValid()) {
                showErrorToast("Expiry date is invalid");
                return false;
            }

            if (!card.isCVVValid()) {
                showErrorToast("CVV is invalid");
                return false;
            }


        }

        return true;


    }

    private void proceedWithCard(Order order, Card card) {
        dialog.dismiss();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //send back the result to Main activity
        if (requestCode == Constants.REQUEST_CODE && data != null) {
            String orderID = data.getStringExtra(Constants.ORDER_ID);
            String transactionID = data.getStringExtra(Constants.TRANSACTION_ID);
            String paymentID = data.getStringExtra(Constants.PAYMENT_ID);

            // Check transactionID, orderID, and orderID for null before using them to check the Payment status.
            if (transactionID != null || paymentID != null) {
//                checkPaymentStatus(transactionID, orderID);
            } else {
                dialog.dismiss();
                showErrorToast("Oops!! Payment was cancelled");
            }
        }
    }

    public void tokenRequest() {
//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://api2.cashlessbazar.com/token",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response != null && !response.equals("")) {

                            try {
                                JSONObject tokenResponse = new JSONObject(response);
                                String token = tokenResponse.getString("access_token");
                                if (token != null)
                                    fetchTokenAndTransactionID(token);
                                else {
                                    Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                        // Do something with the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();
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

//    private void checkPaymentStatus(final String transactionID, final String orderID) {
//
//
//        if (dialog != null && !dialog.isShowing()) {
//            dialog.show();
//        }
//
////        showToast("checking transaction status");
//        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, Config.urlPayFetchToken,
//                new Response.Listener<String>() {
//                    private String transactionID;
//                    private String accessToken;
//
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject responseObject = new JSONObject(response);
//                            accessToken = responseObject.getString("access_token");
//                            transactionID = responseObject.getString("transactionId");
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        // Do something with the response
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                createOrder(accessToken, transactionID);
//                            }
//                        });
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getActivity(), "Some error occured. Try again later.", Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
//                    }
//                }) {
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
////                params.put("Content-Type","application/x-www-form-urlencoded");
//                params.put("Authorization", "bearer " + token);
//                params.put("Content-Type", "application/x-www-form-urlencoded");
//                return params;
//            }
//
//            public String getBodyContentType() {
//                return "application/x-www-form-urlencoded";
//            }
//
//        };
//        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
//
//        OkHttpClient client = new OkHttpClient();
//        HttpUrl.Builder builder = getHttpURLBuilder();
//        builder.addPathSegment("status");
//        if (transactionID != null){
//            builder.addQueryParameter("transaction_id", transactionID);
//        } else {
//            builder.addQueryParameter("id", orderID);
//        }
//        builder.addQueryParameter("env", currentEnv.toLowerCase());
//        HttpUrl url = builder.build();
//
//        okhttp3.Request request = new okhttp3.Request.Builder()
//                .url(url)
//                .addHeader("Authorization", "Bearer " + accessToken)
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (dialog != null && dialog.isShowing()) {
//                            dialog.dismiss();
//                        }
//                        showToast("Failed to fetch the Transaction status");
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, okhttp3.Response response) throws IOException {
//                String responseString = response.body().string();
//                response.body().close();
//                String status = null;
//                String paymentID = null;
//                String amount = null;
//                String errorMessage = null;
//
//                try {
//                    JSONObject responseObject = new JSONObject(responseString);
//                    JSONObject payment = responseObject.getJSONArray("payments").getJSONObject(0);
//                    status = payment.getString("status");
//                    paymentID = payment.getString("id");
//                    amount = responseObject.getString("amount");
//
//                } catch (JSONException e) {
//                    errorMessage = "Failed to fetch the Transaction status";
//                }
//
//                final String finalStatus = status;
//                final String finalErrorMessage = errorMessage;
//                final String finalPaymentID = paymentID;
//                final String finalAmount = amount;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (dialog != null && dialog.isShowing()) {
//                            dialog.dismiss();
//                        }
//                        if (finalStatus == null) {
//                            showToast(finalErrorMessage);
//                            return;
//                        }
//
//                        if (!finalStatus.equalsIgnoreCase("successful")) {
//                            showToast("Transaction still pending");
//                            return;
//                        }
//
//                        showToast("Transaction Successful for id - " + finalPaymentID);
//                        refundTheAmount(transactionID, finalAmount);
//                    }
//                });
//            }
//        });
//
//    }

    private void showErrorToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
