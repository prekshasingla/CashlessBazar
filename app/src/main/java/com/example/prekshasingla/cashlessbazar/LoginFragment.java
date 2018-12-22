package com.example.prekshasingla.cashlessbazar;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import static android.provider.ContactsContract.Intents.Insert.EMAIL;
import static com.facebook.FacebookSdk.getApplicationContext;


public class LoginFragment extends Fragment implements View.OnClickListener {


    private int LOGIN = 1;
    private int FORGOT_PASSWORD = 2;
    private int NEW_PASSWORD = 3;
    private int SOCIALLOGIN = 4;
    private int SOCIALREGISTER = 5;
    private int FB = 1;
    private int GOOGLE = 2;
    private int RC_SIGN_IN = 100;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    LoginButton fbLoginButton;
    CallbackManager callbackManager;
    TextView signup_text;
    TextView forgot_password_text;
    TextView loginError;
    EditText user;
    private EditText password;
    ProgressDialog dialog;
    String mobile;
    String otp;
    String newPass;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getActivity());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        rootView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);

        SignInButton signInButton = rootView.findViewById(R.id.google_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);

        user = (EditText) rootView.findViewById(R.id.user_email);
        password = (EditText) rootView.findViewById(R.id.user_password);

        Button loginButton = (Button) rootView.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String loginId = user.getText().toString();
                String pass = password.getText().toString();
//                ^(?:(?:\+|0{0,2})91(\s*[\-]\s*)?|[0]?)?[789]\d{9}$ phone check india regex

                if (loginId.trim().equals("")) {
                    loginError.setText("Enter a valid login Id");
                    return;
                }
                if (pass.trim().equals("")) {
                    loginError.setText("Enter a valid password");
                    return;
                }


                if ((loginId.length() == 10) || android.util.Patterns.EMAIL_ADDRESS.matcher(loginId).matches()) {
                    tokenRequest(LOGIN, null, null, null, null, -1,null);

                } else {
                    loginError.setText("Invalid Credentials");
                }


            }
        });

        callbackManager = CallbackManager.Factory.create();
        fbLoginButton = (LoginButton) rootView.findViewById(R.id.login_button_fb);
        fbLoginButton.setReadPermissions(Arrays.asList(EMAIL, "public_profile"));
        fbLoginButton.setFragment(this);
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                setFacebookData(loginResult);
                LoginManager.getInstance().logOut();

                Log.e("success", "yes");
                // App code
            }


            @Override
            public void onCancel() {
                // App code
                Log.e("error", "yes");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e("error", "yes");

                // App code
            }
        });

        signup_text = rootView.findViewById(R.id.signup_text);
        signup_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
                navController.navigate(R.id.signupFragment, null,
                        new NavOptions.Builder()
                                .setClearTask(true).build());

            }
        });

        forgot_password_text = rootView.findViewById(R.id.forgot_password_text);
        forgot_password_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                enterMobile(FORGOT_PASSWORD, null, null, -1);
            }
        });
        loginError = rootView.findViewById(R.id.login_error);


        return rootView;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.google_sign_in_button:
                signIn();
                break;

            // ...
        }
    }


    private void enterMobile(final int urlType, final String email, final String mobileno, final int socialSite) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_enter_mobile, null);
        final AppCompatEditText mobileField = dialogView.findViewById(R.id.mobile_field);

        builder.setView(dialogView)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final String PHONE_PATTERN = "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}$";
                        Pattern pattern = Pattern.compile(PHONE_PATTERN);
                        Matcher matcher = pattern.matcher(mobileField.getText().toString().trim());
                        if (!matcher.matches()) {
                            enterMobile(urlType, email, mobileno, socialSite);
                            Toast.makeText(getActivity(), "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();

                        } else {
                            if (urlType == FORGOT_PASSWORD) {
                                mobile = mobileField.getText().toString().trim();
                                tokenRequest(FORGOT_PASSWORD, null, null, null, null, -1,null);
                            } else if (urlType == SOCIALREGISTER) {
                                mobile = mobileField.getText().toString().trim();
                                tokenRequest(SOCIALREGISTER, null, null, email, mobileno, socialSite,null);

                            }

                        }

                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().onBackPressed();
                        // User cancelled the dialog
                    }
                });
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

    private void enterOtpPassword() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_enter_otp_password, null);
        final AppCompatEditText otpField = dialogView.findViewById(R.id.otp_field);
        final AppCompatEditText passwordField = dialogView.findViewById(R.id.password_field);
        builder.setView(dialogView)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (otpField.getText().toString().length() <= 0) {
                            enterOtpPassword();
                            Toast.makeText(getActivity(), "Enter OTP", Toast.LENGTH_SHORT).show();

                        } else if (otpField.toString().trim().length() < 6) {
                            enterOtpPassword();
                            Toast.makeText(getActivity(), "Enter correct otp", Toast.LENGTH_SHORT).show();
                        } else if (passwordField.toString().trim().length() <= 0) {
                            enterOtpPassword();
                            Toast.makeText(getActivity(), "Enter Password", Toast.LENGTH_SHORT).show();
                        } else if (passwordField.toString().trim().length() < 6) {
                            enterOtpPassword();
                            Toast.makeText(getActivity(), "Password length should be between 6 and 15", Toast.LENGTH_SHORT).show();
                        } else if (otpField.getText().toString().trim().length() > 0 &&
                                passwordField.getText().toString().trim().length() > 6) {
                            tokenRequest(NEW_PASSWORD, otpField.getText().toString().trim(), passwordField.getText().toString().trim(),
                                    null, null, -1,null);


                        } else {
                            enterOtpPassword();
                            Toast.makeText(getActivity(), "Some Error occured, please try again", Toast.LENGTH_SHORT).show();
                        }
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().onBackPressed();
                        // User cancelled the dialog
                    }
                });
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

    public void tokenRequest(final int urlType, final String otp, final String newPass,
                             final String email, final String mobile, final int socialSite,final String firstname) {
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
                                    if (urlType == LOGIN)
                                        loginRequest(token);
                                    else if (urlType == FORGOT_PASSWORD)
                                        passwordRequest(token, mobile);
                                    else if (urlType == NEW_PASSWORD)
                                        newPasswordRequest(token, mobile, otp, newPass);
                                    else if (urlType == SOCIALLOGIN)
                                        socialLogin(token, email, socialSite,firstname);

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


    public void loginRequest(final String token) {

//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://api2.cashlessbazar.com/api/customer/login",
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

                                getActivity().onBackPressed();


                            } else {
                                dialog.dismiss();
                                loginError.setText("Login Failed. Try Again");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.dismiss();
                            loginError.setText("Login Failed. Try Again");
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
                params.put("LoginId", user.getText().toString().trim());
                params.put("password", password.getText().toString().trim());

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

    public void passwordRequest(final String token, final String mobile) {

//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlRequestOtp,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();

                        if (response.contains("Sent")) {

                            enterOtpPassword();

                            Toast.makeText(getActivity(), "OTP sent to " + mobile, Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getActivity(), "Some error occurred, please try again", Toast.LENGTH_SHORT).show();

                        }

                        // Do something with the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Some error occurred, please try again", Toast.LENGTH_SHORT).show();

                        // Handle error
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Mobile", mobile);
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

    public void newPasswordRequest(final String token, final String mobile, final String otp, final String newPass) {

//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlForgotPassword,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();

                        if (response.contains("Success")) {
                            Toast.makeText(getActivity(), "Password Change Successful", Toast.LENGTH_SHORT).show();
                            getActivity().onBackPressed();

                        } else {
                            Toast.makeText(getActivity(), "Invalid details, please try again", Toast.LENGTH_SHORT).show();

                        }

                        // Do something with the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Some error occurred, please try again", Toast.LENGTH_SHORT).show();

                        // Handle error
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Mobile", mobile);
                params.put("OTP", otp);
                params.put("NewPassword", newPass);
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

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //fb request code= 64206
    //fb resultcode=-1

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else if (requestCode == 64206) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
//            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

        }
    }


    public void socialLogin(final String token, final String email, final int socialSite,final String firstname) {

//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlSocialLogin,
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

                                getActivity().onBackPressed();


                            } else if (loginResponse.getString("errorcode").equalsIgnoreCase("ER003")) {
                                dialog.dismiss();
                                NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
                                Bundle args=new Bundle();
                                args.putString("email",email);
                                args.putString("social_site",socialSite+"");
                                args.putString("firstname",firstname);
                                navController.navigate(R.id.verifyMobileFragment, args);
//                                enterMobile(SOCIALREGISTER, email, null, socialSite);


                            } else {
                                dialog.dismiss();
                                loginError.setText("Login Failed. Try Again");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.dismiss();
                            loginError.setText("Login Failed. Try Again");
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


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.e("inside", account.getDisplayName());
            String email=account.getEmail();
            String firstName=account.getDisplayName();
            tokenRequest(SOCIALLOGIN, null, null, email, null, GOOGLE,firstName);

            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {

            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
        }
    }


    private void setFacebookData(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        try {
                            Log.i("Response", response.toString());

                            String email = response.getJSONObject().getString("email");
                            String firstName = response.getJSONObject().getString("first_name");
//                            String lastName = response.getJSONObject().getString("last_name");
//
                            tokenRequest(SOCIALLOGIN, null, null, email, null, FB,firstName);




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }

}
