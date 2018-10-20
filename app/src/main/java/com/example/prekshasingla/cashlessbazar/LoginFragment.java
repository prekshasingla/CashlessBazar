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

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import static android.provider.ContactsContract.Intents.Insert.EMAIL;
import static com.facebook.FacebookSdk.getApplicationContext;


public class LoginFragment extends Fragment implements View.OnClickListener {


    private int RC_SIGN_IN = 100;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    LoginButton loginButton;
    CallbackManager callbackManager;
    TextView signup_text;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                        .requestIdToken(getString(R.string.default_web_client_id))
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
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        SignInButton signInButton = rootView.findViewById(R.id.google_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(this);

        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) rootView.findViewById(R.id.login_button_fb);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        loginButton.setFragment(this);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

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

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.e("inside", account.getDisplayName());

            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
        }
    }

}
