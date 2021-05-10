package com.goodwill.wholesale.adaptersection;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.shopify.buy3.Storefront;
import com.goodwill.wholesale.R;
import org.json.JSONArray;
import java.lang.ref.WeakReference;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;
public class Sort_Adapter extends BaseAdapter {
    @Nullable
    private static LayoutInflater inflater = null;
    private final WeakReference<Context> activity;
    private final JSONArray data;
    private final int sortkey;
    @Nullable
    @BindView(R.id.MageNative_SortLabel)
    TextView text;

    public Sort_Adapter(Context a, JSONArray data, int sortkey) {
        activity = new WeakReference<Context>(a);
        this.data = data;
        this.sortkey = sortkey;
        inflater = (LayoutInflater) activity.get().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.length();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    @Nullable
    @SuppressLint("NewApi")
    public View getView(int position, @Nullable View convertView, ViewGroup parent)
    {
        View vi = convertView;
        try
        {
            if (convertView == null)
            {
                vi = Objects.requireNonNull(inflater).inflate(R.layout.magenative_sort_components, parent, false);
            }
            else
            {
                vi = convertView;
            }
            ButterKnife.bind(this, vi);
            text.setText(data.getString(position).toLowerCase());
            if (position==sortkey)
            {
                vi.setBackgroundColor(activity.get().getResources().getColor(R.color.red));
            }
            else
            {
                vi.setBackgroundColor(activity.get().getResources().getColor(R.color.white));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vi;
    }
}