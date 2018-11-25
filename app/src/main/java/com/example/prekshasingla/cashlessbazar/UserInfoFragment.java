package com.example.prekshasingla.cashlessbazar;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
    TextView textViewError;


    public UserInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_user_info, container, false);
        rootView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getActivity(), R.id.fragment).navigateUp();
            }
        });

        final Bundle args=getArguments();
        linearLayoutError=rootView.findViewById(R.id.ll_error);
        textViewError=rootView.findViewById(R.id.text_error);
        linearLayoutPin=rootView.findViewById(R.id.ll_pin);


        textViewName=rootView.findViewById(R.id.user_name);
        textViewMobile=rootView.findViewById(R.id.user_mobile);
      //  textViewBalance.setText(SharedPreferenceUtils.getInstance(getActivity()).getCBTPBalance()+"");

        final AppCompatEditText amount=rootView.findViewById(R.id.amount);
        final AppCompatEditText pin=rootView.findViewById(R.id.pin);
        TextView textViewError=rootView.findViewById(R.id.text_error);

        TextView confirm=rootView.findViewById(R.id.confirm);

        if(args.getString("screenName").equals("null")) {
            linearLayoutPin.setVisibility(View.GONE);
            linearLayoutError.setVisibility(View.GONE);
            amount.setVisibility(View.VISIBLE);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!amount.getText().toString().trim().equals("")) {
                        dialog = new ProgressDialog(getActivity());
                        dialog.setMessage("Please Wait");
                        dialog.setCancelable(false);
                        dialog.show();

                        tokenRequest(amount.getText().toString().trim(), args.getString("cId"),null,null,null);
                    } else {
                        amount.setError("Enter Amount");
                    }
                }
            });

            textViewName.setText(args.getString("firstName"));
            textViewMobile.setText(args.getString("phone"));

        }

        else{

            linearLayoutPin.setVisibility(View.VISIBLE);
            linearLayoutError.setVisibility(View.GONE);
            amount.setVisibility(View.GONE);

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!pin.getText().toString().trim().equals("")) {
                        dialog = new ProgressDialog(getActivity());
                        dialog.setMessage("Please Wait");
                        dialog.setCancelable(false);
                        dialog.show();


                        tokenRequest(args.getString("amount"),args.getString("recRegNo"),args.getString("sendRegNo"),args.getString("request_id"),pin.getText().toString().trim());
                    } else {
                        pin.setError("Please enter a valid pin");
                    }
                }
            });

        }


        return rootView;
    }


    public void tokenRequest(final String amount, final String cId, final String sendRegNo,final String requestId,final String pin){
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

                                    if(sendRegNo.equals(null)) {

                                        proceedTransfer(amount, cId, token);
                                    }
                                    else{
                                    collectPayment(amount,cId,sendRegNo,requestId,token,pin);
                                    }
                                else
                                    Toast.makeText(getActivity(),"Could not connect, please try again later",Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        else
                            Toast.makeText(getActivity(),"Could not connect, please try again later",Toast.LENGTH_SHORT).show();

                        // Do something with the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),"Could not connect, please try again later",Toast.LENGTH_SHORT).show();

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
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }


    private void proceedTransfer(final String amount, final String cId,final String token) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlWalletTransfer,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject responseObject=new JSONObject(response);
                                if(responseObject.getInt("status_code")==200){
                                    JSONObject wallet= responseObject.getJSONObject("wallet");
                                    SharedPreferenceUtils.getInstance(getActivity()).setCBTPBalance(Float.parseFloat(wallet.get("CBTP_Balance").toString()));
                                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                                    builder.setMessage("Success");
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
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Sender_regno", SharedPreferenceUtils.getInstance(getActivity()).getCId() + "");
                params.put("Receiver_regno", cId);
                params.put("Receiver_mobile","null");
                params.put("Amount", amount);

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


    private void collectPayment(final String amount,final String recId, final String sendId,final String requestId,final String token, final String pin) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlCollectPayment,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject responseObject=new JSONObject(response);
                                if(responseObject.getInt("status_code")==200){
                                    JSONObject data= responseObject.getJSONObject("data");
                                    if(data.getString("message").equals("payment successful")){
                                        SharedPreferenceUtils.getInstance(getActivity()).setCBTPBalance(SharedPreferenceUtils.getInstance(getActivity()).getCBTPBalance()+Float.parseFloat(data.get("amount").toString()));
                                    }
                                    SharedPreferenceUtils.getInstance(getActivity());
                                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                                    builder.setMessage("Success");
                                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            getActivity().onBackPressed();
                                        }
                                    });

                                    builder.create();
                                    builder.show();
                                }
                                else if(responseObject.getInt("status_code")==306){
                                    linearLayoutError.setVisibility(View.VISIBLE);
                                    textViewError.setText("*"+responseObject.getString("status_txt"));
                                    linearLayoutPin.setVisibility(View.GONE);

                                }
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
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("sender_regno", sendId);
                params.put("receiver_regno", recId);
                params.put("amount", amount);
                params.put("payment_request_id",requestId);
                params.put("user_pin",pin);

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
