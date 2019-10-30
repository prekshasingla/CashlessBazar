package com.example.prekshasingla.cashlessbazar;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import instamojo.library.InstapayListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class WalletFragment extends Fragment {

    NavController navController;
    int pagenumber;

    int TRANSACTION_HISTORY=1;
    int WALLET_BALANCE=2;
    List<TransactionHistoryItem> transactionHistoryItems;

    RecyclerView mTransactionHistoryRecyclerView;
    TransactionHistoryAdapter mTransactionHistoryAdapter;
    SharedPreferenceUtils sharedPreferenceUtils;
    TextView walletBalance;



    InstapayListener listener;
    public WalletFragment() {
        pagenumber=1;
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview= inflater.inflate(R.layout.fragment_wallet, container, false);
        rootview.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        transactionHistoryItems=new ArrayList<>();
        mTransactionHistoryRecyclerView=(RecyclerView) rootview.findViewById(R.id.transaction_history_recycler);

        mTransactionHistoryAdapter=new TransactionHistoryAdapter(transactionHistoryItems, getActivity());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mTransactionHistoryRecyclerView.setLayoutManager(mLayoutManager);
        mTransactionHistoryRecyclerView.setAdapter(mTransactionHistoryAdapter);

        tokenRequest(WALLET_BALANCE);
        tokenRequest(TRANSACTION_HISTORY);

        sharedPreferenceUtils=SharedPreferenceUtils.getInstance(getActivity());
        walletBalance= rootview.findViewById(R.id.wallet_balance);
        final AppCompatEditText amount=rootview.findViewById(R.id.amount);

       // walletBalance.setText(SharedPreferenceUtils.getInstance(getActivity()).getCBTPBalance()+"");

        navController = Navigation.findNavController(getActivity(), R.id.fragment);


        rootview.findViewById(R.id.add_money_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navController.navigate(R.id.addFundFragment);



            }
        });
        return rootview;
    }



    public void tokenRequest(final int type){
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

                                    if(type==1)
                                        getTransactionHistory(token,Configuration.urlWalletHistory+"regno="+SharedPreferenceUtils.getInstance(getActivity()).getCId()+"&pagenumber="+pagenumber);
                                    else if(type==2)
                                        getCurrentBalance(token);
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
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }


    public void getTransactionHistory(final String token, String url){
//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject loginResponse=new JSONObject(response);
                            if(loginResponse.get("data")!=null){
                                JSONArray itemsJson=loginResponse.getJSONArray("data");
                                for(int i=0;i<itemsJson.length();i++){
                                    TransactionHistoryItem item= new TransactionHistoryItem();
                                    JSONObject itemsObject=itemsJson.getJSONObject(i);
                                    item.setSno(itemsObject.getInt("sno"));
                                    item.setDate(itemsObject.getString("date"));
                                    item.setCredit(itemsObject.getDouble("credit"));
                                    item.setDebit(itemsObject.getDouble("debit"));
                                    item.setDescription(itemsObject.getString("description"));
                                    transactionHistoryItems.add(item);

                                }

                                    mTransactionHistoryAdapter.notifyDataSetChanged();


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
                    }
                })
        {


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
                                    JSONObject wallet=data.getJSONObject("wallet");
                                    walletBalance.setText(wallet.getString("CBTP_Balance"));

                            } else {
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
                        builder.setMessage("something went wrong, please try again");
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
                  params.put("Regno", SharedPreferenceUtils.getInstance(getActivity()).getCId()+"");

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
