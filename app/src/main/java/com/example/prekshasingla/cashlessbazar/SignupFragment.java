package com.example.prekshasingla.cashlessbazar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

public class SignupFragment extends Fragment {


    EditText firstName, lastName, email, phone, password;
    TextView signupError;
    String otp = null;
    ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_signup, container, false);
        rootView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);
        firstName = rootView.findViewById(R.id.user_first_name);
        lastName = rootView.findViewById(R.id.user_last_name);
        email = rootView.findViewById(R.id.user_email);
        phone = rootView.findViewById(R.id.user_phone);
        password = rootView.findViewById(R.id.user_password);

        signupError = rootView.findViewById(R.id.signup_error);

        TextView createAccount = rootView.findViewById(R.id.create_button);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    if(otp==null)
                    confirmNumber();
                    else
                        authenticateUser();
                }
            }
        });

        rootView.findViewById(R.id.login_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
                navController.navigate(R.id.loginFragment, null, new NavOptions.Builder()
                        .setClearTask(true).build());
            }
        });


        return rootView;
    }

    private boolean validate() {
        if (firstName.getText().toString().trim().equals("")) {
            signupError.setText("Enter First Name");
            return false;
        }
        if (lastName.getText().toString().trim().equals("")) {
            signupError.setText("Enter Last Name");
            return false;
        }
//        if (email.getText().toString().trim().equals("")) {
//           signupError.setText("Enter email");
//            return false;
//        }
        if (password.getText().toString().trim().equals("")) {
            signupError.setText("Enter password");
            return false;
        }
        if (phone.getText().toString().trim().equals("")) {
            signupError.setText("Enter phone");
            return false;
        }
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password.getText().toString().trim());
        if (!matcher.matches()) {
            signupError.setText("Password must contain 8 characters, 1 capital, 1 small, 1 digit and a special symbol");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()) {
            signupError.setText("Invalid Email");
            return false;
        }
        final String PHONE_PATTERN = "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}$";
        pattern = Pattern.compile(PHONE_PATTERN);
        matcher = pattern.matcher(phone.getText().toString().trim());
        if (!matcher.matches()) {
            signupError.setText("Invalid Phone Number");
            return false;
        }

        return true;
    }

    private void confirmNumber() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirm_phone, null);
        TextView phoneNumber = dialogView.findViewById(R.id.phone_number);
        phoneNumber.setText(phone.getText().toString().trim());
        builder.setView(dialogView)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sendOTP();
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

    private void sendOTP() {
        otp = getRandomNumberString();
        SharedPreferenceUtils.getInstance(getActivity()).setOTP(otp);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("203.212.70.200")
                .appendPath("smpp")
                .appendPath("sendsms")
                .appendQueryParameter("username", "barterapi")
                .appendQueryParameter("password", "barterapi123")
                .appendQueryParameter("to", phone.getText().toString().trim())
                .appendQueryParameter("from", "BARTER")
                .appendQueryParameter("text", "Hi! Thanks for registering with Cashless Bazar. Your OTP is " + otp);

        String myUrl = builder.build().toString();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, myUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        authenticateUser();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(getActivity(), "There may be some error. Please try later", Toast.LENGTH_SHORT).show();
                    }
                }) {
//
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void authenticateUser() {
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
                                tokenRequest();
                            } else {
                                Toast.makeText(getActivity(), "Invalid OTP", Toast.LENGTH_SHORT).show();
                                authenticateUser();
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

    public void tokenRequest() {
        dialog.show();
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
                                    createUser(token);
                                else {
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {

                        }
                        // Do something with the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Some Error Occured.", Toast.LENGTH_SHORT).show();
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

    private void createUser(final String token) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlRegisterUser,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject responseObject = new JSONObject(response);
                            if (responseObject.getInt("status_code") == 201) {
                                dialog.dismiss();
                                if (responseObject.get("customer") != null) {

//                                    Toast.makeText(getActivity(),"Login Successful",Toast.LENGTH_SHORT).show();

                                    JSONObject customerObject = responseObject.getJSONObject("customer");
                                    SharedPreferenceUtils.getInstance(getContext()).setCId(customerObject.getInt("cId"));
                                    SharedPreferenceUtils.getInstance(getContext()).setName(customerObject.getString("name"));
                                    SharedPreferenceUtils.getInstance(getContext()).setEmail(customerObject.getString("email"));
                                    SharedPreferenceUtils.getInstance(getContext()).setMobile(customerObject.getString("mobile"));
                                    SharedPreferenceUtils.getInstance(getContext()).setUsername(customerObject.getString("username"));
                                    SharedPreferenceUtils.getInstance(getContext()).setType(customerObject.getString("type"));
                                    SharedPreferenceUtils.getInstance(getContext()).setAddress(customerObject.getString("address"));

                                    JSONObject walletObject = customerObject.getJSONObject("wallet");
                                    SharedPreferenceUtils.getInstance(getContext()).setCBTPBalance(walletObject.getInt("CBTP_Balance"));
                                    SharedPreferenceUtils.getInstance(getContext()).setRewardBalance(walletObject.getInt("Reward_Balance"));


                                }
                                Toast.makeText(getActivity(), responseObject.getString("status_txt"), Toast.LENGTH_LONG).show();
                                getActivity().onBackPressed();
                            } else {
                                dialog.dismiss();
                                Toast.makeText(getActivity(), responseObject.getString("status_txt"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Customer Already Registered", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("FirstName", firstName.getText().toString());
                params.put("LastName", lastName.getText().toString());
                params.put("Email", email.getText().toString());
                params.put("Mobile", phone.getText().toString());
                params.put("ReferId", "null");
//                params.put("Ischecked", "true");
                params.put("Password", password.getText().toString());

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
