package com.example.prekshasingla.cashlessbazar;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.navigation.NavController;

public class RecentOrdersRecyclerAdapter extends RecyclerView.Adapter<RecentOrdersRecyclerAdapter.RecentOrdersRecyclerAdapterViewHolder> {

    private static final int ITEM_VIEW_TYPE_HEADER = 1;
    private static final int ITEM_VIEW_TYPE_ITEM = 2;
    private List<RecentOrdersItem> items;
    final private Activity mContext;
    NavController navController;

    RecentOrdersRecyclerAdapter.RecentOrdersRecyclerAdapterViewHolder holder;
    private boolean headerEnabled;

    public RecentOrdersRecyclerAdapter(List<RecentOrdersItem> items, Activity mContext, NavController navController) {
        this.items = items;
        this.mContext = mContext;
        this.navController=navController;

    }



    @Override
    public RecentOrdersRecyclerAdapter.RecentOrdersRecyclerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_orders, parent, false);
        holder = new RecentOrdersRecyclerAdapter.RecentOrdersRecyclerAdapterViewHolder(view);
        return holder;

    }


    @Override
    public void onBindViewHolder(RecentOrdersRecyclerAdapter.RecentOrdersRecyclerAdapterViewHolder holder, int position) {
        RecentOrdersItem item= items.get(position);
        Picasso.with(mContext)
                .load(item.getImageURL())
                .into(holder.imageURL);
        holder.name.setText(item.getName());
        holder.quantity.setText("Qty: "+item.getQuantity()+"pc");
        holder.price.setText("Rs."+item.getPrice()+"/-");
        holder.customerName.setText("Customer Name: "+item.getCustomerName());
        holder.address.setText("Address: "+item.getAddress());
        holder.status.setText("Status: "+item.getStatus());
    }



    @Override
    public int getItemCount() {

        return items.size();
    }


    public class RecentOrdersRecyclerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public ImageView imageURL;
        public TextView name;
        public TextView quantity;
        public TextView price;
        public TextView customerName;
        public TextView address;
        public TextView status;

        public RecentOrdersRecyclerAdapterViewHolder(View itemView) {
            super(itemView);
            imageURL = (ImageView) itemView.findViewById(R.id.item_image);
            name = (TextView) itemView.findViewById(R.id.item_name);
            quantity=(TextView) itemView.findViewById(R.id.item_quantity);
            price=(TextView) itemView.findViewById(R.id.item_price);
            customerName=(TextView) itemView.findViewById(R.id.item_customer_name);
            address=(TextView) itemView.findViewById(R.id.item_address);
            status=(TextView) itemView.findViewById(R.id.item_status);
//            image.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //Intent intent=new Intent(mContext,ProductDetailActivity.class);
//            intent.putExtra("rest",items.get(getAdapterPosition()).getRestaurant_id());
//            navController.navigate(R.id.productDetailFragment);
            //mContext.startActivity(intent);
        }
    }


    public void addAll(List<RecentOrdersItem> items) {
        this.items = items;
    }
}
