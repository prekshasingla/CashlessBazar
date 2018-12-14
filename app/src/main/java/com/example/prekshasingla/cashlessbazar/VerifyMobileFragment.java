package com.example.prekshasingla.cashlessbazar;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class VerifyMobileFragment extends Fragment {

    ProgressDialog dialog;
    private String otp;
    String email;
    String firstname;
    String socialSite;

    public VerifyMobileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_verify_mobile, container, false);
        rootview.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please wait");

        final Bundle args = getArguments();
        email = args.getString("email");
        firstname = args.getString("firstname");
        socialSite = args.getString("social_site");

        final AppCompatEditText mobile = rootview.findViewById(R.id.mobile);
        final Button verify = rootview.findViewById(R.id.verify);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mobile.getText().toString().trim().equals("")) {
                    mobile.setError("Enter number");
                    return;
                }
                final String PHONE_PATTERN = "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}$";
                Pattern pattern = Pattern.compile(PHONE_PATTERN);
                Matcher matcher = pattern.matcher(mobile.getText().toString().trim());
                if (!matcher.matches()) {
                    mobile.setError("Invalid number");
                    return;
                }
                if (otp == null)
                    sendOTP(mobile.getText().toString());

                else
                    authenticateUser(mobile.getText().toString());
//                tokenRequest(args.getString("email"),args.getString("firstname"),
//                        args.getString("social_site"),mobile.getText().toString().trim());
            }
        });

        return rootview;
    }

    private void sendOTP(final String mobile) {
        dialog.show();
        otp = getRandomNumberString();
        SharedPreferenceUtils.getInstance(getActivity()).setOTP(otp);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("203.212.70.200")
                .appendPath("smpp")
                .appendPath("sendsms")
                .appendQueryParameter("username", "barterapi")
                .appendQueryParameter("password", "barterapi123")
                .appendQueryParameter("to", mobile)
                .appendQueryParameter("from", "BARTER")
                .appendQueryParameter("text", "Hi! Thanks for registering with Cashless Bazar. Your OTP is " + otp);

        String myUrl = builder.build().toString();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, myUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        authenticateUser(mobile);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "There may be some error. Please try later", Toast.LENGTH_SHORT).show();
                    }
                }) {
//
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void authenticateUser(final String mobile) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_enter_otp, null);
        final AppCompatEditText otpField = dialogView.findViewById(R.id.otp_field);
        builder.setView(dialogView)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (otpField.getText().toString().length() < 6) {
                            otpField.setError("Enter 6 digit OTP");
                        } else {
                            if (otpField.getText().toString().trim().equals(otp)) {
                                tokenRequest(mobile);
                            } else {
                                Toast.makeText(getActivity(), "Invalid OTP", Toast.LENGTH_SHORT).show();
                                authenticateUser(mobile);
                            }
                        }
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        builder.create();
        builder.show();
    }

    private void tokenRequest(final String mobile) {
        dialog.show();

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://api2.cashlessbazar.com/token",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response != null && !response.equals("")) {

                            try {
                                JSONObject tokenResponse = new JSONObject(response);
                                String token = tokenResponse.getString("access_token");
                                if (token != null) {
                                    socialRegister(token, mobile);
                                } else {
                                    dialog.dismiss();

                                    Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();
                        }

                        // Do something with the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Login Failed", Toast.LENGTH_SHORT).show();
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


    public void socialRegister(final String token, final String mobile) {

//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlSocialRegister,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject loginResponse = new JSONObject(response);
                            if (loginResponse.getString("resultType").equalsIgnoreCase("success")) {

                                JSONObject data = loginResponse.getJSONObject("data");
                                SharedPreferenceUtils.getInstance(getContext()).setCId(data.getInt("regno"));
                                SharedPreferenceUtils.getInstance(getContext()).setName(data.getString("firstname")
                                        + data.getString("lastname"));
                                SharedPreferenceUtils.getInstance(getContext()).setEmail(data.getString("email"));
                                SharedPreferenceUtils.getInstance(getContext()).setMobile(data.getString("mobile"));
                                SharedPreferenceUtils.getInstance(getContext()).setUsername(data.getString("username"));
                                SharedPreferenceUtils.getInstance(getContext()).setType(data.getString("type"));
                                SharedPreferenceUtils.getInstance(getContext()).setAddress(data.getString("address"));
                                SharedPreferenceUtils.getInstance(getContext()).setWalletPin(data.getInt("walletpin"));

//                                JSONObject walletObject = customerObject.getJSONObject("wallet");
//                                SharedPreferenceUtils.getInstance(getContext()).setCBTPBalance(walletObject.getInt("CBTP_Balance"));
//                                SharedPreferenceUtils.getInstance(getContext()).setRewardBalance(walletObject.getInt("Reward_Balance"));

                                dialog.dismiss();

                                Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT).show();

                                Intent intent=new Intent(getActivity(),MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                getActivity().startActivity(intent);


                            } else {
                                dialog.dismiss();
                                Toast.makeText(getActivity(), "Login Failed. Try Again", Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Login Failed. Try Again", Toast.LENGTH_SHORT).show();
                        }

                        // Do something with the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        // Handle error
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("mobile", mobile);
                params.put("socialsite", socialSite + "");
                params.put("firstname",firstname);
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

    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }
}
