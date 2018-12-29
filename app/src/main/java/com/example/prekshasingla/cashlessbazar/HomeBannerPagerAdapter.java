package com.example.prekshasingla.cashlessbazar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

public class HomeBannerPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
    FragmentManager fragmentManager;
    private List<MainActivityFragment.Banner> mBannerUrl;
    private Activity context;


    public HomeBannerPagerAdapter(FragmentManager fm, List<MainActivityFragment.Banner> mBannerUrl, Activity context) {
        super(fm);
        this.fragmentManager = fm;
        this.mBannerUrl = mBannerUrl;
        this.context = context;
    }

    public void addAll(List<MainActivityFragment.Banner> mBannerUrl) {
        this.mBannerUrl = mBannerUrl;
    }


    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new BannerFragment();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        //args.putInt(BannerFragment.ARG_OBJECT, i + 1);
        args.putString("url", mBannerUrl.get(i).url);
        args.putString("image", mBannerUrl.get(i).image);
        args.putString("name", mBannerUrl.get(i).name);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);

    }

//        @Override
//        public float getPageWidth(int position) {
//            Display display = context.getWindowManager().getDefaultDisplay();
//            Point size = new Point();
//            display.getSize(size);
//            return (float) 900 / size.x;
//        }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);

    }

    @Override
    public int getCount() {
        return mBannerUrl.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position + 1);
    }

    public static class BannerFragment extends Fragment {

        public BannerFragment() {
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home_banner, container, false);
            String url = getArguments().getString("image");
            ImageView imageView = rootView.findViewById(R.id.bannerImage);
            Picasso.with(getActivity())
                    .load(url)
                    .into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent= new Intent(getActivity(),EventsActivity.class);
                    getActivity().startActivity(intent);
                }
            });
            return rootView;
        }
    }
}



