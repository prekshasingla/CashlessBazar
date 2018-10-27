 package com.example.prekshasingla.cashlessbazar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RecentOrdersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_orders);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
