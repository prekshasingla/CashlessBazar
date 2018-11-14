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

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.security.GeneralSecurityException;

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
                    String decryptedString="";
                    String qr_code_text = barcodeSparseArray.valueAt(0).displayValue;
                    try {
                         decryptedString=AESCrypt.decrypt(qr_code_text);
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                    final String data[] = decryptedString.split("~");
                    if (qr_code_text.contains("CbAppWallet")) {
                        vibrator.vibrate(10);
                        Toast.makeText(getActivity(), data[1], Toast.LENGTH_SHORT).show();
//                                intent.putExtra("flag", "insideOrder");
//                                startActivity(intent);

                    }
                }

            }
        });
        return rootView;
    }

    private void proceedTransfer() {


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
