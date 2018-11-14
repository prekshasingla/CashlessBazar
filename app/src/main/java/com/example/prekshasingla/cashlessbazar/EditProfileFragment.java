package com.example.prekshasingla.cashlessbazar;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {


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

        SharedPreferenceUtils sharedPreferenceUtils=SharedPreferenceUtils.getInstance(getActivity());

        ImageView qrImageView=rootView.findViewById(R.id.qr_imageview);
        QRCodeWriter writer = new QRCodeWriter();
        try {

            String contents=sharedPreferenceUtils.getStringValue("loginName", null)+"~"+sharedPreferenceUtils.getIntValue("loginCId", 0);
            BitMatrix bitMatrix = writer.encode(contents, BarcodeFormat.QR_CODE, 512, 512);
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
            nameTextView.setText(sharedPreferenceUtils.getStringValue("loginName", null));

            TextView nameEditText=rootView.findViewById(R.id.name_edittext);
            nameEditText.setText(sharedPreferenceUtils.getStringValue("loginName", null));

            TextView emailEditText=rootView.findViewById(R.id.email_edittext);
            emailEditText.setText(sharedPreferenceUtils.getStringValue("loginEmail", null));

            TextView mobileEditText=rootView.findViewById(R.id.mobile_edittext);
            mobileEditText.setText(sharedPreferenceUtils.getStringValue("loginMobile", null));

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
        return rootView;
    }



}
