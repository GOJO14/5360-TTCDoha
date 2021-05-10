package com.goodwill.wholesale.productviewsection;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.shopify.buy3.GraphCallResult;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.QueryGraphCall;
import com.shopify.buy3.Storefront;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.adaptersection.RelatedProducts_Adapter;
import com.goodwill.wholesale.checkoutsection.CartListing;
import com.goodwill.wholesale.checkoutsection.CheckoutLineItems;
import com.goodwill.wholesale.currencysection.CurrencyFormatter;
import com.goodwill.wholesale.langauagesection.LoadLanguage;
import com.goodwill.wholesale.maincontainer.MainActivity;
import com.goodwill.wholesale.productlistingsection.ProductListing;
import com.goodwill.wholesale.requestsection.ApiClient;
import com.goodwill.wholesale.requestsection.ApiInterface;
import com.goodwill.wholesale.searchsection.AutoSearch;
import com.goodwill.wholesale.storagesection.LocalData;
import com.goodwill.wholesale.storefrontqueries.Query;
import com.goodwill.wholesale.storefrontresponse.AsyncResponse;
import com.goodwill.wholesale.storefrontresponse.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class ProductView extends MainActivity
{
    String count="0";
    int current=0;
    int previous=0;
    String productID="noid";
    String catID="";
    int position;
    String sharingurl="";
    GraphClient client=null;
    LocalData localData=null;
    @Nullable @BindView(R.id.main) RelativeLayout main;
    @Nullable @BindView(R.id.MageNative_productimages) ViewPager MageNative_productimages;
    @Nullable @BindView(R.id.MageNative_indicator) CirclePageIndicator MageNative_indicator;
    @Nullable @BindView(R.id.MageNative_specialprice) TextView MageNative_specialprice;
    @Nullable @BindView(R.id.MageNative_normalprice) TextView MageNative_normalprice;
    @Nullable @BindView(R.id.MageNative_productname) TextView MageNative_productname;
    @Nullable @BindView(R.id.product_id) TextView product_id;
    @Nullable @BindView(R.id.optionsection) RelativeLayout horizontal;
    @Nullable @BindView(R.id.dynamic_fields_section) LinearLayout dynamic_fields_section;
    @Nullable @BindView(R.id.description) TextView description;
    @Nullable @BindView(R.id.products_details) WebView products_details;
    @Nullable @BindView(R.id.MageNative_sharesection) RelativeLayout MageNative_sharesection;
    @Nullable @BindView(R.id.scrollmain) ScrollView scrollmain;
    @Nullable @BindView(R.id.similarsection) RelativeLayout similarsection;
    @Nullable @BindView(R.id.products) RecyclerView products;
    @Nullable @BindView(R.id.addtocart) TextView addtocart;
    @Nullable @BindView(R.id.discountSection) TextView discountSection;
    @Nullable @BindView(R.id.MageNative_wishlistsection) RelativeLayout MageNative_wishlistsection;
    @Nullable @BindView(R.id.MageNative_wishlist) ImageView MageNative_wishlist;
    @Nullable @BindView(R.id.volumeOption) WebView volumeOption;
    @Nullable
    @BindView(R.id.quantityy)
    EditText quantityy;
    String variant_id="";
    CheckoutLineItems items=null;
    int totalvariantsize;
    boolean out_of_stock=false;
    ArrayList<String> urls;
    Storefront.ProductEdge edge=null;
    Storefront.Product product=null;
    ApiInterface apiService;

    List<Storefront.ProductVariantEdge> variantsList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ViewGroup content = findViewById(R.id.MageNative_frame_container);
        getLayoutInflater().inflate(R.layout.magenative_productview, content, true);
        urls=new ArrayList<String>();
        localData=new LocalData(ProductView.this);
        items=new CheckoutLineItems(ProductView.this);

        if(getIntent().getSerializableExtra("productobject")!=null)
        {
            product= (Storefront.Product) getIntent().getSerializableExtra("productobject");
        }
        if(getIntent().getSerializableExtra("object")!=null)
        {
            edge= (Storefront.ProductEdge) getIntent().getSerializableExtra("object");
        }
        if(getIntent().getStringExtra("id")!=null)
        {
            productID=getIntent().getStringExtra("id");
            Log.i("Product_id",""+productID);
        }
        if(getIntent().getStringExtra("catID")!=null)
        {
            catID=getIntent().getStringExtra("catID");
            position=getIntent().getIntExtra("position",0);
        }
        client= ApiClient.getGraphClient(ProductView.this,true);
        showbackbutton();
        showTittle(" ");
        ButterKnife.bind(ProductView.this);
        if(localData.getWishList()!=null)
        {
            try
            {
                JSONObject object=new JSONObject(localData.getWishList());
                if(object.has(productID))
                {
                    Log.i("INproduct","IN");
                    MageNative_wishlist.setImageResource(R.drawable.wishred);
                }
                else
                {
                    MageNative_wishlist.setImageResource(R.drawable.wishlike);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        MageNative_sharesection.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareString =getResources().getString(R.string.hey)+"  "+
                        MageNative_productname.getText().toString()+
                        "  "+getResources().getString(R.string.on)+"  "+getResources().getString(R.string.app_name)+"\n" + sharingurl;
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareString);
                startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.shareproduct)));
            }
        });
        MageNative_wishlistsection.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                processwishlist(MageNative_wishlist,productID);
            }
        });
        addtocart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try
                {
                    if(variant_id.isEmpty())
                    {
                        Toast.makeText(ProductView.this,getResources().getString(R.string.selectvariant),Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        if(localData.getWishList()!=null)
                        {
                            JSONObject jsonObject=new JSONObject(localData.getWishList());
                            if(jsonObject.has(productID))
                            {
                                jsonObject.remove(productID);
                                localData.saveWishList(jsonObject);
                            }
                        }

                        int quantUser = Integer.parseInt(quantityy.getText().toString());

                        if(localData.getCheckoutId()!=null)
                        {
                            localData.clearCheckoutId();
                            localData.clearCoupon();
                        }
                        if(localData.getLineItems()!=null)
                        {
                            int qty=1;
                            JSONObject object=new JSONObject(localData.getLineItems());
                            if(object.has(variant_id))
                            {
                                qty= Integer.parseInt(object.getString(variant_id));
                                qty=qty+quantUser;
                                object.put(variant_id,qty);
                                Log.d("vaibhavv 1",""+quantUser);
                            }else {
                                object.put(variant_id,quantUser+"");
                                Log.d("vaibhavv 2",""+quantUser);

                            }

                            localData.saveLineItems(object);
                        }
                        else
                        {
                            JSONObject object=new JSONObject();
                            object.put(variant_id,quantUser);
                            localData.saveLineItems(object);
                        }
                        hideKeyboard(ProductView.this);
                        String message=MageNative_productname.getText().toString()+" "+ getResources().getString(R.string.addedtocart);
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
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
        if(edge!=null)
        {
            GraphResponse<Storefront.QueryRoot> response=null;
            processProductData(response,true);
        }
        else
        {
            if(product!=null)
            {
                GraphResponse<Storefront.QueryRoot> response=null;
                processProductData(response,true);
            }
            else
            {
                getProductData(productID);
            }
        }

        //getVolumeOptions();
        getDohaWholeSaleDiscount();

    }

    private void getDohaWholeSaleDiscount() {

        apiService = ApiClient.getDohaWholesaleClient(ProductView.this).create(ApiInterface.class);

        Call<ResponseBody> call = apiService.getproductoffer(getBase64Decode(productID));
        try {
            Response.getRetrofitResponse(call, new AsyncResponse() {
                @Override
                public void finalOutput(@NonNull Object output, @NonNull boolean error) {
                    Log.d("getDohaWholeSale", "" + output);
                    if (error) {
                        System.out.println("getDohaWholeSaleDiscount" + output.toString());
                        try {
                            JSONObject object = new JSONObject(output.toString());
                            if (object.getJSONObject("response").getString("status").equalsIgnoreCase("Success")) {
                            if (object.getJSONObject("response").has("message")){
                                if(object.getJSONObject("response").getString("message")!="null")
                                discountSection.setText(Html.fromHtml(object.getJSONObject("response").getString("message")));
                            }
                            }
                            else {
                                discountSection.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, ProductView.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getProductData(String product_id)
    {
        try
        {
            QueryGraphCall call = client.queryGraph(Query.getSingleProduct(product_id));
            Response.getGraphQLResponse(call,new AsyncResponse()
            {
                @Override
                public void finalOutput(@NonNull Object output,@NonNull boolean error )
                {
                    if(error)
                    {
                        GraphResponse<Storefront.QueryRoot> response  = ((GraphCallResult.Success<Storefront.QueryRoot>) output).getResponse();
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                processProductData(response,false);
                            }
                        });
                    }
                    else
                    {
                        Log.i("ResponseError",""+output.toString());
                    }
                }
            },ProductView.this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void processProductData(GraphResponse<Storefront.QueryRoot> response,boolean flag)
    {
        try
        {
           Storefront.Product productEdge=null;
           if(flag)
           {
               if(product!=null)
               {
                   productEdge=product;
               }
               else
               {
                   productEdge=edge.getNode();
               }
           }
           else
           {
              if(productID.contains("*#*"))
              {
                  productEdge=response.getData().getProductByHandle();
              }
              else
              {
                  productEdge = (Storefront.Product) response.getData().getNode();
              }
           }
           if(localData.getRecentlyViewed()==null)
           {
               JSONObject object=new JSONObject();
               object.put("id",productID);
               object.put("name",productEdge.getTitle());
               object.put("image",productEdge.getImages().getEdges().get(0).getNode().getTransformedSrc());
               object.put("price", CurrencyFormatter.setsymbol(productEdge.getVariants().getEdges().get(0).getNode().getPrice(),localData.getMoneyFormat()));
               if(productEdge.getVariants().getEdges().get(0).getNode().getCompareAtPrice()!=null)
               {
                   if(productEdge.getVariants().getEdges().get(0).getNode().getCompareAtPrice().compareTo(productEdge.getVariants().getEdges().get(0).getNode().getPrice())==1)
                   {
                       object.put("price",CurrencyFormatter.setsymbol(productEdge.getVariants().getEdges().get(0).getNode().getCompareAtPrice(),localData.getMoneyFormat()));
                       object.put("special_price",CurrencyFormatter.setsymbol(productEdge.getVariants().getEdges().get(0).getNode().getPrice(),localData.getMoneyFormat()));
                   }
                   else
                   {
                       object.put("special_price","nospecial");
                   }
               }
               else
               {
                   object.put("special_price","nospecial");
               }
               JSONObject array=new JSONObject();
               array.put(productID,object);
               localData.setRecentlyViewed(array,productID);
           }
           else
           {
               JSONObject product=localData.getRecentlyViewed();
               if(product.names().length()<50)
               {
                   JSONObject object=new JSONObject();
                   object.put("id",productID);
                   object.put("name",productEdge.getTitle());
                   object.put("image",productEdge.getImages().getEdges().get(0).getNode().getTransformedSrc());
                   object.put("price", CurrencyFormatter.setsymbol(productEdge.getVariants().getEdges().get(0).getNode().getPrice(),localData.getMoneyFormat()));
                   if(productEdge.getVariants().getEdges().get(0).getNode().getCompareAtPrice()!=null)
                   {
                       if(productEdge.getVariants().getEdges().get(0).getNode().getCompareAtPrice().compareTo(productEdge.getVariants().getEdges().get(0).getNode().getPrice())==1)
                       {
                           object.put("price",CurrencyFormatter.setsymbol(productEdge.getVariants().getEdges().get(0).getNode().getCompareAtPrice(),localData.getMoneyFormat()));
                           object.put("special_price",CurrencyFormatter.setsymbol(productEdge.getVariants().getEdges().get(0).getNode().getPrice(),localData.getMoneyFormat()));
                       }
                       else
                       {
                           object.put("special_price","nospecial");
                       }
                   }
                   else
                   {
                       object.put("special_price","nospecial");
                   }
                   product.put(productID,object);
                   localData.setRecentlyViewed(product,productID);
               }
               else
               {
                   for(int i=0;i<10;i++)
                   {
                       product.remove(product.names().getString(i));
                   }
                   JSONObject object=new JSONObject();
                   object.put("id",productID);
                   object.put("name",productEdge.getTitle());
                   object.put("image",productEdge.getImages().getEdges().get(0).getNode().getTransformedSrc());
                   object.put("price", CurrencyFormatter.setsymbol(productEdge.getVariants().getEdges().get(0).getNode().getPrice(),localData.getMoneyFormat()));
                   if(productEdge.getVariants().getEdges().get(0).getNode().getCompareAtPrice()!=null)
                   {
                       object.put("price",CurrencyFormatter.setsymbol(productEdge.getVariants().getEdges().get(0).getNode().getCompareAtPrice(),localData.getMoneyFormat()));
                       object.put("special_price",CurrencyFormatter.setsymbol(productEdge.getVariants().getEdges().get(0).getNode().getPrice(),localData.getMoneyFormat()));
                   }
                   else
                   {
                       object.put("special_price","nospecial");
                   }
                   product.put(productID,object);
                   localData.setRecentlyViewed(product,productID);
               }
           }
           Storefront.ImageConnection connection= productEdge.getImages();
           Iterator imageiterator= connection.getEdges().iterator();
           while (imageiterator.hasNext())
           {
               urls.add(((Storefront.ImageEdge)imageiterator.next()).getNode().getOriginalSrc());
           }
           ProductViewImagSlider slider=new ProductViewImagSlider(getSupportFragmentManager(),urls);
           MageNative_productimages.setAdapter(slider);
           MageNative_indicator.setViewPager(MageNative_productimages);
           Storefront.ProductVariantConnection variants=productEdge.getVariants();
           List<Storefront.ProductVariantEdge> productvariantedge = variants.getEdges();
           Log.i("productvariantedge",""+variants);
           Log.i("productvariantedge",""+productvariantedge.size());
           totalvariantsize=productvariantedge.size();
           variantsList =productvariantedge;
           if(productvariantedge!=null)
           {
                Log.i("productvariantedge",""+productvariantedge.size());
                Iterator iterator=productvariantedge.iterator();
                boolean first=true;
                View variantoption=null;
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
                while (iterator.hasNext())
                {
                    edge= (Storefront.ProductVariantEdge) iterator.next();
                    if(first)
                    {
                        first=false;
                        if(edge.getNode().getCompareAtPrice()!=null)
                        {
                            if(edge.getNode().getCompareAtPrice().compareTo(edge.getNode().getPrice())==1)
                            {
                                MageNative_normalprice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                MageNative_specialprice.setText(CurrencyFormatter.setsymbol(edge.getNode().getPrice(),localData.getMoneyFormat()));
                                MageNative_normalprice.setText(CurrencyFormatter.setsymbol(edge.getNode().getCompareAtPrice(),localData.getMoneyFormat()));
                            }
                            else
                            {
                                MageNative_normalprice.setText(CurrencyFormatter.setsymbol(edge.getNode().getPrice(),localData.getMoneyFormat()));
                                MageNative_specialprice.setVisibility(View.GONE);
                            }

                        }
                        else
                        {
                            MageNative_normalprice.setText(CurrencyFormatter.setsymbol(edge.getNode().getPrice(),localData.getMoneyFormat()));
                            MageNative_specialprice.setVisibility(View.GONE);
                        }
                    }
                    if(edge.getNode().getAvailableForSale())
                    {
                        selctedoption=  edge.getNode().getSelectedOptions();
                        if(productvariantedge.size()>=1)
                        {
                            variantoption=View.inflate(ProductView.this,R.layout.variantoption,null);
                            image=variantoption.findViewById(R.id.image);
                            variantid=variantoption.findViewById(R.id.variantid);
                            variantimage=variantoption.findViewById(R.id.variantimage);
                            selectedoption1=variantoption.findViewById(R.id.selectedoption1);
                            selectedoption2=variantoption.findViewById(R.id.selectedoption2);
                            selectedoption3=variantoption.findViewById(R.id.selectedoption3);
                            variantid.setText(edge.getNode().getId().toString());
                            variantimage.setText(edge.getNode().getImage().getOriginalSrc());
                            Glide.with(ProductView.this)
                                    .load(edge.getNode().getImage().getOriginalSrc())
                                    .thumbnail(0.5f)
                                    .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder).dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL))
                                    .into(image);
                            Iterator iterator1=selctedoption.iterator();
                            int counter=0;
                            while (iterator1.hasNext())
                            {
                                counter=counter+1;
                                option= (Storefront.SelectedOption) iterator1.next();
                                String finalvalue=option.getValue();
                                if(counter==1)
                                {
                                    selectedoption1.setText(finalvalue);
                                    selectedoption1.setVisibility(View.VISIBLE);
                                }
                                if(counter==2)
                                {
                                    selectedoption2.setText(finalvalue);
                                    selectedoption2.setVisibility(View.VISIBLE);
                                }
                                if(counter==3)
                                {
                                    selectedoption3.setText(finalvalue);
                                    selectedoption3.setVisibility(View.VISIBLE);
                                }
                            }
                            View finalVariantoption = variantoption;
                            Storefront.ProductVariantEdge finalEdge = edge;
                            variantoption.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {
                                    if(finalEdge.getNode().getCompareAtPrice()!=null)
                                    {
                                        if(finalEdge.getNode().getCompareAtPrice().compareTo(finalEdge.getNode().getPrice())==1)
                                        {
                                            MageNative_normalprice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                            MageNative_specialprice.setText(CurrencyFormatter.setsymbol(finalEdge.getNode().getPrice(),localData.getMoneyFormat()));
                                            MageNative_normalprice.setText(CurrencyFormatter.setsymbol(finalEdge.getNode().getCompareAtPrice(),localData.getMoneyFormat()));
                                        }
                                        else
                                        {
                                            MageNative_normalprice.setText(CurrencyFormatter.setsymbol(finalEdge.getNode().getPrice(),localData.getMoneyFormat()));
                                            MageNative_specialprice.setVisibility(View.GONE);
                                        }

                                    }
                                    else
                                    {
                                        MageNative_normalprice.setText(CurrencyFormatter.setsymbol(finalEdge.getNode().getPrice(),localData.getMoneyFormat()));
                                        MageNative_specialprice.setVisibility(View.GONE);
                                    }
                                   previous=current;
                                   current=dynamic_fields_section.indexOfChild(finalVariantoption);

                                   View previousvariant=dynamic_fields_section.getChildAt(previous);
                                   View currentvariant=dynamic_fields_section.getChildAt(current);

                                    View tick_image1=previousvariant.findViewById(R.id.tick_image);
                                    View tick_image2=currentvariant.findViewById(R.id.tick_image);

                                    RelativeLayout relatveSelection1 = previousvariant.findViewById(R.id.relatveSelection);
                                    TextView selected = (TextView) relatveSelection1.getChildAt(1);
                                    RelativeLayout relatveSelection2 = currentvariant.findViewById(R.id.relatveSelection);
                                    TextView previous = (TextView) relatveSelection2.getChildAt(1);

                                    Log.d("variantselected",""+selected.getText());
                                    Log.d("variantselectedprev",""+previous.getText());

                                    TextView cuurentvariantid=currentvariant.findViewById(R.id.variantid);
                                    TextView cuurentvariantimage=currentvariant.findViewById(R.id.variantimage);
                                  /* tick_image1.setVisibility(View.GONE);
                                   tick_image2.setVisibility(View.VISIBLE);*/

                                  if (selected.getText() == previous.getText()){
                                      previous.setTextColor(getResources().getColor(R.color.white));
                                      relatveSelection1.setBackgroundColor(getResources().getColor(R.color.white));
                                      selected.setTextColor(getResources().getColor(R.color.white));
                                      relatveSelection2.setBackgroundColor(getResources().getColor(R.color.AppTheme));
                                  }else {
                                      previous.setTextColor(getResources().getColor(R.color.white));
                                      relatveSelection1.setBackgroundColor(getResources().getColor(R.color.white));
                                      selected.setTextColor(getResources().getColor(R.color.black));
                                      relatveSelection2.setBackgroundColor(getResources().getColor(R.color.AppTheme));
                                  }


                                   variant_id=cuurentvariantid.getText().toString();

                                   if(urls.contains(cuurentvariantimage.getText().toString()))
                                   {
                                       MageNative_productimages.setCurrentItem(urls.indexOf(cuurentvariantimage.getText().toString()),true);
                                   }
                                   else
                                   {
                                       urls.add(cuurentvariantimage.getText().toString());
                                       ProductViewImagSlider slider=new ProductViewImagSlider(getSupportFragmentManager(),urls);
                                       MageNative_productimages.setAdapter(slider);
                                       MageNative_indicator.setViewPager(MageNative_productimages);
                                   }
                                   //MageNative_productimages.requestFocus();
                                   //scrollmain.scrollTo(0,0);
                                }
                            });
                            dynamic_fields_section.addView(variantoption);
                        }
                        else
                        {
                            variant_id=edge.getNode().getId().toString();
                        }
                    }
                    else
                    {
                        if(totalvariantsize==1)
                        {
                            out_of_stock=true;
                            addtocart.setText(getResources().getString(R.string.outofstock));
                            addtocart.setEnabled(false);
                        }
                    }
                }
                if(dynamic_fields_section.getChildCount()>0)
                {
                    horizontal.setVisibility(View.VISIBLE);
                }
            }
            MageNative_productname.setText(productEdge.getTitle());
            product_id.setText(productID);
            Log.i("Description",""+productEdge.getDescriptionHtml());
            if(productEdge.getDescriptionHtml()!=null)
            {
                if(!(productEdge.getDescription().isEmpty())) {
                    description.setVisibility(View.VISIBLE);
                    products_details.setVisibility(View.VISIBLE);
                    products_details.getSettings().setLoadsImagesAutomatically(true);
                    products_details.getSettings().setJavaScriptEnabled(true);
                    products_details.getSettings().setDomStorageEnabled(true);
                    String description="<style>img{width:100%;margin-top:10px;}</style>"+productEdge.getDescriptionHtml().replace("//cdn","https://cdn");
                    products_details.loadData(description.trim(), "text/html", "utf-8");
                }
             }
            sharingurl=productEdge.getOnlineStoreUrl();
            if(!(catID.isEmpty()))
            {
                if(ProductListing.productedge!=null)
                {
                    similarsection.setVisibility(View.VISIBLE);
                    products.setHasFixedSize(true);
                    products.setLayoutManager(new GridLayoutManager(this, 2));
                    products.setNestedScrollingEnabled(false);
                    List<Storefront.ProductEdge> data= new ArrayList<Storefront.ProductEdge>(ProductListing.productedge);;
                    data.remove(position);
                    RelatedProducts_Adapter related=new RelatedProducts_Adapter(ProductView.this,data,catID,position);
                    products.setAdapter(related);
                    related.notifyDataSetChanged();
                }
            }
            main.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable()
            {
              @Override
              public void run()
              {
                  MageNative_productimages.requestFocus();
                  scrollmain.scrollTo(0,0);
              }
            },100);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume()
    {
        LoadLanguage.setLocale(localData.getLangCode(),ProductView.this);
        if(localData.getLineItems()!=null)
        {
            count= String.valueOf(items.getItemcounts());
        }
        else
        {
            count="0";
        }
        invalidateOptionsMenu();
        super.onResume();
    }
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_product, menu);
        MenuItem item = menu.findItem(R.id.MageNative_action_cart);
        item.setActionView(R.layout.magenative_feed_update_count);
        View notifCount = item.getActionView();
        TextView textView = notifCount.findViewById(R.id.MageNative_hotlist_hot);
        textView.setText(count);
        notifCount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), CartListing.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.MageNative_action_search:
                Intent searchintent=new Intent(ProductView.this, AutoSearch.class);
                searchintent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(searchintent);
                overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
    public void changecount()
    {
        if(localData.getLineItems()!=null)
        {
            count= String.valueOf(items.getItemcounts());
        }
        else
        {
            count="0";
        }
        invalidateOptionsMenu();
    }
    private void processwishlist(ImageView wishlist, String s)
    {
        try
        {
            if(localData.getWishList()!=null)
            {
                JSONObject object=new JSONObject((localData.getWishList()));
                if(object.has(s))
                {
                    Log.i("InRemoval","In");
                    object.remove(s);
                    localData.saveWishList(object);
                    wishlist.setImageResource(R.drawable.wishlike);
                }
                else
                {
                    JSONObject subobject=new JSONObject();
                    subobject.put("product_id",productID);
                    subobject.put("product_name",MageNative_productname.getText().toString());
                    subobject.put("image",urls.get(0));
                    subobject.put("varinats",totalvariantsize);
                    subobject.put("variantsList",variantsList);
                    subobject.put("varinatid",variant_id);
                    object.put(productID,subobject);
                    localData.saveWishList(object);
                    wishlist.setImageResource(R.drawable.wishred);
                }
            }
            else
            {
                JSONObject object=new JSONObject();
                JSONObject subobject=new JSONObject();
                subobject.put("product_id",productID);
                subobject.put("product_name",MageNative_productname.getText().toString());
                subobject.put("varinats",totalvariantsize);
                subobject.put("varinatid",variant_id);
                subobject.put("variantsList",variantsList);
                subobject.put("image",urls.get(0));
                object.put(productID,subobject);
                localData.saveWishList(object);
                wishlist.setImageResource(R.drawable.wishred);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void getVolumeOptions() {
        apiService = ApiClient.getCustomAPisClient(ProductView.this).create(ApiInterface.class);

        Call<ResponseBody> call = apiService.getVolumeOptions(getResources().getString(R.string.shopdomain),getBase64Decode(productID));
        try {

            Response.getRetrofitResponse(call, new AsyncResponse() {
                @Override
                public void finalOutput(@NonNull Object output, @NonNull boolean error) {
                    Log.d("vaibhav VolumeOptions", "" + output);
                    if (error) {
                        System.out.println("output==getVolumeOptions= " + output.toString());
                        // Toast.makeText(ProductView.this, ""+output, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject object = new JSONObject(output.toString());

                            if (object.getBoolean("success")) {
                                volumeOption.setVisibility(View.VISIBLE);
                                volumeOption.loadData(object.getString("data"),"","");

                            }
                            else {
                                volumeOption.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, ProductView.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
