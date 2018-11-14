package com.example.prekshasingla.cashlessbazar;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
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
import com.facebook.Profile;
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
import com.instamojo.android.models.Card;
import com.instamojo.android.models.Order;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import static android.provider.ContactsContract.Intents.Insert.EMAIL;
import static com.facebook.FacebookSdk.getApplicationContext;


public class LoginFragment extends Fragment implements View.OnClickListener {


    private int RC_SIGN_IN=100;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    LoginButton fbLoginButton;
    CallbackManager callbackManager;
    TextView signup_text;
    TextView loginError;
    EditText user;
    private EditText password;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(Configration.ClientID)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(getActivity());
//

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_login, container, false);
        rootView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        SignInButton signInButton = rootView.findViewById(R.id.google_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);

          user= (EditText)rootView.findViewById(R.id.user_email);
         password= (EditText)rootView.findViewById(R.id.user_password);

        Button loginButton=(Button)rootView.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String loginId=user.getText().toString();
                String pass=password.getText().toString();
//                ^(?:(?:\+|0{0,2})91(\s*[\-]\s*)?|[0]?)?[789]\d{9}$ phone check india regex

                if(loginId.trim().equals("")){
                    loginError.setText("Enter a valid login Id");
                    return;
                }
                if(pass.trim().equals("")){
                    loginError.setText("Enter a valid password");
                    return;
                }



                if((loginId.length()==10) || android.util.Patterns.EMAIL_ADDRESS.matcher(loginId).matches()){
                    tokenRequest();

                }
                else {
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

                Log.e("success","yes");
                // App code
            }


            @Override
            public void onCancel() {
                // App code
                Log.e("error","yes");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e("error","yes");

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

        loginError=rootView.findViewById(R.id.login_error);



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



    public void tokenRequest(){
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
                                  loginRequest(token);
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



    public void loginRequest(final String token){
//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://api2.cashlessbazar.com/api/customer/login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject loginResponse=new JSONObject(response);
                            if(loginResponse.get("customer")!=null){

                                Toast.makeText(getActivity(),"Login Successful",Toast.LENGTH_SHORT).show();

                                JSONObject customerObject=loginResponse.getJSONObject("customer");
                                SharedPreferenceUtils.getInstance(getContext()).setCId(customerObject.getInt("cId"));
                                SharedPreferenceUtils.getInstance(getContext()).setName(customerObject.getString("name"));
                                SharedPreferenceUtils.getInstance(getContext()).setEmail(customerObject.getString("email"));
                                SharedPreferenceUtils.getInstance(getContext()).setMobile(customerObject.getString("mobile"));
                                SharedPreferenceUtils.getInstance(getContext()).setUsername(customerObject.getString("username"));
                                SharedPreferenceUtils.getInstance(getContext()).setType(customerObject.getString("type"));
                                SharedPreferenceUtils.getInstance(getContext()).setAddress(customerObject.getString("address"));
                                JSONObject walletObject=loginResponse.getJSONObject("wallet");
                                SharedPreferenceUtils.getInstance(getContext()).setCBTPBalance(walletObject.getInt("CBTP_Balance"));
                                SharedPreferenceUtils.getInstance(getContext()).setRewardBalance(walletObject.getInt("Reward_Balance"));


                            }
                            else
                            loginError.setText("Invalid Credentials");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finally {
                            getActivity().onBackPressed();

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
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("LoginId",user.getText().toString().trim());
                params.put("password",password.getText().toString().trim());

                return params;
            }

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
        }
        else if(requestCode== 64206){
            callbackManager.onActivityResult(requestCode, resultCode, data);
//            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.e("inside",account.getDisplayName());

            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
        }
    }


    private void setFacebookData(final LoginResult loginResult)
    {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        try {
                            Log.i("Response",response.toString());

                            String email = response.getJSONObject().getString("email");
                            String firstName = response.getJSONObject().getString("first_name");
                            String lastName = response.getJSONObject().getString("last_name");
                            String gender = response.getJSONObject().getString("gender");



                            Profile profile = Profile.getCurrentProfile();
                            String id = profile.getId();
                            String link = profile.getLinkUri().toString();
                            Log.i("Link",link);
                            if (Profile.getCurrentProfile()!=null)
                            {
                                Log.i("Login", "ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                            }

                            Log.i("Login" + "Email", email);
                            Log.i("Login"+ "FirstName", firstName);
                            Log.i("Login" + "LastName", lastName);
                            Log.i("Login" + "Gender", gender);


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
