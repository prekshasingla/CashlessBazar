package com.example.prekshasingla.cashlessbazar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.navigation.NavController;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewAdapterViewHolder> {

    private static final int ITEM_VIEW_TYPE_HEADER = 1;
    private static final int ITEM_VIEW_TYPE_ITEM = 2;
    private List<Product> items;
    final private Activity mContext;
    NavController navController;

    RecyclerViewAdapter.RecyclerViewAdapterViewHolder holder;
    private boolean headerEnabled;

    public RecyclerViewAdapter(List<Product> items, Activity mContext, NavController navController) {
        this.items = items;
        this.mContext = mContext;
        this.navController=navController;

    }



    @Override
    public RecyclerViewAdapter.RecyclerViewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        holder = new RecyclerViewAdapter.RecyclerViewAdapterViewHolder(view);
        return holder;

    }


    @Override
    public void onBindViewHolder(RecyclerViewAdapter.RecyclerViewAdapterViewHolder holder, int position) {
        Product item= items.get(position);
        Picasso.with(mContext)
                .load(item.getImg())
                .into(holder.image);
        holder.name.setText(item.getName());
        holder.price.setText(mContext.getString(R.string.rupee)+item.getCbtp());

    }



    @Override
    public int getItemCount() {

        return items.size();
    }


    public class RecyclerViewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView image;
        public TextView name;
        public TextView price;

        public RecyclerViewAdapterViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.item_image);
            name = (TextView) itemView.findViewById(R.id.item_name);
            price=(TextView) itemView.findViewById(R.id.item_price);
            image.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent=new Intent(mContext,ProductDetailActivity.class);
            intent.putExtra("item_id",items.get(getAdapterPosition()).getId()+"");

            mContext.startActivity(intent);
        }
    }


    public void addAll(List<Product> items) {
        this.items = items;
    }
}
