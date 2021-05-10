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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.goodwill.wholesale.R;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
public class ShowZoomImageFragment extends Fragment
{
    Unbinder unbinder;
    @BindView(R.id.MageNative_image) ScaleImageView bmImage;
    @BindView(R.id.MageNative_cross) ImageView cross;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        String currenturl = Objects.requireNonNull(getArguments()).getString("current");
        final View v = inflater.inflate(R.layout.magenative_fragment_show_zoom_image, container, false);
        unbinder = ButterKnife.bind(this, v);
        cross.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
        Glide.with(getActivity())
                .asBitmap()
                .load(currenturl)
                .apply(new RequestOptions().placeholder(R.drawable.tab).error(R.drawable.tab).dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(bmImage);
        return v;
    }
    @Override public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }
}
