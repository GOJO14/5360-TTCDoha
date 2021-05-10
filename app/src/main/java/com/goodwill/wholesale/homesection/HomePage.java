package com.goodwill.wholesale.homesection;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.maincontainer.MainActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomePage extends MainActivity
{
    private String[] tittles;
    @Nullable
    @BindView(R.id.MageNative_pager) ViewPager pager;
    @Nullable
    @BindView(R.id.tabs) TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ViewGroup content = findViewById(R.id.MageNative_frame_container);
        getLayoutInflater().inflate(R.layout.magenative_home_page, content, true);
        ButterKnife.bind(HomePage.this);
        tittles = new String[2];
        tittles[0] = getResources().getString(R.string.home);
        /*tittles[1] = getResources().getString(R.string.categories);*/
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
        mapVariantsIds=new HashMap<>();
        cart_discount=new HashMap<>();
        mapVariantsPrices=new HashMap<String, HashMap<String, String>>();
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.AppTheme));
        setupTabIcons();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {

                Log.i("tab.getPosition(1)", "" + tab.getPosition());
                TextView selectedtab = Objects.requireNonNull(tab.getCustomView()).findViewById(R.id.tab);
                selectedtab.setTextColor(getResources().getColor(R.color.black));
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(@NonNull TabLayout.Tab tab) {
                Log.i("tab.getPosition(2)", "" + tab.getPosition());
                TextView selectedtab = Objects.requireNonNull(tab.getCustomView()).findViewById(R.id.tab);
                selectedtab.setTextColor(getResources().getColor(R.color.main_color_gray));
            }

            @Override
            public void onTabReselected(@NonNull TabLayout.Tab tab) {
                Log.i("tab.getPosition(3)", "" + tab.getPosition());
                pager.setCurrentItem(tab.getPosition());
            }
        });
        if(getIntent().getStringExtra("collection-all")!=null)
        {
            Log.i("Collection-All","IN");
            pager.setCurrentItem(1);
        }
    }

    private void setupTabIcons()
    {
        View view1 = View.inflate(HomePage.this, R.layout.custom_tab, null);
        TextView tab1 = view1.findViewById(R.id.tab);
        tab1.setText(getResources().getString(R.string.home));
        Objects.requireNonNull(tabLayout.getTabAt(0)).setCustomView(view1);

        /*View view2 = View.inflate(HomePage.this, R.layout.custom_tab, null);
        TextView tab2 = view2.findViewById(R.id.tab);
        tab2.setText(getResources().getString(R.string.categories));
        tab2.setTextColor(getResources().getColor(R.color.main_color_gray));
        Objects.requireNonNull(tabLayout.getTabAt(1)).setCustomView(view2);*/
    }

    @Override
    public void onResume() {

        invalidateOptionsMenu();
        super.onResume();
    }
    class MyPagerAdapter extends FragmentPagerAdapter
    {

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return tittles[position];
        }
        @Override
        public int getCount() {
            return 1;
        }
        @Nullable
        @Override
        public Fragment getItem(int position)
        {
            switch (position)
            {
                case 0:
                    return new HomeFragment();

                case 1:
                    return new CategoriesFragment();
            }
            return null;
        }
    }

}
