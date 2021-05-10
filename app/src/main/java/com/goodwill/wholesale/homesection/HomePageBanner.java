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
package com.goodwill.wholesale.homesection;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.json.JSONArray;
public class HomePageBanner extends FragmentStatePagerAdapter
{
    JSONArray stringArray;
    public HomePageBanner(FragmentManager fm, Context context, JSONArray img)
    {
        super(fm);
        stringArray = img;
    }
    @Override
    public Fragment getItem(int position)
    {
        final MainBannerFragment f1=new MainBannerFragment();
        final Bundle bundle=new Bundle();
        try
        {
            bundle.putString("banner_image", stringArray.getJSONObject(position).getString("url"));
            bundle.putString("link_to", stringArray.getJSONObject(position).getString("link_to"));
            bundle.putString("id", stringArray.getJSONObject(position).getString("id"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        f1.setArguments(bundle);
        return f1;
    }
    @Override
    public int getCount()
    {
        return stringArray.length();
    }
}
