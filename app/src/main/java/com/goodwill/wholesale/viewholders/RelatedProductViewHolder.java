package com.goodwill.wholesale.viewholders;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.goodwill.wholesale.R;
/**
 * Created by cedcoss on 2/5/18.
 */
public class RelatedProductViewHolder extends RecyclerView.ViewHolder
{
    public final TextView MageNative_specialprice;
    public final TextView MageNative_reguralprice;
    public final TextView title;
    public final TextView id;
    public final TextView discountsection;
    public final ImageView imageview;
    public final ImageView wishlist;
    public final Spinner variants;
    public final Button addtocart;
    public final EditText quantity;
    public final RelativeLayout main;
    public final LinearLayout pricesection;
    public RelatedProductViewHolder(@NonNull View view, @NonNull final Activity context)
    {
        super(view);
        this.MageNative_specialprice = view.findViewById(R.id.MageNative_specialprice);
        this.MageNative_reguralprice = view.findViewById(R.id.MageNative_reguralprice);
        this.title = view.findViewById(R.id.MageNative_title);
        this.id = view.findViewById(R.id.product_id);
        this.discountsection = view.findViewById(R.id.discountsection);
        this.imageview = view.findViewById(R.id.MageNative_image);
        this.wishlist = view.findViewById(R.id.wishlist);
        this.variants = view.findViewById(R.id.variants);
        this.addtocart = view.findViewById(R.id.addtocart);
        this.quantity = view.findViewById(R.id.quantity);
        this.main = view.findViewById(R.id.main);
        this.pricesection = view.findViewById(R.id.pricesection);
    }
}
