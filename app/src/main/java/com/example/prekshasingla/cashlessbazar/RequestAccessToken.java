package com.example.prekshasingla.cashlessbazar;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RequestAccessToken {

    String accessToken;
    public String getToken(final Context context){


        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://api2.cashlessbazar.com/token",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response!=null && !response.equals("")){

                            try {
                                JSONObject tokenResponse=new JSONObject(response);
                                String token= tokenResponse.getString("access_token");
                                if(token != null)
                                { accessToken= token;
                                }

                                else {
                                    Toast.makeText(context, "Could not connect, please try again later", Toast.LENGTH_SHORT).show();
                                    accessToken=null;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                accessToken=null;
                            }
                        }
                        else {
                            Toast.makeText(context, "Could not connect, please try again later", Toast.LENGTH_SHORT).show();
                            accessToken=null;
                        }

                        // Do something with the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        accessToken=null;
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
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
        return null;
    }
}
