package com.goodwill.wholesale.adaptersection;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.currencysection.CurrencyFormatter;
import com.goodwill.wholesale.storagesection.LocalData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Product_Adapter_Tags extends BaseAdapter
{
    @Nullable private static LayoutInflater inflater = null;
    private final WeakReference<Activity> activity;
    private final JSONArray data;
    @NonNull private final LocalData management;
    @Nullable @BindView(R.id.product_id)
    TextView product_id;
    @Nullable @BindView(R.id.MageNative_reguralprice)
    TextView MageNative_reguralprice;
    @Nullable @BindView(R.id.MageNative_specialprice)
    TextView MageNative_specialprice;
    @Nullable @BindView(R.id.MageNative_title)
    TextView MageNative_title;
    @Nullable @BindView(R.id.MageNative_image)
    ImageView MageNative_image;
    String currency_symbol;
    View vi=null;
    GridView grid;
    public Product_Adapter_Tags(Activity a, JSONArray d, String currency_symbol, GridView grid)
    {
        activity = new WeakReference<Activity>(a);
        data = d;
        inflater = (LayoutInflater) activity.get().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        management = new LocalData(activity.get());
        this.currency_symbol=currency_symbol;
        this.grid=grid;
    }
    @Override
    public int getCount() {
        return data.length();
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
    public View getView(final int position, @Nullable View convertView, ViewGroup parent) {
        if (convertView == null) {
            vi = Objects.requireNonNull(inflater).inflate(R.layout.magenative_product_list_item, parent, false);
        } else {
            vi = convertView;
        }
        final ImageView wishlist= vi.findViewById(R.id.wishlist);
        ButterKnife.bind(this, vi);
        try
        {
            if(management.getWishList()!=null)
            {
                JSONObject object=new JSONObject(management.getWishList());
                if(object.has( getBase64Encode("gid://shopify/Product/"+data.getJSONObject(position).getString("id"))))
                {
                    wishlist.setImageResource(R.drawable.wishred);
                }
                else
                {
                    wishlist.setImageResource(R.drawable.wishlike);
                }
            }
            else
            {
                wishlist.setImageResource(R.drawable.wishlike);
            }
            MageNative_title.setText(data.getJSONObject(position).getString("title").trim());
            product_id.setText(getBase64Encode("gid://shopify/Product/"+data.getJSONObject(position).getString("id")));
            //Log.i("Wishlist",""+data.get(position).getNode().getVariants().getEdges().size());
            String regularprice= CurrencyFormatter.setsymbol(BigDecimal.valueOf(Double.valueOf(data.getJSONObject(position).getString("price").replace(",",""))),currency_symbol);
            if (data.getJSONObject(position).getString("compare_at_price")==null)
            {
                MageNative_specialprice.setVisibility(View.GONE);
                MageNative_reguralprice.setText(regularprice);
                MageNative_reguralprice.setPaintFlags(MageNative_reguralprice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
            else
            {
                if(data.getJSONObject(position).getString("compare_at_price").compareTo(data.getJSONObject(position).getString("price"))==1)
                {
                    String specialprice= CurrencyFormatter.setsymbol(BigDecimal.valueOf(Double.valueOf(data.getJSONObject(position).getString("compare_at_price").replace(",",""))),currency_symbol);
                    MageNative_reguralprice.setText(specialprice);
                    MageNative_reguralprice.setPaintFlags(MageNative_reguralprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    MageNative_reguralprice.setTextColor(activity.get().getResources().getColor(R.color.black));
                    MageNative_specialprice.setVisibility(View.VISIBLE);
                    MageNative_specialprice.setText(regularprice);
                }
                else
                {
                    MageNative_specialprice.setVisibility(View.GONE);
                    MageNative_reguralprice.setText(regularprice);
                    MageNative_reguralprice.setPaintFlags(MageNative_reguralprice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }

            Glide.with(activity.get())
                    .load("https:"+data.getJSONObject(position).getString("featured_image"))
                    .apply(new RequestOptions()
                            //.placeholder(R.drawable.loadinggif)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .dontTransform()
                            //.error(R.drawable.loadinggif)
                    )
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //loadingimage.setVisibility(View.VISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                           // loadingimage.setVisibility(View.GONE);
                            return false;
                        }

                    })
                    .into(MageNative_image);

            wishlist.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    processwishlist(wishlist,position);
                }
            });

            /*if (data.getJSONObject(position).getBoolean("available")) {
                outofstock.setVisibility(View.GONE);
                overlay.setVisibility(View.GONE);
            }
            else
            {
                outofstock.setVisibility(View.VISIBLE);
                overlay.setVisibility(View.GONE);
            }*/
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return vi;
    }

    private void processwishlist(ImageView wishlist, int position)
    {
        try
        {
            Log.i("Wishlist",""+position);
            int varinats=data.getJSONObject(position).getJSONArray("variants").length();
            Log.i("Wishlist",""+varinats);
            String varinatid= getBase64Encode("gid://shopify/Variant/"+data.getJSONObject(position).getJSONArray("variants").getJSONObject(0).getString("id"));
            if(management.getWishList()!=null)
            {
                JSONObject object=new JSONObject((management.getWishList()));
                if(object.has(getBase64Encode("gid://shopify/Product/"+data.getJSONObject(position).getString("id"))))
                {
                    Log.i("Wishlist","Three");
                    object.remove(getBase64Encode("gid://shopify/Product/"+data.getJSONObject(position).getString("id")));
                    management.saveWishList(object);
                    wishlist.setImageResource(R.drawable.wishlike);
                }
                else
                {
                    Log.i("Wishlist","TWO");
                    JSONObject subobject=new JSONObject();
                    subobject.put("product_id",getBase64Encode("gid://shopify/Product/"+data.getJSONObject(position).getString("id")));
                    subobject.put("product_name",data.getJSONObject(position).getString("title").trim());
                    subobject.put("varinats",varinats);
                    subobject.put("varinatid",varinatid);
                    subobject.put("image","https:"+data.getJSONObject(position).getString("featured_image"));
                    object.put(data.getJSONObject(position).getString("id"),subobject);
                    management.saveWishList(object);
                    wishlist.setImageResource(R.drawable.wishred);
                }
            }
            else
            {
                Log.i("Wishlist","First");
                JSONObject object=new JSONObject();
                JSONObject subobject=new JSONObject();
                subobject.put("product_id",getBase64Encode("gid://shopify/Product/"+data.getJSONObject(position).getString("id")));
                subobject.put("product_name",data.getJSONObject(position).getString("title").trim());
                subobject.put("varinats",varinats);
                subobject.put("varinatid",varinatid);
                subobject.put("image","https:"+data.getJSONObject(position).getString("featured_image"));
                object.put(data.getJSONObject(position).getString("id"),subobject);
                management.saveWishList(object);
                wishlist.setImageResource(R.drawable.wishred);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private String getBase64Encode(String id)
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
