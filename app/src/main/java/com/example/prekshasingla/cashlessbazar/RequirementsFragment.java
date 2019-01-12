package com.example.prekshasingla.cashlessbazar;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequirementsFragment extends Fragment {


    private TabLayout tabLayout;
    private ViewPager viewPager;
    NavController navController;
    private Toolbar toolbar;


    public RequirementsFragment() {
        // Required empty public constructor
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            getActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.fragment_requirements, container, false);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((RequirementsActivity)getActivity()).setSupportActionBar(toolbar);

        ((RequirementsActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navController = Navigation.findNavController(getActivity(), R.id.fragment);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        TabsViewPagerAdapter adapter = new TabsViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(RequirementsListFragment.newInstance("looking"), "Looking");
        adapter.addFragment(RequirementsListFragment.newInstance("offering"), "Offering");
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        return rootView;
    }
    class TabsViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public TabsViewPagerAdapter(FragmentManager manager) {
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
