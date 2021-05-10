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
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.goodwill.wholesale.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

class ProductImageAdapter2 extends BaseAdapter
{

    @Nullable
    private static LayoutInflater inflater = null;
    private final String[] urls;
    private final Context galleryContext;
    @Nullable
    @BindView(R.id.MageNative_galleryimage) ImageView imageView;
    public ProductImageAdapter2(Context c, String image[])
    {
        galleryContext = c;
        urls = image;
        inflater = (LayoutInflater) galleryContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return urls.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Nullable
    @Override
    public View getView(int position, @Nullable View convertView, ViewGroup parent)
    {
        View vi=null;
        try
        {

            if (convertView == null) {
                vi = Objects.requireNonNull(inflater).inflate(R.layout.magenative_gallerycards_2, parent, false);
            } else {
                vi = convertView;
            }
            ButterKnife.bind(this,vi);
            Glide.with(galleryContext)
                    .load(urls[position])
                    .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder).dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL).override(90,90))
                    .into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            vi.setPadding(10, 10, 10, 10);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return vi;
    }
}
