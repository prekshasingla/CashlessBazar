package com.example.prekshasingla.cashlessbazar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.navigation.Navigation;


public class RechargeFragment2 extends Fragment {


    String type = null;
    ProgressDialog dialog;
    private List<String> operatorIdList;
    private List<String> operatorList;
    private AlertDialog.Builder operatorBuilder;
    
    AppCompatEditText operatorField,numberField,amountField;
    TextView numberText;

    private String operatorCode;


    public RechargeFragment2() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString("type");
        }
        operatorList=new ArrayList<>();
        operatorIdList=new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview= inflater.inflate(R.layout.fragment_recharge_fragment2, container, false);
        rootview.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getActivity(), R.id.fragment).navigateUp();
            }
        });

        dialog=new ProgressDialog(getActivity());
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);
        
        operatorField=rootview.findViewById(R.id.recharge_operator);
        numberField=rootview.findViewById(R.id.recharge_number);
        amountField=rootview.findViewById(R.id.recharge_amount);
        numberText=rootview.findViewById(R.id.recharge_number_text);

        Button pay=rootview.findViewById(R.id.recharge_pay);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    tokenRequest(type);
                }
            }
        });

        
        if(type.equals("mobile")){
            tokenRequest("operators");
            numberText.setText("Mobile Number");
        }
        if(type.equals("dth")){
            tokenRequest("operators");
            numberText.setText("DTH Number");
        }
        if(type.equals("datacard")){
            tokenRequest("operators");
            numberText.setText("Card Number");
        }
        
        operatorField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                operatorBuilder.show();
            }
        });
        

        return rootview;
    }

    private boolean validate() {
        if(numberField.getText().toString().equals("")){
            numberField.setError("Enter number");
            return false;
        }
        if(type.equals("mobile"))
        if(numberField.getText().toString().length()<10){
            numberField.setError("Invalid number");
            return false;
        }
        if(operatorField.getText().toString().equals("")){
            operatorField.setError("Enter operator");
            return false;
        }
        if(amountField.getText().toString().equals("")){
            amountField.setError("Enter amount");
            return false;
        }

        return true;
    }

    public void tokenRequest(final String type1) {
//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();
        dialog.show();
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

                                    if (type1.equals("operators") )
                                        getOperators(token);
                                else if(type1.equals("mobile")|| type1.equals("dth")||type1.equals("datacard"))
                                    proceedRecharge(token);


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

//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String,String> params = new HashMap<String, String>();
////                params.put("Content-Type","application/x-www-form-urlencoded");
//                //params.put("Authorization","bearer kZnREUlqOg4CSoqmN-fvrR53Gyp6JGUG9VQh-w4J9fu0ZwAVSdsJNkzA00bw-ZsOWX6ZTuEOxCGoGqxEJz_xk-PXvZ3UnI0zEmjCbmkvsA8cyFzvtRVtpbFFNwo5SWh85D1MtVHIaKBWzJur14LQjCuFW2WX87B-UsyDZbxmgMSdxJbqgiD3cVKipsMThQJDtM6ZM1-V1OM-rL75O0t6r3Ew36Ve6HkebmcKKyrssRJeP4rgyD9m3prKJs5lr_pFTRhkYq2hi07pcIjwCet1wRe9NQo4k8xp9FF5n4U-1gScdP4JXPoikp4HG9QAPrm5");
//                return params;
//            }

            public String getBodyContentType() {
                return "application/json";
            }

        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void proceedRecharge(final String token) {
        dialog.show();
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("Regno", SharedPreferenceUtils.getInstance(getActivity()).getCId());
            if(Config.isDebugMode)
            jsonBody.put("mode", 0);
            else
                jsonBody.put("mode", 1);

            jsonBody.put("service", numberField.getText().toString());
            jsonBody.put("operatorcode", operatorCode);
            jsonBody.put("amount", amountField.getText().toString());
            if(type.equals("mobile"))
                jsonBody.put("type", "PREPAID_MOBILE");
            if(type.equals("dth"))
                jsonBody.put("type", "DTH_RECHARGE");
            if(type.equals("datacard"))
                jsonBody.put("type", "DATACARD_RECHARGE");


            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlRecharge,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dialog.dismiss();
                            try {
                                JSONObject loginResponse = new JSONObject(response);
                                if (loginResponse.getString("resultType").equalsIgnoreCase("success")) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle("Success");
                                    builder.setCancelable(false);
                                    builder.setMessage("Congrats! Your recharge done successfully");
                                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            getActivity().onBackPressed();
                                        }
                                    });

                                    builder.create();
                                    builder.show();
                                } else {
                                    Toast.makeText(getActivity(), loginResponse.getString("message"), Toast.LENGTH_SHORT).show();

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
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Some error occurred, please try again later", Toast.LENGTH_SHORT).show();

                        }
                    }) {


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/x-www-form-urlencoded");
                    params.put("Authorization", "bearer " + token);
                    return params;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }

                public String getBodyContentType() {
                    return "application/json";
                }

            };
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getOperators(final String token) {

        dialog.show();
        operatorList.clear();
        operatorIdList.clear();
        try {


            String serviceType="";
            if(type.equals("mobile")){
                serviceType="PREPAID_MOBILE";
            }
            if(type.equals("dth")){
                serviceType="DTH_RECHARGE";
            }
            if(type.equals("datacard")){
                serviceType="DATACARD_RECHARGE";
            }
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Configuration.urlOperatorList
                                            +"service="+serviceType,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject loginResponse = new JSONObject(response);
                                if (loginResponse.getString("resultType").equalsIgnoreCase("success")) {

                                    JSONArray jsonArray = loginResponse.getJSONArray("data");

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        operatorList.add(jsonObject.getString("OperatorName"));
                                        operatorIdList.add(jsonObject.getString("OperatorCode"));
                                    }


                                    operatorBuilder = new AlertDialog.Builder(getActivity());
                                    operatorBuilder.setTitle("Select operator");

                                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.select_dialog_post_requirement);
                                    arrayAdapter.addAll(operatorList);

                                    operatorBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                                    operatorBuilder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String strName = arrayAdapter.getItem(which);
                                            operatorField.setText(strName);
                                            operatorField.setError(null);
                                            operatorCode = operatorIdList.get(which);
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.dismiss();
//
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(), "Some error occurred, please try again later", Toast.LENGTH_SHORT).show();

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
                            dialog.dismiss();
                        }
                    }) {


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/x-www-form-urlencoded");
                    params.put("Authorization", "bearer " + token);
                    return params;
                }


//                public String getBodyContentType() {
//                    return "application/x-www-form-urlencoded";
//                }

            };
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
