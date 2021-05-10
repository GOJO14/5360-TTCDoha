package com.goodwill.wholesale.adaptersection;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.goodwill.wholesale.checkoutsection.CartListing;
import com.shopify.buy3.Storefront;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.currencysection.CurrencyFormatter;
import com.goodwill.wholesale.maincontainer.MainActivity;
import com.goodwill.wholesale.productlistingsection.ProductListing;
import com.goodwill.wholesale.productviewsection.ProductView;
import com.goodwill.wholesale.storagesection.LocalData;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;
public class Product_Adapter extends BaseAdapter
{
    @Nullable
    private static LayoutInflater inflater = null;
    private final WeakReference<Activity> activity;
    private final List<Storefront.ProductEdge> data;
    @NonNull
    private final LocalData management;
    @Nullable @BindView(R.id.product_id) TextView product_id;
    @Nullable @BindView(R.id.MageNative_reguralprice) TextView MageNative_reguralprice;
    @Nullable @BindView(R.id.MageNative_specialprice) TextView MageNative_specialprice;
    @Nullable @BindView(R.id.MageNative_title) TextView MageNative_title;
    @Nullable @BindView(R.id.MageNative_image) ImageView MageNative_image;
    @Nullable @BindView(R.id.varianttext) TextView varianttext;
    @Nullable @BindView(R.id.addtocart)
    Button addtocart;
    @Nullable @BindView(R.id.variants)
    Spinner variantsspnr;
    @Nullable @BindView(R.id.quantity)
    EditText quantity;
    String currency_symbol;
    View vi=null;
    GridView grid;
    LocalData localData;
    public Product_Adapter(Activity a, List<Storefront.ProductEdge> d, String currency_symbol, GridView grid)
    {
        activity = new WeakReference<Activity>(a);
        data = d;
        inflater = (LayoutInflater) activity.get().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        management = new LocalData(activity.get());
        this.currency_symbol=currency_symbol;
        this.grid=grid;
        localData=new LocalData(activity.get());

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
    public View getView(final int position, @Nullable View convertView, ViewGroup parent) {

        String variant_id="";
        if (convertView == null) {
            vi = Objects.requireNonNull(inflater).inflate(R.layout.magenative_product_list_item_homepage, parent, false);
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
                if(object.has(data.get(position).getNode().getId().toString()))
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


            MageNative_title.setText(data.get(position).getNode().getTitle().toString());
            product_id.setText(data.get(position).getNode().getTitle().toString());
            Log.i("Wishlist",""+data.get(position).getNode().getVariants().getEdges().size());
            String regularprice= CurrencyFormatter.setsymbol(data.get(position).getNode().getVariants().getEdges().get(0).getNode().getPrice(),currency_symbol);
            if (data.get(position).getNode().getVariants().getEdges().get(0).getNode().getCompareAtPrice()==null)
            {
                MageNative_specialprice.setVisibility(View.GONE);
                MageNative_reguralprice.setText(regularprice);
                MageNative_reguralprice.setPaintFlags(MageNative_reguralprice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
            else
            {
                if(data.get(position).getNode().getVariants().getEdges().get(0).getNode().getCompareAtPrice().compareTo(data.get(position).getNode().getVariants().getEdges().get(0).getNode().getPrice())==1)
                {
                    String specialprice= CurrencyFormatter.setsymbol(data.get(position).getNode().getVariants().getEdges().get(0).getNode().getCompareAtPrice(),currency_symbol);
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
                    .load(data.get(position).getNode().getImages().getEdges().get(0).getNode().getTransformedSrc())
                    .apply(new RequestOptions().centerCrop().placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .dontTransform()
                            .error(R.drawable.placeholder))
                    .into(MageNative_image);
            wishlist.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) 
                {
                    processwishlist(wishlist,position);
                }
            });


            /***************************************************************************************************************/
            Storefront.ProductVariantConnection variants=data.get(position).getNode().getVariants();
            List<Storefront.ProductVariantEdge> productvariantedge = variants.getEdges();
            Log.i("productvariantedge",""+productvariantedge);
            Log.i("productvariantedge",""+productvariantedge.size());
            int totalvariantsize=productvariantedge.size();

            if(productvariantedge!=null)
            {
                Log.i("productvariantedge",""+productvariantedge.size());
                Iterator iterator=productvariantedge.iterator();
                boolean first=true;
                View variantoption=null;
                RelativeLayout mainlayout;
                ImageView image=null;
                ImageView tick_image=null;
                TextView variantid=null;
                TextView variantimage=null;
                TextView selectedoption1=null;
                TextView selectedoption2=null;
                TextView selectedoption3=null;
                Storefront.ProductVariantEdge edge=null;
                List<Storefront.SelectedOption> selctedoption=null;
                Storefront.SelectedOption option=null;
                List<String> categories = categories = new ArrayList<String>();
                String title ="";
                while (iterator.hasNext())
                {
                    edge = (Storefront.ProductVariantEdge) iterator.next();

                    if(edge.getNode().getAvailableForSale())
                    {
                        selctedoption=  edge.getNode().getSelectedOptions();
                        if(productvariantedge.size()>=1)
                        {
                            if (productvariantedge.size() ==1){
                                variantsspnr.setBackground(activity.get().getResources().getDrawable(R.drawable.product_corners));
                            }

                            Iterator iterator1=selctedoption.iterator();
                            int counter=0;

                            while (iterator1.hasNext())
                            {
                                counter=counter+1;
                                option= (Storefront.SelectedOption) iterator1.next();
                                //String finalvalue=option.getName()+" : "+option.getValue();
                                String finalvalue=option.getValue();

                                title = data.get(position).getNode().getTitle().trim();
                                HashMap<String,String> mapPrice = null;

                                if(counter==1)
                                {
                                    MainActivity.mapVariantsIds.put(title+finalvalue,edge.getNode().getId().toString());
                                    mapPrice = new HashMap();
                                    mapPrice.put(edge.getNode().getCompareAtPrice().toString(),edge.getNode().getPrice().toString());
                                    MainActivity.mapVariantsPrices.put(title+finalvalue,mapPrice);
                                    Log.d("productvariantedge",""+finalvalue);
                                    categories.add(finalvalue);


                                }
                                if(counter==2)
                                {
                                    MainActivity.mapVariantsIds.put(title+finalvalue,edge.getNode().getId().toString());
                                    mapPrice = new HashMap();
                                    mapPrice.put(edge.getNode().getCompareAtPrice().toString(),edge.getNode().getPrice().toString());
                                    MainActivity.mapVariantsPrices.put(title+finalvalue,mapPrice);
                                    Log.d("productvariantedge",""+finalvalue);
                                    categories.add(finalvalue);

                                }
                                if(counter==3)
                                {
                                    MainActivity.mapVariantsIds.put(title+finalvalue,edge.getNode().getId().toString());
                                    mapPrice = new HashMap();
                                    mapPrice.put(edge.getNode().getCompareAtPrice().toString(),edge.getNode().getPrice().toString());
                                    MainActivity.mapVariantsPrices.put(title+finalvalue,mapPrice);
                                    Log.d("productvariantedge",""+finalvalue);
                                    categories.add(finalvalue);

                                }
                            }
                        }
                        else
                        {
                            variant_id=edge.getNode().getId().toString();
                            varianttext.setText(title);
                           // varianttext.setVisibility(View.VISIBLE);
                        }
                    }
                    else
                    {
                        if(totalvariantsize==1)
                        {

                            addtocart.setText(activity.get().getResources().getString(R.string.outofstock));
                            addtocart.setEnabled(false);
                        }
                    }
                }

                Log.d("categories",data.get(position).getNode().getTitle().trim()+" - "+categories);
                Log.d("categoriesvarinats",""+MainActivity.mapVariantsIds);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity.get(), android.R.layout.simple_spinner_item, categories);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                variantsspnr.setAdapter(dataAdapter);
                final String[] finalVariant_id = {variant_id};
                final HashMap<String, String>[] map = new HashMap[]{null};
                variantsspnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        Log.d("SelectedVarId*",""+variantsspnr.getSelectedItem());
                        String varId = MageNative_title.getText().toString()+variantsspnr.getSelectedItem();
                        finalVariant_id[0] = MainActivity.mapVariantsIds.get(varId);
                        Log.d("SelectedVarId*",""+varId);
                        Log.d("SelectedVarId",""+finalVariant_id[0]);

                        map[0] = MainActivity.mapVariantsPrices.get(varId);
                        if (!map[0].containsKey("special"))
                        {
                            MageNative_specialprice.setVisibility(View.GONE);
                            MageNative_reguralprice.setText(localData.getMoneyFormat()+" "+map[0].get("normal"));
                            MageNative_reguralprice.setPaintFlags(MageNative_reguralprice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        }
                        else
                        {
                            if(map[0].get("special").compareTo(map[0].get("normal"))==1)
                            {
                                MageNative_reguralprice.setText(localData.getMoneyFormat()+" "+map[0].get("special"));
                                MageNative_reguralprice.setPaintFlags(MageNative_reguralprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                MageNative_reguralprice.setTextColor(activity.get().getResources().getColor(R.color.black));
                                MageNative_specialprice.setVisibility(View.VISIBLE);
                                MageNative_specialprice.setText(localData.getMoneyFormat()+map[0].get("normal"));
                            }
                            else
                            {
                                MageNative_specialprice.setVisibility(View.GONE);
                                MageNative_reguralprice.setText(localData.getMoneyFormat()+" "+map[0].get("normal"));
                                MageNative_reguralprice.setPaintFlags(MageNative_reguralprice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                            }

                        }

                        Log.d("SelectedVarIdPrice",""+map[0]);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                addtocart.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        try
                        {
                            MainActivity.hideKeyboard(activity.get());

                            if(finalVariant_id[0].isEmpty()||quantity.getText().toString().isEmpty())
                            {
                                Toast.makeText(activity.get(),activity.get().getResources().getString(R.string.selectvariant),Toast.LENGTH_LONG).show();
                            }
                            else
                            {

                                if(localData.getWishList()!=null)
                                {
                                    JSONObject jsonObject=new JSONObject(localData.getWishList());
                                    if(jsonObject.has(data.get(position).getNode().getId().toString()))
                                    {
                                        jsonObject.remove(data.get(position).getNode().getId().toString());
                                        localData.saveWishList(jsonObject);
                                    }
                                }
                                if(localData.getCheckoutId()!=null)
                                {
                                    localData.clearCheckoutId();
                                    localData.clearCoupon();
                                }
                                if(localData.getLineItems()!=null)
                                {
                                    int qty=1;
                                    JSONObject object=new JSONObject(localData.getLineItems());
                                    if(object.has(finalVariant_id[0]))
                                    {
                                        qty= Integer.parseInt(object.getString(finalVariant_id[0]));
                                        qty=qty+1;
                                    }
                                    object.put(finalVariant_id[0],quantity.getText().toString());
                                    localData.saveLineItems(object);
                                }
                                else
                                {
                                    JSONObject object=new JSONObject();
                                    object.put(finalVariant_id[0],quantity.getText().toString());
                                    localData.saveLineItems(object);
                                }
                                String message=data.get(position).getNode().getTitle().trim()+" "+ activity.get().getResources().getString(R.string.addedtocart);
                                Snackbar snackbar = Snackbar.make(activity.get().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
                                snackbar.show();
                                //changecount();

                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                });
            }
            /***************************************************************************************************************/


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity.get(), ProductView.class);
                intent.putExtra("id", data.get(position).getNode().getId());
                intent.putExtra("object",(Serializable)data.get(position));
                intent.putExtra("position",position);
                activity.get().startActivity(intent);
                activity.get().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
            }
        });

        Log.d("ProductTags Adapter",""+data.get(position).getNode().getTags());
        return vi;
    }

    private void processwishlist(ImageView wishlist,int position)
    {
        try
        {
            Log.i("Wishlist",""+position);
            int varinats=data.get(position).getNode().getVariants().getEdges().size();
            List<Storefront.ProductVariantEdge> variantsList =data.get(position).getNode().getVariants().getEdges();
            Log.i("Wishlist",""+varinats);
            String varinatid=data.get(position).getNode().getVariants().getEdges().get(0).getNode().getId().toString();
            if(management.getWishList()!=null)
            {
                JSONObject object=new JSONObject((management.getWishList()));
                if(object.has(data.get(position).getNode().getId().toString()))
                {
                    Log.i("Wishlist","Three");
                    object.remove(data.get(position).getNode().getId().toString());
                    management.saveWishList(object);
                    wishlist.setImageResource(R.drawable.wishlike);
                }
                else
                {
                    Log.i("Wishlist","TWO");
                    JSONObject subobject=new JSONObject();
                    subobject.put("product_id",data.get(position).getNode().getId().toString());
                    subobject.put("product_name",data.get(position).getNode().getTitle().trim());
                    subobject.put("product_price",data.get(position).getNode().getVariants().getEdges().get(0).getNode().getPrice());
                    subobject.put("product_specialprice",data.get(position).getNode().getVariants().getEdges().get(0).getNode().getCompareAtPrice());
                    subobject.put("varinats",varinats);
                    subobject.put("variantsList",variantsList);
                    subobject.put("varinatid",varinatid);
                    subobject.put("image",data.get(position).getNode().getImages().getEdges().get(0).getNode().getTransformedSrc());
                    object.put(data.get(position).getNode().getId().toString(),subobject);
                    management.saveWishList(object);
                    wishlist.setImageResource(R.drawable.wishred);
                }
            }
            else
            {
                Log.i("Wishlist","First");
                JSONObject object=new JSONObject();
                JSONObject subobject=new JSONObject();
                subobject.put("product_id",data.get(position).getNode().getId().toString());
                subobject.put("product_name",data.get(position).getNode().getTitle().trim());
                subobject.put("product_price",data.get(position).getNode().getVariants().getEdges().get(0).getNode().getPrice());
                subobject.put("product_specialprice",data.get(position).getNode().getVariants().getEdges().get(0).getNode().getCompareAtPrice());
                subobject.put("varinats",varinats);
                subobject.put("variantsList",variantsList);
                subobject.put("varinatid",varinatid);
                subobject.put("image",data.get(position).getNode().getImages().getEdges().get(0).getNode().getTransformedSrc());
                object.put(data.get(position).getNode().getId().toString(),subobject);
                management.saveWishList(object);
                wishlist.setImageResource(R.drawable.wishred);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }




}