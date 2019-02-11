package com.example.prekshasingla.cashlessbazar;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {


    ProgressDialog dialog;
    TextView activateText;
    TextView cardNumber,cardName,cardFromThru,cardType;
    RelativeLayout loyaltyCard;
    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_edit_profile, container, false);
        rootView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        dialog=new ProgressDialog(getActivity());
        dialog.setMessage("Loading");
        dialog.setCancelable(false);
        activateText=rootView.findViewById(R.id.activate_text);
        cardName=rootView.findViewById(R.id.card_name);
        cardNumber=rootView.findViewById(R.id.card_number);
        cardType=rootView.findViewById(R.id.card_type);
        cardFromThru=rootView.findViewById(R.id.card_from_thru);
        loyaltyCard=rootView.findViewById(R.id.loyalty_card);

        tokenRequest();
        SharedPreferenceUtils sharedPreferenceUtils=SharedPreferenceUtils.getInstance(getActivity());

        ImageView qrImageView=rootView.findViewById(R.id.qr_imageview);
        QRCodeWriter writer = new QRCodeWriter();
        try {
            String encodedString="";
            String contents="CbAppWallet~"+sharedPreferenceUtils.getName()+"~"+sharedPreferenceUtils.getCId()
                         +"~"+sharedPreferenceUtils.getPhone();
            try {
                 encodedString=new AesBase64Wrapper().encryptAndEncode(contents);
            } catch ( Exception e) {
                e.printStackTrace();
            }
//            encodedString=contents;

            BitMatrix bitMatrix = writer.encode(encodedString, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLUE : Color.WHITE);
                }
            }
            qrImageView.setImageBitmap(bmp);

            TextView nameTextView=rootView.findViewById(R.id.name_textview);
            nameTextView.setText(sharedPreferenceUtils.getName());

            TextView nameEditText=rootView.findViewById(R.id.name_edittext);
            nameEditText.setText(sharedPreferenceUtils.getName());

            TextView emailEditText=rootView.findViewById(R.id.email_edittext);
            emailEditText.setText(sharedPreferenceUtils.getEmail());

            TextView mobileEditText=rootView.findViewById(R.id.mobile_edittext);
            mobileEditText.setText(sharedPreferenceUtils.getPhone());

        } catch (WriterException e) {
            e.printStackTrace();
        }

        rootView.findViewById(R.id.logout_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferenceUtils.getInstance(getActivity()).clear();
                getActivity().onBackPressed();
            }
        });

        LinearLayout changePassword=rootView.findViewById(R.id.change_password);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
                navController.navigate(R.id.changePasswordFragment);

            }
        });


        return rootView;
    }

    public void tokenRequest() {
        dialog.show();
//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();
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

                                    getUserInfo(token);

                                else
                                    Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        } else {
                            Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();

                        }
                        // Do something with the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();

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

    private void getUserInfo(final String token) {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlUserInfo,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            if (responseObject.getString("resultType").equalsIgnoreCase("success")) {

                                JSONObject data = responseObject.getJSONObject("data");

                               if(data.getString("isloyalityactivated").equals("false")){
                                   activateLoyaltyCard();
                                   hideLoyaltyUI();
                               }else{
                                   showLoyaltyUI(data.getJSONObject("loyality"));
                               }

                            } else {
                                getActivity().onBackPressed();
                                Toast.makeText(getActivity(), responseObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                        dialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Cannot Find User. Try Again");
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

    private void hideLoyaltyUI() {
        loyaltyCard.setVisibility(View.GONE);
    }

    private void showLoyaltyUI(JSONObject loyality) throws JSONException {
        loyaltyCard.setVisibility(View.VISIBLE);
        activateText.setVisibility(View.VISIBLE);
        activateText.setOnClickListener(null);
        activateText.setText("Active");
        activateText.setTextColor(getActivity().getResources().getColor(R.color.green));

        cardNumber.setText(loyality.getString("cardnumber"));
        cardFromThru.setText("from : "+loyality.getString("cardregister")+"  thru : "
                            +loyality.getString("expiry"));
        cardType.setText(loyality.getString("cardtype"));
        cardName.setText(loyality.getString("merchant"));
    }

    private void activateLoyaltyCard() {
        activateText.setVisibility(View.VISIBLE);
        activateText.setTextColor(getActivity().getResources().getColor(R.color.red));
        activateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
                navController.navigate(R.id.redeemLoyaltyFragment);
            }
        });
    }

}
