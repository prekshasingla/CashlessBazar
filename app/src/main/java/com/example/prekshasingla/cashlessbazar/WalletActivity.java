package com.example.prekshasingla.cashlessbazar;

import android.app.Activity;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import instamojo.library.InstamojoPay;
import instamojo.library.InstapayListener;

public class WalletActivity extends AppCompatActivity {
    InstapayListener listener;
    String paymentReqId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
    }
    public void callInstamojoPay(String email, String phone, String amount, String purpose, String buyername,final String paymentReqId1) {
        final Activity activity = this;
        InstamojoPay instamojoPay = new InstamojoPay();
        IntentFilter filter = new IntentFilter("ai.devsupport.instamojo");
        registerReceiver(instamojoPay, filter);
        JSONObject pay = new JSONObject();
        try {
            pay.put("email", email);
            pay.put("phone", phone);
            pay.put("purpose", purpose);
            pay.put("amount", amount);
            pay.put("name", buyername);
            pay.put("send_sms", false);
            pay.put("send_email", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        paymentReqId=paymentReqId1;
        initListener();
        instamojoPay.start(activity, pay, listener);
    }
    private void initListener() {
        listener = new InstapayListener() {
            @Override
            public void onSuccess(String response) {


                    if(response.contains("success")){
                        String[] data=response.split(":");
                        String orderId= (data[1].split("="))[1];
                        String txnId= (data[2].split("="))[1];
                        String paymentId= (data[3].split("="))[1];
                        tokenRequest(orderId,txnId,paymentId,paymentReqId);

                    }
                    else {
                        Toast.makeText(WalletActivity.this, "Payment Failed. Try again later", Toast.LENGTH_SHORT).show();
                    }


            }

            @Override
            public void onFailure(int code, String reason) {
                Toast.makeText(WalletActivity.this, "Failed: " + reason, Toast.LENGTH_LONG)
                        .show();
            }
        };
    }
    public void tokenRequest(final String orderId, final String txnId, final String payementId, final String paymentReqId){
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
                                    updateWallet( orderId, txnId, payementId,token, paymentReqId);
                                else
                                    Toast.makeText(WalletActivity.this,"Could not connect, please try again later",Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                            Toast.makeText(WalletActivity.this,"Could not connect, please try again later",Toast.LENGTH_SHORT).show();

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


            public String getBodyContentType()
            {
                return "application/x-www-form-urlencoded";
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
    private void updateWallet(final String orderId, final String txnId, final String payementId, final String token, final String paymentReqId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlAddFund,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject loginResponse=new JSONObject(response);

                            if(loginResponse.getString("resultType").equalsIgnoreCase("success")){
                                JSONObject wallet= loginResponse.getJSONObject("wallet");
//                               android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getApplicationContext());
//                                    View dialogView = getLayoutInflater().inflate(R.layout.success_dialog_layout, null);
//                                    builder.setView(dialogView)
//                                            .setCancelable(false);
//
//
//                                    final android.support.v7.app.AlertDialog alertDialog = builder.create();
//                                    TextView text = dialogView.findViewById(R.id.success_desc);
//                                    text.setText("Funds Added successfully");
//                                    TextView ok = dialogView.findViewById(R.id.ok);
//                                    ok.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            alertDialog.cancel();
//                                            onBackPressed();
//                                        }
//                                    });
//                                    builder.show();

                            }
                            else{
                                Toast.makeText(WalletActivity.this, "There is some error. Please try again.", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finally {
                           onBackPressed();

                        }
                        // Do something with the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WalletActivity.this, "There is some error. Please try again.", Toast.LENGTH_LONG).show();

                        // Handle error
                    }
                })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("payment_request_id",paymentReqId);
                params.put("instaorderId",orderId);
//                params.put("txnId",txnId);
                params.put("instapaymentId",payementId);

                return params;
            }

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

//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                0,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
