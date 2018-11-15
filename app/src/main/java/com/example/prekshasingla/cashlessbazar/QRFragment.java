package com.example.prekshasingla.cashlessbazar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
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
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;


public class QRFragment extends Fragment {
    SurfaceView camera_surface_view;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    final int camera_permission = 100;
    Vibrator vibrator;
    TextView text;
    Intent intent;

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
        camera_surface_view = rootView.findViewById(R.id.surface_view);
        barcodeDetector = new BarcodeDetector.Builder(getActivity()).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(getActivity(), barcodeDetector).setAutoFocusEnabled(true).setRequestedPreviewSize(640, 480).build();

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
                    try {
                        decryptedString = AESCrypt.decrypt(qr_code_text);
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                    final String data[] = decryptedString.split("~");
                    if (decryptedString.contains("CbAppWallet")) {
                        vibrator.vibrate(10);
                        tokenRequest(data[2]);
                        barcodeDetector.release();
                    }
                }

            }
        });
        return rootView;
    }

    private void proceedTransfer(final String regNo, final String token) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlWalletTransfer,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response != null && !response.equals("")) {

                            Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();

                        // Do something with the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("senderRegno", SharedPreferenceUtils.getInstance(getActivity()).getCId() + "");
                params.put("receiverRegno", regNo);
                params.put("amount", "500");

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

    public void tokenRequest(final String regNo) {
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
                                    proceedTransfer(regNo, token);
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
                        // Handle error
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case camera_permission: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
