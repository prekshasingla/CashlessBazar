package com.example.prekshasingla.cashlessbazar;

import android.content.Intent;
import android.app.Activity;


import org.json.JSONObject;
import org.json.JSONException;

import android.content.IntentFilter;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    TextView customerName;
    TextView editProfile;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Call the function callInstamojo to start payment here

        mDrawerLayout = findViewById(R.id.drawer_layout);
        final NavigationView navigationView=findViewById(R.id.nav_view);
        View nav_header= navigationView.getHeaderView(0);
         customerName= nav_header.findViewById(R.id.nav_header_customer_name);


         editProfile= nav_header.findViewById(R.id.nav_header_profile_text);

        checkLogin();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id= item.getItemId();
                Intent intent;
                switch (id) {
                    case R.id.nav_login:

                        intent=new Intent(MainActivity.this,LoginSignupActivity.class);
                        startActivity(intent);
                        mDrawerLayout.closeDrawers();


//                        ( Navigation.findNavController(MainActivity.this, R.id.fragment)).navigate(R.id.loginSignupActivity);
                        //Do some thing here
                        // add navigation drawer item onclick method here
                        break;

                    case R.id.nav_wallet:
                         intent = new Intent(MainActivity.this, WalletActivity.class);
                        startActivity(intent);
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.nav_withdraw_funds:
                        intent = new Intent(MainActivity.this, WithdrawFundsActivity.class);
                        startActivity(intent);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_recent_orders:
                        intent = new Intent(MainActivity.this, RecentOrdersActivity.class);
                        intent.putExtra("screen","Recent Orders");
                        startActivity(intent);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_my_orders:
                        intent = new Intent(MainActivity.this, RecentOrdersActivity.class);
                        intent.putExtra("screen","My Orders");
                        startActivity(intent);
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.nav_quick_requirements:
                        intent = new Intent(MainActivity.this, RecentOrdersActivity.class);
                        intent.putExtra("screen","Quick Requirements");
                        startActivity(intent);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_add_product:
                        intent = new Intent(MainActivity.this, AddProductActivity.class);
                        startActivity(intent);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_pay:
                         intent = new Intent(MainActivity.this, QRActivity.class);
                        startActivity(intent);
                        mDrawerLayout.closeDrawers();


                }
                return false;

            }
        });



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);



    }

    private void checkLogin() {
        SharedPreferenceUtils sharedPreferenceUtils=SharedPreferenceUtils.getInstance(getApplicationContext());
        if(sharedPreferenceUtils.getName()!=null) {
            customerName.setText("Hi, " + sharedPreferenceUtils.getName());
            editProfile.setText("Profile");
            customerName.setVisibility(View.VISIBLE);
            editProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent= new Intent(MainActivity.this,EditProfileActivity.class);
                    startActivity(intent);
                    mDrawerLayout.closeDrawers();
                }
            });
        }
        else{
            editProfile.setText("Login");
            customerName.setText("");
            customerName.setVisibility(View.GONE);
            editProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent= new Intent(MainActivity.this,LoginSignupActivity.class);
                    startActivity(intent);
                    mDrawerLayout.closeDrawers();
                }
            });

        }
    }

    @Override
    protected void onResume() {
        checkLogin();
        super.onResume();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
