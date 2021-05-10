package com.goodwill.wholesale.adaptersection;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shopify.buy3.Storefront;
import com.goodwill.wholesale.R;

import org.json.JSONArray;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Collections_Adapter extends BaseAdapter
{
    @Nullable
    private static LayoutInflater inflater = null;
    private WeakReference<Activity>  activity;
    private final List<Storefront.CollectionEdge> data;
    @Nullable
    @BindView(R.id.cat_title) TextView cat_title;
    @Nullable
    @BindView(R.id.cat_image) ImageView cat_image;
    @Nullable
    @BindView(R.id.cat_id) TextView cat_id;
    public Collections_Adapter(Activity a, List<Storefront.CollectionEdge> d)
    {
        activity = new WeakReference<Activity>(a);
        data = d;
        inflater = (LayoutInflater) activity.get().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return data.size();
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
        View vi;
        if (convertView == null)
        {
          vi = Objects.requireNonNull(inflater).inflate(R.layout.magenative_category_listing, parent, false);
        }
        else
        {
            vi = convertView;
        }
        ButterKnife.bind(this, vi);
        try
        {
            cat_title.setText(data.get(position).getNode().getTitle().toUpperCase());
            String url="";
            if(data.get(position).getNode().getImage()!=null)
            {
                url=data.get(position).getNode().getImage().getOriginalSrc();
                if(url.contains(".png"))
                {
                    url=url.replace(".png","_700X300.png");
                }
                if(url.contains(".jpg"))
                {
                    url=url.replace(".jpg","_700X300.jpg");
                }
                if(url.contains(".jpeg"))
                {
                    url=url.replace(".jpeg","_700X300.jpeg");
                }
            }
            Glide.with(activity.get())
                        .load(url)
                        .thumbnail(0.5f)
                        .apply(new RequestOptions().placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL)
                                .dontTransform()
                                .error(R.drawable.placeholder))
                        .into(cat_image);
            cat_id.setText(data.get(position).getNode().getId().toString());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return vi;
    }
}
