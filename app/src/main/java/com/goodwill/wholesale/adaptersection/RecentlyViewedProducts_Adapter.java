package com.goodwill.wholesale.adaptersection;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shopify.buy3.Storefront;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.productviewsection.ProductView;
import com.goodwill.wholesale.storagesection.LocalData;
import com.goodwill.wholesale.viewholders.RelatedProductViewHolder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;

public class RecentlyViewedProducts_Adapter extends RecyclerView.Adapter<RelatedProductViewHolder>
{
    private final JSONObject data;
    private final WeakReference<Activity> context;
    LocalData localData=null;
    public RecentlyViewedProducts_Adapter(Activity context, JSONObject d)
    {
        this.context = new WeakReference<Activity>(context);
        this.data = d;
        localData=new LocalData(context);
    }

    @Override
    public int getItemViewType(int position)
    {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RelatedProductViewHolder holder, int position)
    {
        try
        {
            holder.title.setText(data.getJSONObject(data.names().getString(position)).getString("name").trim());
            holder.id.setText(data.getJSONObject(data.names().getString(position)).getString("id"));
            if (data.getJSONObject(data.names().getString(position)).getString("special_price").equals("nospecial"))
            {
                holder.MageNative_specialprice.setVisibility(View.GONE);
                holder.MageNative_reguralprice.setText(data.getJSONObject(data.names().getString(position)).getString("price"));
                holder.MageNative_reguralprice.setPaintFlags(holder.MageNative_reguralprice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
            else
            {
                holder.MageNative_reguralprice.setText(data.getJSONObject(data.names().getString(position)).getString("price"));
                holder.MageNative_reguralprice.setPaintFlags(holder.MageNative_reguralprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.MageNative_reguralprice.setTextColor(context.get().getResources().getColor(R.color.black));
                holder.MageNative_specialprice.setVisibility(View.VISIBLE);
                holder.MageNative_specialprice.setText(data.getJSONObject(data.names().getString(position)).getString("special_price"));
            }
            Glide.with(context.get())
                    .load(data.getJSONObject(data.names().getString(position)).getString("image")).
            apply(new RequestOptions().placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontTransform()
                    .error(R.drawable.placeholder))
                    .into(holder.imageview);
            holder.imageview.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent=new Intent(context.get(), ProductView.class);
                    intent.putExtra("id",holder.id.getText().toString());
                    context.get().startActivity(intent);
                    context.get().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                }
            });
            holder.wishlist.setVisibility(View.GONE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public RelatedProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
    {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.magenative_product_list_item, viewGroup, false);
        return new RelatedProductViewHolder(mainGroup, context.get());
    }
    @Override
    public int getItemCount()
    {
        return  (null != data ? data.length() : 0);
    }

}
