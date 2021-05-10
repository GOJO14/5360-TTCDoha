package com.goodwill.wholesale.viewholders;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.goodwill.wholesale.R;

/**
 * Created by cedcoss on 2/5/18.
 */
public class ProductHolder extends RecyclerView.ViewHolder
{
    public final TextView id;
    public final ImageView imageview;
    public ProductHolder(@NonNull View view, @NonNull final Activity context)
    {
        super(view);
        this.id = view.findViewById(R.id.product_id);
        this.imageview = view.findViewById(R.id.image);
    }
}
