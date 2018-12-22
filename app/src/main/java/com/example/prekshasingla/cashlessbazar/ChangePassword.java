package com.example.prekshasingla.cashlessbazar;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePassword extends Fragment {


    private ProgressDialog dialog;

    public ChangePassword() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_change_password, container, false);
        rootView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);
        final AppCompatEditText currentPass = rootView.findViewById(R.id.current_password);
        final AppCompatEditText newPass = rootView.findViewById(R.id.new_password);
        final AppCompatEditText confirmPass = rootView.findViewById(R.id.confirm_password);
        Button continueBtn = rootView.findViewById(R.id.change_pass_continue);

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPass.getText().toString().trim().equals("")) {
                    currentPass.setError("Enter current password");
                    return;
                }
                if (newPass.getText().toString().trim().equals("")) {
                    newPass.setError("Enter new password");
                    return;
                }
                if (newPass.getText().toString().trim().length() < 6 ||
                        newPass.getText().toString().trim().length() > 15) {
                    newPass.setError("minimum 6 and maximum 15 character");
                    return;
                }
                if (confirmPass.getText().toString().trim().equals("")) {
                    confirmPass.setError("Re-enter password");
                    return;
                }
                if (!newPass.getText().toString().trim().equals(confirmPass.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "Passwords dont match", Toast.LENGTH_SHORT).show();
                    return;
                }
                requestToken(currentPass.getText().toString().trim(), newPass.getText().toString().trim());
            }
        });
        return rootView;
    }

    private void requestToken(final String oldPass, final String newPass) {
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

                                    changePassword(token, oldPass, newPass);
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

    private void changePassword(final String token, final String oldPass, final String newPass) {

        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlChangePassword,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject responseObject = new JSONObject(response);
                                if (responseObject.getInt("status_code") == 200) {
                                    SharedPreferenceUtils.getInstance(getActivity()).clear();
                                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                                    builder.setMessage("Password changed successfully. Please login again to continue");
                                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            getActivity().startActivity(intent);
                                        }
                                    });

                                    builder.create();
                                    builder.show();
                                } else {
                                    Toast.makeText(getActivity(), responseObject.getString("status_txt"), Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(getActivity(), "Some error occurred. Try again later", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("oldPassword", oldPass);
                params.put("newPassword", newPass);
                params.put("regno", SharedPreferenceUtils.getInstance(getActivity()).getCId() + "");

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
