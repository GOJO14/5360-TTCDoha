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
package com.goodwill.wholesale.dashboardsection;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.addresssection.AddressList;
import com.goodwill.wholesale.homesection.HomePage;
import com.goodwill.wholesale.maincontainer.MainActivity;
import com.goodwill.wholesale.orderssection.OrderListing;
import com.goodwill.wholesale.storagesection.LocalData;
import com.goodwill.wholesale.wishlistsection.WishListing;

import org.json.JSONObject;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccountDashboard extends MainActivity {
    @Nullable
    @BindView(R.id.appversion)
    TextView appversion;
    @Nullable
    @BindView(R.id.customername)
    TextView customername;
    @Nullable
    @BindView(R.id.copyright)
    TextView copyright;
    @Nullable
    @BindView(R.id.wish_section)
    RelativeLayout wish_section;
    @Nullable
    @BindView(R.id.profile)
    RelativeLayout profile;
    @Nullable
    @BindView(R.id.logoutsection)
    RelativeLayout logoutsection;
    @Nullable
    @BindView(R.id.address_section)
    RelativeLayout address_section;
    @Nullable
    @BindView(R.id.order_section)
    RelativeLayout order_section;
    @Nullable
    @BindView(R.id.accountimage)
    ImageView accountimage;
    LocalData data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            ViewGroup content = findViewById(R.id.MageNative_frame_container);
            getLayoutInflater().inflate(R.layout.magenative_account_page, content, true);
            ButterKnife.bind(AccountDashboard.this);
            data = new LocalData(AccountDashboard.this);
            showbackbutton();
            showTittle(getResources().getString(R.string.myaccount));
            PackageInfo pInfo = getPackageManager().getPackageInfo(AccountDashboard.this.getPackageName(), 0);
            String version = pInfo.versionName;
            int versioncode = pInfo.versionCode;
            String app_version = "App Version " + version + "(" + versioncode + ")";
            appversion.setText(app_version);
            copyright.setText(getResources().getString(R.string.copyright) + " " + getResources().getString(R.string.app_name));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Glide.with(AccountDashboard.this)
                                .asBitmap()
                                .load(data.getHeaderLogo())
                                .into(accountimage);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            wish_section.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AccountDashboard.this, WishListing.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                }
            });
            profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AccountDashboard.this, ProfilePage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                }
            });
            logoutsection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    data.logout();
                    Intent intent = new Intent(AccountDashboard.this, HomePage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                }
            });
            address_section.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AccountDashboard.this, AddressList.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                }
            });
            order_section.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AccountDashboard.this, OrderListing.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        return false;
    }

    @Override
    protected void onResume() {
        String name = "";
        super.onResume();
        if (data.getLastName()!=null){
            if (data.getLastName().isEmpty()) {
                name = Objects.requireNonNull(data.getFirstName()).substring(0, 2);
            } else {
                name = Objects.requireNonNull(data.getFirstName()).substring(0, 2);
               // name = Objects.requireNonNull(data.getFirstName()).substring(0, 1) + " " + data.getLastName().substring(0, 1);
            }
            customername.setText(name);
        }

    }
}
