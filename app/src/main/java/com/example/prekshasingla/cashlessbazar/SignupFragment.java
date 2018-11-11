package com.example.prekshasingla.cashlessbazar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

public class SignupFragment extends Fragment {

    TextView name,email,phone,password;
    TextView signupError;
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

        name=rootView.findViewById(R.id.user_name);
        email=rootView.findViewById(R.id.user_email);
        phone=rootView.findViewById(R.id.user_phone);
        password=rootView.findViewById(R.id.user_password);

        signupError=rootView.findViewById(R.id.signup_error);

        TextView createAccount= rootView.findViewById(R.id.create_button);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate())
                createUser();
            }
        });

        rootView.findViewById(R.id.login_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
                navController.navigate(R.id.loginFragment,null,new NavOptions.Builder()
                        .setClearTask(true).build());
            }
        });


        return rootView;
    }

    private boolean validate() {
        if(name.getText().toString().trim().equals("")){
            signupError.setText("Enter Name");
            return false;
        }
        if(email.getText().toString().trim().equals("")){
            signupError.setText("Enter email");
            return false;
        }
        if(password.getText().toString().trim().equals("")){
            signupError.setText("Enter password");
            return false;
        }
        if(phone.getText().toString().trim().equals("")){
            signupError.setText("Enter phone");
            return false;
        }
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password.getText().toString().trim());
        if(!matcher.matches()){
            signupError.setText("Password must contain 8 characters, 1 capital, 1 small, 1 digit and a special symbol");
            return false;
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()){
            signupError.setText("Invalid Email");
            return false;
        }
        final String PHONE_PATTERN = "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}$";
        pattern = Pattern.compile(PHONE_PATTERN);
        matcher = pattern.matcher(phone.getText().toString().trim());
        if(!matcher.matches()){
            signupError.setText("Invalid Phone Number");
            return false;
        }

        return true;
    }

    private void createUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView=getLayoutInflater().inflate(R.layout.dialog_confirm_phone, null);
        TextView phoneNumber=dialogView.findViewById(R.id.phone_number);
        phoneNumber.setText(phone.getText().toString().trim());
        builder.setView(dialogView)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
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



}
