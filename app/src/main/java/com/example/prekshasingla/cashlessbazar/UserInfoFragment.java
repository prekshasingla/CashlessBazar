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


        textViewName=rootView.findViewById(R.id.user_name);
        textViewMobile=rootView.findViewById(R.id.user_mobile);
        textViewBalance=rootView.findViewById(R.id.balance);
        textViewBalance.setText(SharedPreferenceUtils.getInstance(getActivity()).getCBTPBalance()+"");

        final AppCompatEditText amount=rootView.findViewById(R.id.amount);

        TextView confirm=rootView.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!amount.getText().toString().trim().equals("")){
                   dialog=new ProgressDialog(getActivity());
                   dialog.setMessage("Please Wait");
                   dialog.setCancelable(false);
                   dialog.show();

                    tokenRequest(amount.getText().toString().trim(),args.getString("cId"));
                }else{
                    amount.setError("Enter Amount");
                }
            }
        });

        textViewName.setText(args.getString("name"));
        textViewMobile.setText(args.getString("phone"));


        return rootView;
    }


    public void tokenRequest(final String amount, final String cId){
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

                                    proceedTransfer(amount,cId,token);
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
                params.put("senderRegno", SharedPreferenceUtils.getInstance(getActivity()).getCId() + "");
                params.put("receiverRegno", cId);
                params.put("amount", amount);

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
