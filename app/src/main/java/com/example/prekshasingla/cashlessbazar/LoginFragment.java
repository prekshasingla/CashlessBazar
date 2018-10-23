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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import androidx.navigation.NavController;
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
        SignInButton signInButton = rootView.findViewById(R.id.google_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);

        EditText user= (EditText)rootView.findViewById(R.id.user_email);
        EditText password= (EditText)rootView.findViewById(R.id.user_password);

        Button loginButton=(Button)rootView.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginRequest();
            }
        });

        callbackManager = CallbackManager.Factory.create();
        fbLoginButton = (LoginButton) rootView.findViewById(R.id.login_button_fb);
        fbLoginButton.setReadPermissions(Arrays.asList(EMAIL));
        fbLoginButton.setFragment(this);
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

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
                navController.navigate(R.id.signupFragment);

            }
        });
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




    public void loginRequest(){
//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://api2.cashlessbazar.com/api/customer/login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

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
                params.put("LoginId","8447707717");
                params.put("password","111111");

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/x-www-form-urlencoded");

                params.put("Authorization","bearer Mxj8Lq3gDUcTt4S42N3IqYT1LZpWdXlWI9f5xDRifvQWRaxxVMPOoL6w3Nhig_4n4sGtA3qEMaFiEGni_JcWLngp--QgKROWi2W4-DpJNP557jyA_ILM2XHbfBCn9hrGF5dsChIauOZInzrpNf_50F4-g-cz4lYjgnGwqcBPGx_SCpWpNa4H1oqOLDrhPkGLf3iUMLRDhQgrfffZ1P0oTmNKTRAY2c3g2Nl7EZ3XQzEKSKetAIAarm3-HVmlu6Ecsotm87cVXWwEKIwenBhK5aN3ZtpQ7-2LAy3v6JPCf_jIh8SrSJATQ1MeHr4bVPp4");
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

}
