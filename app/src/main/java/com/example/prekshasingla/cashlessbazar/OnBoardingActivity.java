package com.example.prekshasingla.cashlessbazar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingActivity extends AppCompatActivity {
    ViewPager mViewPager;
    ViewPagerAdapter viewPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);
        mViewPager=(ViewPager) findViewById(R.id.onboarding_viewpager);
        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(OnBoardingFragment.newInstance("screen1"));
        viewPagerAdapter.addFragment(OnBoardingFragment.newInstance("screen2"));
        viewPagerAdapter.addFragment(OnBoardingFragment.newInstance("screen3"));
        viewPagerAdapter.addFragment(OnBoardingFragment.newInstance("screen4"));
        viewPagerAdapter.addFragment(OnBoardingFragment.newInstance("screen5"));

        mViewPager.setAdapter(viewPagerAdapter);
        CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
        indicator.initViewPager(mViewPager);
    }
    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }


        public void addFragment(Fragment fragment) {

            mFragmentList.add(fragment);
        }


    }
}
