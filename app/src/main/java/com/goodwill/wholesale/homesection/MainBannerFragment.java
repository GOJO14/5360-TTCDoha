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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.productlistingsection.ProductListing;
import com.goodwill.wholesale.productviewsection.ProductView;

import java.io.UnsupportedEncodingException;

public class MainBannerFragment extends Fragment
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState)
    {
        View banner = null;
        try
        {
            banner = inflater.inflate(R.layout.magenative_banner_layout, null);
            final ImageView bannerimage= (ImageView) banner.findViewById(R.id.MageNative_bannerimage);
            final TextView banner_link_to= (TextView) banner.findViewById(R.id.link_to);
            banner_link_to.setText(getArguments().getString("link_to"));
            final TextView banner_id= (TextView) banner.findViewById(R.id.id);
            banner_id.setText(getArguments().getString("id"));
            Glide.with(getActivity())
                    .load(getArguments().getString("banner_image"))
                    .apply(new RequestOptions().placeholder(R.drawable.bannerplaceholder).error(R.drawable.bannerplaceholder).dontTransform())
                    .into(bannerimage);
            bannerimage.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    if (banner_link_to.getText().toString().equals("collection"))
                    {
                        String s1 = "gid://shopify/Collection/" + banner_id.getText().toString();
                        Intent intent = new Intent(getActivity(), ProductListing.class);
                        intent.putExtra("cat_id",getBase64Encode(s1));
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                    }
                    if (banner_link_to.getText().toString().equals("product"))
                    {
                        String s1 = "gid://shopify/Product/" + banner_id.getText().toString();
                        Intent prod_link = new Intent(getActivity(), ProductView.class);
                        prod_link.putExtra("id",getBase64Encode(s1));
                        startActivity(prod_link);
                        getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                    }
                    if (banner_link_to.getText().toString().equals("web_address"))
                    {
                        Intent weblink = new Intent(getActivity(), HomeWeblink.class);
                        weblink.putExtra("link", banner_id.getText().toString());
                        startActivity(weblink);
                       getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return  banner;
    }
    public String getBase64Encode(String id)
    {
        byte[] data = Base64.encode(id.getBytes(), Base64.DEFAULT);
        try {
            id = new String(data, "UTF-8").trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return id;
    }

}