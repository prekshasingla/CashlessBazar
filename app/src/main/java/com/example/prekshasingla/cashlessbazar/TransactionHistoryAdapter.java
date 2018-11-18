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

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.TransactionHistoryAdapterViewHolder> {

    private List<TransactionHistoryItem> items;
    final private Activity mContext;

    TransactionHistoryAdapter.TransactionHistoryAdapterViewHolder holder;

    public TransactionHistoryAdapter(List<TransactionHistoryItem> items, Activity mContext) {
        this.items = items;
        this.mContext = mContext;

    }



    @Override
    public TransactionHistoryAdapter.TransactionHistoryAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_history, parent, false);
        holder = new TransactionHistoryAdapter.TransactionHistoryAdapterViewHolder(view);
        return holder;

    }


    @Override
    public void onBindViewHolder(TransactionHistoryAdapter.TransactionHistoryAdapterViewHolder holder, int position) {
        TransactionHistoryItem item= items.get(position);

        //holder.sno.setText(item.getSno()+"");
        if(item.getDebit()!=0.00)
         holder.amount.setText("-"+mContext.getString(R.string.rupee)+item.getDebit());
        else if(item.getCredit()!=0.00)
        holder.amount.setText("+"+mContext.getString(R.string.rupee)+item.getCredit());
        holder.description.setText(item.getDescription());
        holder.date.setText(item.getDate());


    }



    @Override
    public int getItemCount() {

        return items.size();
    }


    public class TransactionHistoryAdapterViewHolder extends RecyclerView.ViewHolder {

        //public TextView sno;
        public TextView amount;
        public TextView description;
        public TextView date;

        public TransactionHistoryAdapterViewHolder(View itemView) {
            super(itemView);

           // sno= (TextView) itemView.findViewById(R.id.sno);
            amount=(TextView) itemView.findViewById(R.id.amount);
            description=(TextView) itemView.findViewById(R.id.description);
            date=(TextView)itemView.findViewById(R.id.date);
        }


    }


    public void addAll(List<TransactionHistoryItem> items) {
        this.items = items;
    }
}
