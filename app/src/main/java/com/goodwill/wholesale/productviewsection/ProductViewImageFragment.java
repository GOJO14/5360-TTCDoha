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
import android.support.annotation.Nullable;
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
public class ProductViewImageFragment extends Fragment
{
    @Nullable
    Unbinder unbinder;
    @Nullable
    @BindView(R.id.MageNative_image)
    ImageView image;
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState)
    {
        View v=null;

        try
        {
            v= inflater.inflate(R.layout.magenative_image, container, false);
            unbinder = ButterKnife.bind(this, v);
            String url = Objects.requireNonNull(getArguments()).getString("url");
            final CharSequence stack[] = getArguments().getCharSequenceArray("stack");
            final int position = getArguments().getInt("position");
            image.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(getActivity(), ZoomImagePagerActivity.class);
                    intent.putExtra("IMAGEURL", stack);
                    intent.putExtra("POS", position);
                    startActivity(intent);
                }
            });
            try
            {
                Glide.with(getActivity())
                        .load(url)
                        .apply(new RequestOptions().placeholder(R.drawable.tab).error(R.drawable.tab).dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(image);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return v;
    }
    @Override public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }
}