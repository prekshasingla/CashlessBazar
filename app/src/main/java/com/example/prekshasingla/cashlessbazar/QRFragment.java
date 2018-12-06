package com.example.prekshasingla.cashlessbazar;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import static com.facebook.FacebookSdk.getApplicationContext;


public class QRFragment extends Fragment {
    SurfaceView camera_surface_view;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    final int camera_permission = 100;
    Vibrator vibrator;

    TextView text;
    Intent intent;
    String amount;
//    String screenName;

    public QRFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_qr, container, false);
        rootView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });


        LinearLayout linearLayoutMobilePay=rootView.findViewById(R.id.mobile_pay_ll);

        Intent intent=getActivity().getIntent();

        final String screenName=intent.getStringExtra("screen");


        if(screenName.equals("requestPayment")){

            linearLayoutMobilePay.setVisibility(View.GONE);
            enterAmount();

        }
        else{
            linearLayoutMobilePay.setVisibility(View.VISIBLE);
        }



        final TextView textViewVendorPhone=rootView.findViewById(R.id.vendor_phone);
        Button payButton=rootView.findViewById(R.id.pay_button);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textViewVendorPhone.getText().length()==10){
                    tokenRequest(null,textViewVendorPhone.getText()+"",null,screenName);
                }
                else {
                    textViewVendorPhone.setError("Invalid Phone Number");
                }
            }
        });
        camera_surface_view = rootView.findViewById(R.id.surface_view);
        barcodeDetector = new BarcodeDetector.Builder(getActivity()).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(getActivity(), barcodeDetector).setAutoFocusEnabled(true).setRequestedPreviewSize(640, 480).build();

        camera_surface_view.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions( new String[]{Manifest.permission.CAMERA}, camera_permission);

                    return;
                }
                try {
                    cameraSource.start(camera_surface_view.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodeSparseArray = detections.getDetectedItems();
                if (barcodeSparseArray.size() != 0) {
                    vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    String decryptedString = "";
                    String qr_code_text = barcodeSparseArray.valueAt(0).displayValue;
//                    try {
//                        decryptedString = AESCrypt.decrypt(qr_code_text);
//                    } catch (GeneralSecurityException e) {
//                        e.printStackTrace();
//                    }
                    decryptedString=qr_code_text;

                    final String data[] = decryptedString.split("~");
                    if (decryptedString.contains("CbAppWallet")) {
                        vibrator.vibrate(10);
                        tokenRequest(data[2],null,amount,screenName);
                        barcodeDetector.release();
                    }
                }

            }
        });
        return rootView;
    }

    private void enterAmount() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_enter_amount, null);
        final AppCompatEditText amountField = dialogView.findViewById(R.id.amount_field);
        builder.setView(dialogView)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (amountField.getText().toString().length() <0 ) {
                            amountField.setError("Enter Amount");
                        } else {
                            if (amountField.getText().toString().trim().length()>0 &&
                                    Float.parseFloat(amountField.getText().toString().trim())!=0) {
                                amount=amountField.getText().toString().trim();

                            } else {
                                enterAmount();
                                Toast.makeText(getActivity(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
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


    public void tokenRequest(final String regNo, final String phone, final String amount, final String screenName) {
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
                                        if(screenName.equals("requestPayment")){

                                         requestPayment(regNo,amount,token,screenName);
                                        }
                                        else {

                                            findUserWalletTransfer(regNo, phone, token);
                                        }
//                                    proceedTransfer(regNo, token);
                                    else
                                        Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            } else {
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
                        // Do something with the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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

    private void findUserWalletTransfer(final String regNo, final String phone, final String token) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlUserInfo,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject responseObject = new JSONObject(response);
                            if (responseObject.getString("resultType").equalsIgnoreCase("success")) {

                                    JSONObject customer = responseObject.getJSONObject("data");
                                    Bundle bundle = new Bundle();
                                    bundle.putString("screenName","payment");
                                    bundle.putString("name", customer.getString("firstname")+customer.getString("lastname"));
                                    bundle.putString("phone", customer.getString("mobile"));
                                    bundle.putString("cId", customer.getString("regno"));
                                    NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
                                    navController.navigate(R.id.userInfoFragment, bundle);

                            } else {
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
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
                if(regNo!=null) {
                    params.put("Regno", regNo);
                }
                if(phone!=null){
                    params.put("Contact", phone);
                }
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

    private void requestPayment(final String regNo, final String amount, final String token, final String screenName) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlRequestTransfer,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject responseObject = new JSONObject(response);
                            if (responseObject.getInt("status_code") == 200) {
                                if(responseObject.getString("status_txt").equals("success")) {

                                    JSONObject customer = responseObject.getJSONObject("data");
                                    Bundle bundle = new Bundle();
                                    bundle.putString("screenName", screenName);
                                    bundle.putString("name", customer.getString("name"));
                                    bundle.putString("phone", customer.getString("mobile"));
                                    bundle.putString("request_id", customer.getString("request_id"));
                                    bundle.putString("amount", customer.getString("amount"));
                                    bundle.putString("sendRegNo", regNo);
                                    bundle.putString("recRegNo", SharedPreferenceUtils.getInstance(getActivity()).getCId() + "");
                                    NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
                                    navController.navigate(R.id.userInfoFragment, bundle);
                                }
                             else {
                                String status = responseObject.getString("status_txt");
                                Bundle bundle = new Bundle();
                                bundle.putString("status",status);
                                NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
                                navController.navigate(R.id.userInfoFragment, bundle);

                            }} else {
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
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
                   params.put("sender_regno", regNo);
                   params.put("receiver_regno", SharedPreferenceUtils.getInstance(getActivity()).getCId()+"");
                   params.put("receiver_mobile",SharedPreferenceUtils.getInstance(getActivity()).getPhone());
                   params.put("amount",amount);
                   params.put("mode","collect");
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


    public void startCamera() {
        camera_surface_view.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, camera_permission);

                    return;
                }
                try {
                    cameraSource.start(camera_surface_view.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case camera_permission: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera();
                    try {
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            getActivity().onBackPressed();
                        }
                        cameraSource.start(camera_surface_view.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    getActivity().onBackPressed();
                }
            }
            break;
        }
    }



}
