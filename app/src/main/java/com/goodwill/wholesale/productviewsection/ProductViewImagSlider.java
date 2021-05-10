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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;
class ProductViewImagSlider extends FragmentPagerAdapter
{
    @NonNull
    private final CharSequence[] stringArray;
    public ProductViewImagSlider(FragmentManager fm, ArrayList<String> img)
    {
        super(fm);
        stringArray = img.toArray(new String[img.size()]);
    }
    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        try
        {
            final ProductViewImageFragment f1 = new ProductViewImageFragment();
            final Bundle bundle = new Bundle();
            bundle.putString("url", String.valueOf(stringArray[position]));
            bundle.putCharSequenceArray("stack", stringArray);
            bundle.putInt("position", position);
            f1.setArguments(bundle);
            return f1;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;

    }
    @Override
    public int getCount()
    {
        return stringArray.length;
    }
}
