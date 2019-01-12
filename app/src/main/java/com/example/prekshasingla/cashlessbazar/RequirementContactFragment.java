package com.example.prekshasingla.cashlessbazar;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequirementContactFragment extends Fragment {

    TextView screenName;
    AppCompatEditText expPrice, expQty, remarks;
    Button submit;
    ProgressDialog dialog;

    public RequirementContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_requirement_contact, container, false);
        rootview.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController controller = Navigation.findNavController(getActivity(), R.id.fragment);
                controller.navigateUp();
            }
        });
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);
        screenName = rootview.findViewById(R.id.screen_name);
        expPrice = rootview.findViewById(R.id.requirement_expected_price);
        expQty = rootview.findViewById(R.id.requirement_expected_qty);
        remarks = rootview.findViewById(R.id.requirement_remark);
        submit = rootview.findViewById(R.id.post_button);

        Bundle args = getArguments();
        final String service = args.getString("service");
        final String adid=args.getString("adid");
        if (service.equalsIgnoreCase("looking"))
            screenName.setText("Contact Buyer");
        else
            screenName.setText("Contact Seller");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    tokenRequest(service,adid);
                }
            }
        });
        return rootview;
    }

    private boolean validate() {
        if (expPrice.getText().toString().equals("")) {
            expPrice.setError("Enter Price");
            return false;
        }
        if (expQty.getText().toString().equals("")) {
            expQty.setError("Enter quantity");
            return false;
        }
        if (remarks.getText().toString().equals("")) {
            remarks.setError("Enter Remarks");
            return false;
        }

        return true;
    }

    public void tokenRequest(final String service, final String adid) {
//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();
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
                                    submitPost(token,service,adid);
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
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void submitPost(final String token,final String service,final String adid) {

        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlInterestedRequirement,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    dialog.dismiss();
                        try {
                            JSONObject loginResponse = new JSONObject(response);
                            if (loginResponse.getString("resultType").equalsIgnoreCase("success")) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Posted");
                                builder.setMessage("Your response has been submitted successfully");
                                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        getActivity().onBackPressed();
                                    }
                                });

                                builder.create();
                                builder.show();


                            } else {
                                dialog.dismiss();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.dismiss();
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
                params.put("adid", adid);
                params.put("regno", SharedPreferenceUtils.getInstance(getActivity()).getCId()+"");
                params.put("expectedPrice", expPrice.getText().toString());
                params.put("expectedQty", expQty.getText().toString());
                params.put("remark", remarks.getText().toString());

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
