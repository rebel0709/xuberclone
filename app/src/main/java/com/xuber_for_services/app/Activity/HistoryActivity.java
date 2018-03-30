package com.xuber_for_services.app.Activity;


import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.xuber_for_services.app.Fragments.OnGoingTrips;
import com.xuber_for_services.app.Fragments.PastTrips;
import com.xuber_for_services.app.R;

public class HistoryActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private String tabTitles[];
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        setContentView(R.layout.activity_history);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        tabLayout.setupWithViewPager(viewPager);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToMainActivity();
            }
        });

        String strTag = getIntent().getExtras().getString("tag");
        tabTitles = new String[]{"Past Services", "Upcoming Services"};

        viewPager.setAdapter(new SampleFragmentPagerAdapter(tabTitles, getSupportFragmentManager(),
                this));

        if(strTag.equalsIgnoreCase("past")){
            viewPager.setCurrentItem(0);
        }else{
            viewPager.setCurrentItem(1);
        }
    }

    @Override
    public void onBackPressed() {
        GoToMainActivity();
    }

    public void GoToMainActivity(){
        Intent mainIntent = new Intent(this, Home.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;
        private String tabTitles[];
        private Context context;

        public SampleFragmentPagerAdapter(String tabTitles[], FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
            this.tabTitles = tabTitles;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new PastTrips();
                case 1:
                    return new OnGoingTrips();
                default:
                    return new PastTrips();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }

}
