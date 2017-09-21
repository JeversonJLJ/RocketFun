package com.jljsoluctions.rocketfun.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jljsoluctions.rocketfun.Adapters.ViewPagerAdapter;
import com.jljsoluctions.rocketfun.Fragments.LeaderboardsFragment;
import com.jljsoluctions.rocketfun.Fragments.NewsFragment;
import com.jljsoluctions.rocketfun.Fragments.SoundsFragment;
import com.jljsoluctions.rocketfun.R;

/**
 * Created by jever on 20/09/2017.
 */

public class TabMain extends AppCompatActivity {

    //This is our tablayout
    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;

    ViewPagerAdapter adapter;

    //Fragments

    LeaderboardsFragment leaderboardsFragment;
    NewsFragment newsFragment;
    SoundsFragment soundsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_main);
        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabMain);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(),false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        // Associate searchable configuration with the SearchView
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        return false;
    }

    private void setupViewPager(ViewPager viewPager)
    {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        leaderboardsFragment =new LeaderboardsFragment();
        newsFragment =new NewsFragment();
        soundsFragment =new SoundsFragment();
        adapter.addFragment(newsFragment,"News");
        adapter.addFragment(soundsFragment,"Sounds");
        adapter.addFragment(leaderboardsFragment,"Leaderboards");
        viewPager.setAdapter(adapter);
    }

}
