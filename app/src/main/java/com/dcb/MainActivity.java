package com.dcb;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import colortrackview.ColorTrackTabLayout;
import colortrackview.RoundColorTrackTabLayout;

public class MainActivity extends AppCompatActivity {
    private RoundColorTrackTabLayout tab1;
    private ColorTrackTabLayout tab2;
    private ViewPager mViewPager1;
    private ViewPager mViewPager2;
    private String[] titles= new String[]{"全部", "进行中", "长标题哦","已完成"};;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tab1 = (RoundColorTrackTabLayout) findViewById(R.id.tab1);
        mViewPager1 = (ViewPager) findViewById(R.id.viewPager1);
        tab2 = (ColorTrackTabLayout) findViewById(R.id.tab2);
        mViewPager2 = (ViewPager) findViewById(R.id.viewPager2);
        initTab();
    }


    protected void initTab() {
        final List<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            fragments.add(MyFragment.newInstance());
        }


        mViewPager1.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        });
        tab1.setSelectedTabIndicatorHeight(0);//指示器高度
        tab1.setLastSelectedTabPosition(0);//默认选中第0个
        tab1.setupWithViewPager(mViewPager1);

        final List<Fragment> fragments2 = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            fragments2.add(MyFragment.newInstance());
        }
        mViewPager2.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments2.get(position);
            }

            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        });
        tab2.setupWithViewPager(mViewPager2);
    }
}
