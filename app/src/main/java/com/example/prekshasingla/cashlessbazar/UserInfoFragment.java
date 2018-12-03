package com.example.prekshasingla.cashlessbazar;


import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class UserInfoFragment extends Fragment {


    String userName;
    String userMobile;
    TextView textViewName;
    TextView textViewMobile;
    private TextView textViewBalance;
    ProgressDialog dialog;
    LinearLayout linearLayoutError;
    LinearLayout linearLayoutPin;
    LinearLayout linearLayoutAmount;
    TextView textViewError;
    TextView confirm;

    boolean flag = true;


    public UserInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_info, container, false);
        rootView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getActivity(), R.id.fragment).navigateUp();
            }
        });

        final Bundle args = getArguments();
        linearLayoutError = rootView.findViewById(R.id.ll_error);
        textViewError = rootView.findViewById(R.id.text_error);
        linearLayoutPin = rootView.findViewById(R.id.ll_pin);
        linearLayoutAmount = rootView.findViewById(R.id.ll_amount);

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);


        textViewName = rootView.findViewById(R.id.user_name);
        textViewMobile = rootView.findViewById(R.id.user_mobile);
        //  textViewBalance.setText(SharedPreferenceUtils.getInstance(getActivity()).getCBTPBalance()+"");

        final AppCompatEditText amount = rootView.findViewById(R.id.amount);
        final AppCompatEditText pin = rootView.findViewById(R.id.pin);
        TextView textViewError = rootView.findViewById(R.id.text_error);

        confirm = rootView.findViewById(R.id.confirm);

        if (args.getString("screenName").equals("payment")) {
            linearLayoutPin.setVisibility(View.GONE);
            linearLayoutError.setVisibility(View.GONE);
            amount.setVisibility(View.VISIBLE);
            linearLayoutAmount.setVisibility(View.VISIBLE);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!amount.getText().toString().trim().equals("") && Float.parseFloat(amount.getText().toString().trim()) != 0) {
                        dialog.show();

                        if (flag) {
                            flag = false;
                            tokenRequest(amount.getText().toString().trim(), args.getString("cId"),
                                    null, null, null, args.getString("screenName"));
                        }
                    } else {
                        amount.setError("Enter Amount");
                    }
                }
            });

            textViewName.setText(args.getString("name"));
            textViewMobile.setText(args.getString("phone"));

        } else {

            if (args.getString("status") != null && !args.getString("status").equals("0")) {
                linearLayoutError.setVisibility(View.VISIBLE);
                textViewError.setText("*" + args.getString("status"));
                linearLayoutPin.setVisibility(View.GONE);
                amount.setVisibility(View.GONE);
                confirm.setVisibility(View.GONE);
                LinearLayout linearLayoutUserInfo = rootView.findViewById(R.id.ll_user_info);
                linearLayoutUserInfo.setVisibility(View.GONE);


            } else {
                linearLayoutPin.setVisibility(View.VISIBLE);
                linearLayoutError.setVisibility(View.GONE);
                amount.setVisibility(View.GONE);


                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (flag) {
                            flag=false;
                            if (!pin.getText().toString().trim().equals("") && Float.parseFloat(pin.getText().toString().trim()) != 0) {
                                dialog = new ProgressDialog(getActivity());
                                dialog.setMessage("Please Wait");
                                dialog.setCancelable(false);
                                dialog.show();

                                tokenRequest(args.getString("amount"), args.getString("recRegNo"),
                                        args.getString("sendRegNo"), args.getString("request_id"),
                                        pin.getText().toString().trim(), args.getString("screenName"));
                            } else {
                                pin.setError("Please enter a valid pin");
                            }
                        }
                    }
                });
                textViewName.setText(args.getString("name"));
                textViewMobile.setText(args.getString("phone"));
            }

        }


        return rootView;
    }


    public void tokenRequest(final String amount, final String recRegNo, final String sendRegNo, final String requestId, final String pin, final String screenName) {
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

                                    if (screenName.equals("payment")) {

//                                        proceedTransfer(amount, cId, token);
                                        requestPayment(recRegNo,amount,token);
                                    } else {
                                        collectPayment(amount, recRegNo, sendRegNo, requestId, token, pin);
                                    }
                                else {
                                    flag = true;
                                    Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                flag = true;
                                Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();

                            }

                        } else {
                            Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();
                            flag = true;
                        }

                        // Do something with the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        flag = true;
                        Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();

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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void requestPayment(final String regNo, final String amount, final String token) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlRequestTransfer,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    dialog.dismiss();
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            if (responseObject.getInt("status_code") == 200) {
                                if(responseObject.getString("status_txt").equals("success")) {

                                    JSONObject customer = responseObject.getJSONObject("data");
                                    proceedTransfer(amount,regNo,token,customer.getString("request_id"));

                                }
                                else {
                                    flag=true;
                                    String status = responseObject.getString("status_txt");
                                    Toast.makeText(getActivity(), status, Toast.LENGTH_SHORT).show();

                                }
                            } else {
                                flag=true;
                                Toast.makeText(getActivity(), "Some error occurred, Try again later.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            flag=true;
                            Toast.makeText(getActivity(), "Some error occurred, Try again later.", Toast.LENGTH_SHORT).show();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        dialog.dismiss();
                        flag=true;
                        Toast.makeText(getActivity(), "Some error occurred, Try again later.", Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("sender_regno", SharedPreferenceUtils.getInstance(getActivity()).getCId()+"");
                params.put("receiver_regno", regNo);
                params.put("receiver_mobile","");
                params.put("amount",amount);
                params.put("mode","transfer");
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
    private void proceedTransfer(final String amount, final String recRegNo, final String token,final String requestId) {

        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlTransferPayment,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject responseObject = new JSONObject(response);
                                if (responseObject.getInt("status_code") == 200) {
                                    sendNotification("Money Transfered Successfully");
                                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                                    builder.setMessage("Success");
                                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
//                                            getActivity().onBackPressed();

                                        }
                                    })
                                            .setCancelable(false);

                                    builder.create();
                                    builder.show();
                                } else {
                                    flag = true;
                                    Toast.makeText(getActivity(), responseObject.getString("status_txt"), Toast.LENGTH_SHORT).show();

                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                flag = true;
                            }

                        } else {
                            Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();
                            flag = true;

                        }

                        // Do something with the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        flag = true;
                        dialog.dismiss();
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("payment_request_id", requestId);
                params.put("amount", amount);
                params.put("sender_regno", SharedPreferenceUtils.getInstance(getActivity()).getCId()+"");
                params.put("receiver_regno", recRegNo);
                params.put("mode", "transfer");

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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }


    private void collectPayment(final String amount, final String recId, final String sendId, final String requestId, final String token, final String pin) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlCollectPayment,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject responseObject = new JSONObject(response);
                                if (responseObject.getInt("status_code") == 200) {
                                    if (responseObject.getString("status_txt").equals("success")) {
                                        JSONObject data = responseObject.getJSONObject("data");
//                                        if (data.getString("message").equals("payment successful")) {
                                            Toast.makeText(getActivity(), data.getString("message"), Toast.LENGTH_SHORT).show();

//                                            SharedPreferenceUtils.getInstance(getActivity()).setCBTPBalance(SharedPreferenceUtils.getInstance(getActivity()).getCBTPBalance() + Float.parseFloat(data.get("amount").toString()));
//                                        }
                                        sendNotification("Payment Recieved successfully");
                                        SharedPreferenceUtils.getInstance(getActivity());
                                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                                        builder.setMessage("Success");
                                        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
//                                                getActivity().onBackPressed();
                                            }
                                        }).setCancelable(false);

                                        builder.create();
                                        builder.show();
                                    } else {
                                        flag=true;
                                        Toast.makeText(getActivity(), responseObject.getString("status_txt"), Toast.LENGTH_SHORT).show();

                                    }
                                } else {
                                    flag=true;
                                    Toast.makeText(getActivity(), responseObject.getString("status_txt"), Toast.LENGTH_SHORT).show();

                                }

                            } catch (JSONException e) {
                                flag=true;
                                e.printStackTrace();
                            }

                        } else {
                            flag=true;
                            Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();
                        }

                        // Do something with the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        flag=true;
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("sender_regno", sendId);
                params.put("receiver_regno", recId);
                params.put("amount", amount);
                params.put("payment_request_id", requestId);
                params.put("user_pin", pin);
                params.put("mode", "collect");

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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.payments_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getActivity(), channelId)
                        .setSmallIcon(R.drawable.cb_logo_icon)
                        .setContentTitle("CBTP Wallet")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Payment Transactions",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
