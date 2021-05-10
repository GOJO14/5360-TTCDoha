package com.goodwill.wholesale.adaptersection;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.shopify.buy3.Storefront;
import com.goodwill.wholesale.R;
import org.json.JSONObject;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderListAdapter extends BaseAdapter
{
    @Nullable @BindView(R.id.orderviewurl) TextView orderviewurl;
    @Nullable @BindView(R.id.order_id) TextView order_id;
    @Nullable @BindView(R.id.order_date) TextView order_date;
    private final List<Storefront.OrderEdge> array;
    @NonNull
    private final LayoutInflater infalInflater;
    WeakReference<Context> context;
    public OrderListAdapter(Context con, List<Storefront.OrderEdge> array)
    {
        this.context=new WeakReference<Context>(con);
        this.array = array;
        infalInflater = (LayoutInflater) Objects.requireNonNull(context.get().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
    }
    @Override
    public int getCount()
    {
        return array.size();
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
        View vi = null;
        try
        {
            if(convertView == null)
            {
                vi = infalInflater.inflate(R.layout.list_item, parent, false);
            }
            else
            {
                vi = convertView;
            }
            ButterKnife.bind(this,vi);
            order_id.setText(""+array.get(position).getNode().getOrderNumber().toString());
            orderviewurl.setText(array.get(position).getNode().getCustomerUrl());
            SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date expiretime = sdf.parse(array.get(position).getNode().getProcessedAt().toLocalDateTime().toString());
            String time=sdf2.format(expiretime);
            order_date.setText(time);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return vi;
    }

}