package com.example.prekshasingla.cashlessbazar;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import androidx.navigation.Navigation;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFundFragment extends Fragment {

    AppCompatEditText amount;
    SharedPreferenceUtils sharedPreferenceUtils;

    public AddFundFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_add_fund, container, false);
        rootview.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getActivity(), R.id.fragment).navigateUp();
            }
        });
        sharedPreferenceUtils = SharedPreferenceUtils.getInstance(getActivity());

        amount = rootview.findViewById(R.id.amount);


        Button addMoney = rootview.findViewById(R.id.add_money);
        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!amount.getText().toString().trim().equals("")) {
                    tokenRequest();
                } else {
                    amount.setError("enter amount");
                }
//                Intent intent=new Intent(getActivity(),PaymentActivity.class);
//                getActivity().startActivity(intent);
            }
        });

        return rootview;
    }

    public void tokenRequest() {
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

                                    requestAddFund(token);

                                else
                                    Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Cannot Find User. Try Again");
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Cannot Find User. Try Again");
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

    private void requestAddFund(final String token) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlRequestAddFund,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject responseObject = new JSONObject(response);
                            if (responseObject.getString("resultType").equalsIgnoreCase("success")) {

                                JSONObject data = responseObject.getJSONObject("data");

                                ((WalletActivity) getActivity()).callInstamojoPay(sharedPreferenceUtils.getEmail(),
                                        sharedPreferenceUtils.getPhone(), amount.getText().toString().trim(), "Indplas_event",
                                        sharedPreferenceUtils.getName(), data.getString("request_id"));

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("Cannot Find User. Try Again");
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Cannot Find User. Try Again");
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

                params.put("name", sharedPreferenceUtils.getName());
                params.put("email", sharedPreferenceUtils.getEmail());
                params.put("phone", sharedPreferenceUtils.getPhone());
                params.put("amount", amount.getText().toString().trim());
                if (Config.isDebugMode)
                    params.put("mode", "TEST");
                else
                    params.put("mode", "LIVE");

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
