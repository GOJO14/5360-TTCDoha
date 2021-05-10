/*
 * Copyright/**
 *          * CedCommerce
 *           *
 *           * NOTICE OF LICENSE
 *           *
 *           * This source file is subject to the End User License Agreement (EULA)
 *           * that is bundled with this package in the file LICENSE.txt.
 *           * It is also available through the world-wide-web at this URL:
 *           * http://cedcommerce.com/license-agreement.txt
 *           *
 *           * @category  Ced
 *           * @package   MageNative
 *           * @author    CedCommerce Core Team <connect@cedcommerce.com >
 *           * @copyright Copyright CEDCOMMERCE (http://cedcommerce.com/)
 *           * @license      http://cedcommerce.com/license-agreement.txt
 *
 */
package com.goodwill.wholesale.productviewsection;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import com.goodwill.wholesale.R;
import butterknife.BindView;
import butterknife.ButterKnife;
public class ZoomImagePagerActivity extends FragmentActivity
{

    private String[] url;
    @BindView(R.id.MageNative_pager) NonSwipeableViewPager pager;
    @SuppressWarnings("deprecation")
    @BindView(R.id.MageNative_productgallery) Gallery productgallery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magenative_content_zoom_image_pager);
        ButterKnife.bind(ZoomImagePagerActivity.this);
        Intent intent = getIntent();
        url = intent.getStringArrayExtra("IMAGEURL");
        int pos = intent.getIntExtra("POS", 0);
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setCurrentItem(pos);
        ProductImageAdapter2 productImageAdapter = new ProductImageAdapter2(getApplicationContext(), url);
        productgallery.setAdapter(productImageAdapter);
        productgallery.setSelection(pos);
        productgallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pager.setCurrentItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                productgallery.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {

        invalidateOptionsMenu();
        super.onResume();

    }

    class MyPagerAdapter extends FragmentPagerAdapter {
        MyPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return url.length;
        }

        @NonNull
        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            final ShowZoomImageFragment f = new ShowZoomImageFragment();
            final Bundle bundle = new Bundle();
            bundle.putString("current", url[position]);
            f.setArguments(bundle);
            return f;
        }
    }
}