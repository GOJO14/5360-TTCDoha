package com.goodwill.wholesale.searchsection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.productviewsection.ProductView;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class CustomListAdapter extends ArrayAdapter
{

    @Nullable
    private List<String> dataList;
    private Context mContext;
    private int itemLayout;
    @NonNull
    private ListFilter listFilter = new ListFilter();
    @Nullable
    private List<String> dataListAllItems;

    public CustomListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> storeDataLst) {
        super(context, resource, storeDataLst);
        dataList = storeDataLst;
        mContext = context;
        itemLayout = resource;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public String getItem(int position) {
        Log.d("CustomListAdapter",
                dataList.get(position));
        return dataList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(itemLayout, parent, false);
        }
        String parts[] = getItem(position).split("#");
        TextView strName = view.findViewById(R.id.textView);
        TextView product_id = view.findViewById(R.id.product_id);
        ImageView imageView = view.findViewById(R.id.button);
        Glide.with(mContext)
                .load(parts[1])
                .apply(new RequestOptions().placeholder(R.drawable.tab).error(R.drawable.tab).dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL).encodeFormat(Bitmap.CompressFormat.PNG))
                .into(imageView);
        strName.setText(parts[0]);
        product_id.setText(parts[2]);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProductView.class);
                intent.putExtra("id", parts[2]);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                //mContext.overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
            }
        });

        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    public class ListFilter extends Filter {
        @NonNull
        private Object lock = new Object();

        @NonNull
        @Override
        protected FilterResults performFiltering(@Nullable CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (dataListAllItems == null) {
                synchronized (lock) {
                    dataListAllItems = new ArrayList<String>(dataList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = dataListAllItems;
                    results.count = dataListAllItems.size();
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                ArrayList<String> matchValues = new ArrayList<String>();

                for (String dataItem : dataListAllItems) {
                    if (dataItem.toLowerCase().startsWith(searchStrLowerCase)) {
                        matchValues.add(dataItem);
                    }
                }

                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, @NonNull FilterResults results) {
            if (results.values != null) {
                dataList = (ArrayList<String>) results.values;
            } else {
                dataList = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
}
