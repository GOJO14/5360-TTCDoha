package com.goodwill.wholesale.adaptersection;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shopify.buy3.Storefront;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.checkoutsection.CheckoutLineItems;
import com.goodwill.wholesale.currencysection.CurrencyFormatter;
import com.goodwill.wholesale.maincontainer.MainActivity;
import com.goodwill.wholesale.productlistingsection.ProductListing;
import com.goodwill.wholesale.productviewsection.ProductView;
import com.goodwill.wholesale.requestsection.ApiClient;
import com.goodwill.wholesale.requestsection.ApiInterface;
import com.goodwill.wholesale.storagesection.LocalData;
import com.goodwill.wholesale.storefrontresponse.AsyncResponse;
import com.goodwill.wholesale.storefrontresponse.Response;
import com.goodwill.wholesale.viewholders.RelatedProductViewHolder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.goodwill.wholesale.maincontainer.MainActivity.cart_discount;

public class RelatedProducts_Adapter extends RecyclerView.Adapter<RelatedProductViewHolder>
{
    private final List<Storefront.ProductEdge> data;
    private final WeakReference<Activity> context;
    LocalData localData=null;
    String catID="";
    int pos;
    boolean homepage=true;
    boolean showPriceSection=true;
    CheckoutLineItems items=null;
    public RelatedProducts_Adapter(Activity context, List<Storefront.ProductEdge> d,String catID,int pos)
    {
        this.context = new WeakReference<Activity>(context);
        this.data = d;
        localData=new LocalData(context);
        this.catID=catID;
        this.pos=pos;
        homepage=false;
        showPriceSection=false;
        items=new CheckoutLineItems(context.getBaseContext());
    }
    public RelatedProducts_Adapter(Activity context, List<Storefront.ProductEdge> d, boolean showPriceSection)
    {
        this.context = new WeakReference<Activity>(context);
        this.data = d;
        localData=new LocalData(context);
        homepage=false;
        this.showPriceSection=showPriceSection;
        items=new CheckoutLineItems(context.getBaseContext());
    }

    @Override
    public int getItemViewType(int position)
    {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RelatedProductViewHolder holder, int position)
    {
        String variant_id="";
        try
        {

            if(localData.getWishList()!=null)
            {
                JSONObject object=new JSONObject(localData.getWishList());
                if(object.has(data.get(position).getNode().getId().toString()))
                {
                    holder.wishlist.setImageResource(R.drawable.wishred);
                }
                else
                {
                    holder.wishlist.setImageResource(R.drawable.wishlike);
                }
            }
            holder.title.setText(data.get(position).getNode().getTitle().trim());
            holder.id.setText(data.get(position).getNode().getId().toString());
            String regularprice= CurrencyFormatter.setsymbol(data.get(position).getNode().getVariants().getEdges().get(0).getNode().getPrice(),localData.getMoneyFormat());
            String productId= MainActivity.getBase64Decode(data.get(position).getNode().getId().toString());
            holder.discountsection.setText("");

            if (cart_discount!=null){
                if (cart_discount.containsKey(productId)){
                    holder.discountsection.setText(Html.fromHtml(cart_discount.get(productId)));
                    holder.discountsection.setVisibility(View.VISIBLE);
                }
               /* else {
                    holder.discountsection.setVisibility(View.GONE);
                }*/
                Log.e("dohacheck",cart_discount.containsKey(productId)+" -- "+data.get(position).getNode().getTitle().trim()+" -- "+cart_discount.get(productId));

            }else {
                holder.discountsection.setVisibility(View.GONE);
            }


            if (data.get(position).getNode().getVariants().getEdges().get(0).getNode().getCompareAtPrice()==null)
            {
                holder.MageNative_specialprice.setVisibility(View.GONE);
                holder.MageNative_reguralprice.setText(regularprice);
                holder.MageNative_reguralprice.setPaintFlags(holder.MageNative_reguralprice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
            else
            {
                if(data.get(position).getNode().getVariants().getEdges().get(0).getNode().getCompareAtPrice().compareTo(data.get(position).getNode().getVariants().getEdges().get(0).getNode().getPrice())==1)
                {
                    String specialprice=CurrencyFormatter.setsymbol(data.get(position).getNode().getVariants().getEdges().get(0).getNode().getCompareAtPrice(),localData.getMoneyFormat());
                    holder.MageNative_reguralprice.setText(specialprice);
                    holder.MageNative_reguralprice.setPaintFlags(holder.MageNative_reguralprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.MageNative_reguralprice.setTextColor(context.get().getResources().getColor(R.color.black));
                    holder.MageNative_specialprice.setVisibility(View.VISIBLE);
                    holder.MageNative_specialprice.setText(regularprice);
                }
                else
                {
                    holder.MageNative_specialprice.setVisibility(View.GONE);
                    holder.MageNative_reguralprice.setText(regularprice);
                    holder.MageNative_reguralprice.setPaintFlags(holder.MageNative_reguralprice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }

            }

            Glide.with(context.get())
                    .load(data.get(position).getNode().getImages().getEdges().get(0).getNode().getTransformedSrc())
                    .apply(new RequestOptions().placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .dontTransform()
                            .error(R.drawable.placeholder))
                    .into(holder.imageview);
            holder.main.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent=new Intent(context.get(), ProductView.class);
                    intent.putExtra("object",(Serializable)data.get(position));
                    intent.putExtra("id",holder.id.getText().toString());
                    if(homepage)
                    {
                        intent.putExtra("catID",catID);
                        intent.putExtra("position",pos+1);
                    }
                    context.get().startActivity(intent);
                    context.get().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                }
            });
            holder.wishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    processwishlist(holder.wishlist,holder.id.getText().toString(),position);
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
                Storefront.ProductVariantEdge edge=null;
                List<Storefront.SelectedOption> selctedoption=null;
                Storefront.SelectedOption option=null;
                List<String> categories = new ArrayList<String>();
                while (iterator.hasNext())
                {
                    edge = (Storefront.ProductVariantEdge) iterator.next();
                    if(edge.getNode().getAvailableForSale())
                    {
                        selctedoption=  edge.getNode().getSelectedOptions();
                        if(productvariantedge.size()>=1)
                        {


                            Iterator iterator1=selctedoption.iterator();
                            int counter=0;
                            while (iterator1.hasNext())
                            {
                                counter=counter+1;
                                option= (Storefront.SelectedOption) iterator1.next();
                                //String finalvalue=option.getName()+" : "+option.getValue();
                                String finalvalue=option.getValue();

                                String title = data.get(position).getNode().getTitle().trim();
                                HashMap<String,String> mapPrice = null;

                                if(counter==1)
                                {
                                    MainActivity.mapVariantsIds.put(title+finalvalue,edge.getNode().getId().toString());
                                    mapPrice = new HashMap();
                                    if (edge.getNode().getCompareAtPrice()!=null){
                                        mapPrice.put("special",edge.getNode().getCompareAtPrice().toString());
                                        mapPrice.put("normal",edge.getNode().getPrice().toString());
                                    }else {
                                        mapPrice.put("normal",edge.getNode().getPrice().toString());
                                    }
                                    MainActivity.mapVariantsPrices.put(title+finalvalue,mapPrice);
                                    Log.d("productvariantedge",""+finalvalue);
                                    categories.add(finalvalue);


                                }
                                if(counter==2)
                                {
                                    MainActivity.mapVariantsIds.put(title+finalvalue,edge.getNode().getId().toString());
                                    mapPrice = new HashMap();
                                    if (edge.getNode().getCompareAtPrice()!=null){
                                        mapPrice.put("special",edge.getNode().getCompareAtPrice().toString());
                                        mapPrice.put("normal",edge.getNode().getPrice().toString());
                                    }else {
                                        mapPrice.put("normal",edge.getNode().getPrice().toString());
                                    }
                                    MainActivity.mapVariantsPrices.put(title+finalvalue,mapPrice);
                                    Log.d("productvariantedge",""+finalvalue);
                                    categories.add(finalvalue);

                                }
                                if(counter==3)
                                {
                                    MainActivity.mapVariantsIds.put(title+finalvalue,edge.getNode().getId().toString());
                                    mapPrice = new HashMap();
                                    if (edge.getNode().getCompareAtPrice()!=null){
                                        mapPrice.put("special",edge.getNode().getCompareAtPrice().toString());
                                        mapPrice.put("normal",edge.getNode().getPrice().toString());
                                    }else {
                                        mapPrice.put("normal",edge.getNode().getPrice().toString());
                                    }
                                    MainActivity.mapVariantsPrices.put(title+finalvalue,mapPrice);


                                    Log.d("productvariantedge",""+finalvalue);
                                    categories.add(finalvalue);

                                }
                            }


                        }
                        else
                        {
                            variant_id=edge.getNode().getId().toString();
                            holder.variants.setVisibility(View.GONE);
                        }

                        if (productvariantedge.size() ==1){
                            holder.variants.setBackground(context.get().getResources().getDrawable(R.drawable.product_corners));
                        }else {
                            holder.variants.setBackground(context.get().getResources().getDrawable(R.drawable.spnr_product_corners));
                        }
                    }
                    else
                    {
                        if(totalvariantsize==1)
                        {

                            holder.addtocart.setText(context.get().getResources().getString(R.string.outofstock));
                            holder.addtocart.setEnabled(false);
                            holder.variants.setVisibility(View.GONE);
                        }
                    }
                }

                Log.d("categories",data.get(position).getNode().getTitle().trim()+" - "+categories);
                Log.d("categoriesvarinats",""+MainActivity.mapVariantsIds);

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context.get(), R.layout.text, categories);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                holder.variants.setAdapter(dataAdapter);
                final String[] finalVariant_id = {variant_id};
                final HashMap<String, String>[] map = new HashMap[]{null};
                holder.variants.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String varId = holder.title.getText().toString()+holder.variants.getSelectedItem();
                        finalVariant_id[0] = MainActivity.mapVariantsIds.get(varId);
                        Log.d("SelectedVarId*",""+varId);
                        Log.d("SelectedVarId",""+finalVariant_id[0]);

                        map[0] = MainActivity.mapVariantsPrices.get(varId);

                        if (!map[0].containsKey("special"))
                        {
                            holder.MageNative_specialprice.setVisibility(View.GONE);
                            holder.MageNative_reguralprice.setText(localData.getMoneyFormat()+" "+map[0].get("normal"));
                            holder.MageNative_reguralprice.setPaintFlags(holder.MageNative_reguralprice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        }
                        else
                        {
                            if(map[0].get("special").compareTo(map[0].get("normal"))==1)
                            {
                                holder.MageNative_reguralprice.setText(localData.getMoneyFormat()+" "+map[0].get("special"));
                                holder.MageNative_reguralprice.setPaintFlags(holder.MageNative_reguralprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                holder.MageNative_reguralprice.setTextColor(context.get().getResources().getColor(R.color.black));
                                holder.MageNative_specialprice.setVisibility(View.VISIBLE);
                                holder.MageNative_specialprice.setText(localData.getMoneyFormat()+" "+map[0].get("normal"));
                            }
                            else
                            {
                                holder.MageNative_specialprice.setVisibility(View.GONE);
                                holder.MageNative_reguralprice.setText(localData.getMoneyFormat()+" "+map[0].get("normal"));
                                holder.MageNative_reguralprice.setPaintFlags(holder.MageNative_reguralprice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                            }

                        }

                        Log.d("SelectedVarIdPrice",""+map[0]);


                        /*if (data.get(position).getNode().getVariants().getEdges().get(position).getNode().getCompareAtPrice()==null)
                        {
                            holder.MageNative_specialprice.setVisibility(View.GONE);
                            holder.MageNative_reguralprice.setText(regularprice);
                            holder.MageNative_reguralprice.setPaintFlags(holder.MageNative_reguralprice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        }
                        else
                        {
                            if(data.get(position).getNode().getVariants().getEdges().get(position).getNode().getCompareAtPrice().compareTo(data.get(position).getNode().getVariants().getEdges().get(position).getNode().getPrice())==1)
                            {
                                String specialprice=CurrencyFormatter.setsymbol(data.get(position).getNode().getVariants().getEdges().get(position).getNode().getCompareAtPrice(),localData.getMoneyFormat());
                                holder.MageNative_reguralprice.setText(specialprice);
                                holder.MageNative_reguralprice.setPaintFlags(holder.MageNative_reguralprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                holder.MageNative_reguralprice.setTextColor(context.get().getResources().getColor(R.color.black));
                                holder.MageNative_specialprice.setVisibility(View.VISIBLE);
                                holder.MageNative_specialprice.setText(regularprice);
                            }
                            else
                            {
                                holder.MageNative_specialprice.setVisibility(View.GONE);
                                holder.MageNative_reguralprice.setText(regularprice);
                                holder.MageNative_reguralprice.setPaintFlags(holder.MageNative_reguralprice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                            }

                        }*/
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                holder.addtocart.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        try
                        {
                            MainActivity.hideKeyboard(context.get());
                            if(finalVariant_id[0].isEmpty()||holder.quantity.getText().toString().isEmpty())
                            {
                                Toast.makeText(context.get(),context.get().getResources().getString(R.string.selectvariant),Toast.LENGTH_LONG).show();
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
                                        qty=qty+Integer.parseInt(holder.quantity.getText().toString());
                                    }
                                    object.put(finalVariant_id[0],qty);
                                    localData.saveLineItems(object);
                                }
                                else
                                {
                                    JSONObject object=new JSONObject();
                                    object.put(finalVariant_id[0],holder.quantity.getText().toString());
                                    localData.saveLineItems(object);
                                }
                                String message=data.get(position).getNode().getTitle().trim()+" "+ context.get().getResources().getString(R.string.addedtocart);
                                Snackbar snackbar = Snackbar.make(context.get().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
                                snackbar.show();
                                changecount();

                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                });
            }

            if (showPriceSection){
                holder.pricesection.setVisibility(View.VISIBLE);
            }else {
                holder.pricesection.setVisibility(View.GONE);
                holder.discountsection.setVisibility(View.GONE);
            }
            /***************************************************************************************************************/


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public  void changecount()
    {
        if(localData.getLineItems()!=null)
        {
            MainActivity.count= String.valueOf(items.getItemcounts());
        }
        else
        {
            MainActivity.count="0";
        }
        context.get().invalidateOptionsMenu();
    }

    @NonNull
    @Override
    public RelatedProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
    {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = null;
        if (homepage){
            mainGroup = (ViewGroup) mInflater.inflate(R.layout.magenative_product_list_item, viewGroup, false);
        }else {
            mainGroup = (ViewGroup) mInflater.inflate(R.layout.magenative_product_list_item_homepage, viewGroup, false);
        }
        return new RelatedProductViewHolder(mainGroup, context.get());
    }
    @Override
    public int getItemCount() {
        return (null != data ? data.size() : 0);
    }
    private void processwishlist(ImageView wishlist, String s, int position)
    {
        try
        {
            int varinats=data.get(position).getNode().getVariants().getEdges().size();
            String varinatid=data.get(position).getNode().getVariants().getEdges().get(0).getNode().getId().toString();
            List<Storefront.ProductVariantEdge> variantsList =data.get(position).getNode().getVariants().getEdges();
            if(localData.getWishList()!=null)
            {
                JSONObject object=new JSONObject((localData.getWishList()));
                if(object.has(s))
                {
                    object.remove(s);
                    localData.saveWishList(object);
                    wishlist.setImageResource(R.drawable.wishlike);
                }
                else
                {
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
                    localData.saveWishList(object);
                    wishlist.setImageResource(R.drawable.wishred);
                }
            }
            else
            {
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
                localData.saveWishList(object);
                wishlist.setImageResource(R.drawable.wishred);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}