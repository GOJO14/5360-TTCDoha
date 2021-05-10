package com.goodwill.wholesale.adaptersection;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.bumptech.glide.request.RequestOptions;
import com.shopify.buy3.Storefront;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.productviewsection.ProductView;
import com.goodwill.wholesale.storagesection.LocalData;
import com.goodwill.wholesale.viewholders.ProductHolder;
import com.goodwill.wholesale.viewholders.RelatedProductViewHolder;

import org.json.JSONObject;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;

public class StaggeredLayoutAdapter extends RecyclerView.Adapter<ProductHolder>
{
    private final List<Storefront.ProductEdge> data;
    private final WeakReference<Activity> context;
    LocalData localData=null;
    public StaggeredLayoutAdapter(Activity context, List<Storefront.ProductEdge> d)
    {
        this.context = new WeakReference<Activity>(context);
        this.data = d;
        localData=new LocalData(context);
    }

    @Override
    public int getItemViewType(int position)
    {
        if(position==0)
        {
            return -15;
        }
        else
        {
            return super.getItemViewType(position);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, final int position)
    {
        try
        {
            if(position==0)
            {
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
                layoutParams.setFullSpan(true);
            }
            else
            {
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
                layoutParams.setFullSpan(false);
            }

            Log.i("getTransformedSrc1",""+data.get(position).getNode().getImages().getEdges());
            Log.i("getTransformedSrc2",""+data.get(position).getNode().getImages().getEdges().get(0));
            Log.i("getTransformedSrc3",""+data.get(position).getNode().getImages().getEdges().get(0).getNode());
            Log.i("getTransformedSrc4",""+data.get(position).getNode().getImages().getEdges().get(0).getNode().getTransformedSrc());

         //   GlideBuilder glideBuilder=    new GlideBuilder(context.get()).setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
            Glide.with(context.get())
                    .load(data.get(position).getNode().getImages().getEdges().get(0).getNode().getTransformedSrc())
                    .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder).dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL).encodeFormat(Bitmap.CompressFormat.PNG))
                    .into(holder.imageview);
            holder.id.setText(data.get(position).getNode().getId().toString());
            holder.imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try
                    {
                        Intent intent = new Intent(context.get(), ProductView.class);
                        intent.putExtra("object",(Serializable)data.get(position));
                        intent.putExtra("id",holder.id.getText().toString());
                        context.get().startActivity(intent);
                        context.get().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
    {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup;
        if(viewType==-15)
        {
            mainGroup = (ViewGroup) mInflater.inflate(R.layout.product_item_big, viewGroup, false);
        }
        else
        {
            mainGroup = (ViewGroup) mInflater.inflate(R.layout.product_item, viewGroup, false);
        }
        return new ProductHolder(mainGroup, context.get());
    }
    @Override
    public int getItemCount() {
        return (null != data ? data.size() : 0);
    }

}
