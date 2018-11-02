package com.example.prekshasingla.cashlessbazar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ProductDetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product_detail, container, false);

        ImageView imageView=rootView.findViewById(R.id.product_image);
        TextView nameTextView=rootView.findViewById(R.id.product_name);
        TextView descTextView=rootView.findViewById(R.id.product_desc);
        TextView priceTextView=rootView.findViewById(R.id.product_price);

        Picasso.with(getContext())
                .load("https://cashlessbazar.com/images/newsletter/TIECONBANNER.png")
                .into(imageView);
        nameTextView.setText("Product Name");
        descTextView.setText("This is the description of the item. Test description of the product. Gift vouchers. Amazon, facebook, flipkart gift vouchers");
        priceTextView.setText("450 Rupees");


        return rootView;
    }
}
