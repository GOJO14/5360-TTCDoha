package com.goodwill.wholesale.productlistingsection;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shopify.buy3.GraphCallResult;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.QueryGraphCall;
import com.shopify.buy3.Storefront;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.adaptersection.Product_Adapter;
import com.goodwill.wholesale.adaptersection.Sort_Adapter;
import com.goodwill.wholesale.datasection.Data;
import com.goodwill.wholesale.maincontainer.MainActivity;
import com.goodwill.wholesale.productviewsection.ProductView;
import com.goodwill.wholesale.requestsection.ApiClient;
import com.goodwill.wholesale.storagesection.LocalData;
import com.goodwill.wholesale.storefrontqueries.Query;
import com.goodwill.wholesale.storefrontresponse.AsyncResponse;
import com.goodwill.wholesale.storefrontresponse.Response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by cedcoss on 27/4/18.
 */
public class AllProductListing extends MainActivity
{
    @Nullable @BindView(R.id.grid)  GridView grid;
    @Nullable @BindView(R.id.MageNative_sortsection)  RelativeLayout MageNative_sortsection = null;
    GraphClient client=null;
    String cursor="nocursor";
    boolean hasNextPage=false;
    boolean reverse=false;
    Storefront.ProductSortKeys sort_key=Storefront.ProductSortKeys.CREATED_AT;
    int sortvalue=2;
    String cat_id="";
    LocalData localData=null;
    String currency_symbol="";
    public static  List<Storefront.ProductEdge> productedge=null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            ViewGroup content = findViewById(R.id.MageNative_frame_container);
            getLayoutInflater().inflate(R.layout.magenative_gridview, content, true);
            localData=new LocalData(AllProductListing.this);
            if(localData.getMoneyFormat()!=null)
            {
                currency_symbol=localData.getMoneyFormat();
            }
            productedge=new ArrayList<Storefront.ProductEdge>();
            ButterKnife.bind(AllProductListing.this);
            showbackbutton();
            if (getIntent().getStringExtra("cat_name") != null)
            {
                showTittle(getIntent().getStringExtra("cat_name"));
            }
            else
            {
                showTittle(" ");
            }
            client= ApiClient.getGraphClient(AllProductListing.this,true);
            grid.setOnScrollListener(new AbsListView.OnScrollListener()
            {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                }

                @Override
                public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount)
                {
                    if ((firstVisibleItem + visibleItemCount) != 0) {
                        if (((firstVisibleItem + visibleItemCount) == totalItemCount) && hasNextPage)
                        {
                            hasNextPage = false;
                            getProducts(cursor,sort_key,reverse,"scroll");
                        }
                    }
                }
            });
            MageNative_sortsection.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Sort();
                }
            });
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                {
                    TextView  id=view.findViewById(R.id.product_id);
                    Intent intent=new Intent(AllProductListing.this, ProductView.class);
                    intent.putExtra("id",id.getText().toString());
                    intent.putExtra("object",(Serializable)productedge.get(i));
                    startActivity(intent);
                    overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                }
            });
            getProducts(cursor,sort_key,reverse,"firsttime");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void Sort()
    {
        try
        {
            final Dialog listDialog = new Dialog(this, R.style.PauseDialog);
            ((ViewGroup) ((ViewGroup) Objects.requireNonNull(listDialog.getWindow()).getDecorView()).getChildAt(0))
                    .getChildAt(1)
                    .setBackgroundColor(getResources().getColor(R.color.black));
            listDialog.setTitle(Html.fromHtml("<center><font color='#ffffff'>" + getResources().getString(R.string.sortbyitems) + "</font></center>"));
            LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View v = Objects.requireNonNull(li).inflate(R.layout.magenative_sortitemlist, null, false);
            listDialog.setContentView(v);
            listDialog.setCancelable(true);
            ListView list1 = listDialog.findViewById(R.id.MageNative_sortlistview);
            list1.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, @NonNull View view, int i, long l)
                {
                    listDialog.cancel();
                    listDialog.dismiss();
                    sortvalue=i;
                    switch (i)
                    {
                        case 0:
                            sort_key = Storefront.ProductSortKeys.TITLE;
                            reverse = false;
                            break;

                        case 1:
                            sort_key = Storefront.ProductSortKeys.TITLE;
                            reverse = true;
                            break;

                        case 2:
                            sort_key = Storefront.ProductSortKeys.CREATED_AT;
                            reverse = false;
                            break;
                    }
                    cursor="nocursor";
                    hasNextPage=false;
                    productedge=null;
                    getProducts(cursor,sort_key,reverse,"firsttime");

                }
            });
            Sort_Adapter adapter = new Sort_Adapter(this, Data.getSortOptionsAllProducts(AllProductListing.this), sortvalue);
            list1.setAdapter(adapter);
            listDialog.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public void getProducts(String cursor,Storefront.ProductSortKeys keys,boolean reverse,String origin)
    {
        try
        {
            Log.i("Cursor",""+cursor);
            QueryGraphCall call = client.queryGraph(Query.getAllProducts(cursor,keys,reverse,10));
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
                                processProducts(response,origin);
                            }
                        });
                    }
                    else
                    {
                        Log.i("ResponseError",""+output.toString());
                        hasNextPage=false;
                    }
                }
            },AllProductListing.this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void processProducts(GraphResponse<Storefront.QueryRoot> response,String origin)
    {
        try
        {
           Storefront.ProductConnection collectionEdge =  response.getData().getShop().getProducts();
           hasNextPage=collectionEdge.getPageInfo().getHasNextPage();
           Log.i("hasNextPage", "" + hasNextPage);
           List<Storefront.ProductEdge> data = collectionEdge.getEdges();
           if(data.size()>0)
           {

               Observable.fromIterable(data).subscribeOn(Schedulers.io())
                       .filter(x->x.getNode().getAvailableForSale())
                       .toList()
                       .observeOn(AndroidSchedulers.mainThread())
                       .subscribe(new SingleObserver<List<Storefront.ProductEdge>>() {
                           @Override
                           public void onSubscribe(Disposable d) {

                           }

                           @Override
                           public void onSuccess(List<Storefront.ProductEdge> productEdges) {
                               Log.d("size pre", ""+productEdges.size());
                               List<Storefront.ProductEdge> allowedList = new ArrayList<>();
                               for (int i=0;i<productEdges.size();i++){
                                   ArrayList<String> tags=(ArrayList<String>) productEdges.get(i).getNode().getTags();
                                   for (int j =0;j<productTags.size();j++){
                                       if(tags.contains(MainActivity.productTags.get(j))){
                                           allowedList.add(productEdges.get(i));
                                           //productEdges.remove(i);
                                       }
                                   }
                               }
                               Log.d("tagsss",""+allowedList);
                               Log.d("size post", ""+productEdges.size());

                               if(productedge==null) {
                                   productedge=allowedList;
                               } else {
                                   productedge.addAll(allowedList);
                               }

                               cursor=productedge.get(productedge.size()-1).getCursor();
                               onResume();
                           }

                           @Override
                           public void onError(Throwable e) {

                           }
                       });//done
           }
           else
           {
               if (origin.equals("firsttime"))
               {
                   Toast.makeText(AllProductListing.this, getResources().getString(R.string.noproducts), Toast.LENGTH_LONG).show();
                   finish();
               }
           }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void onResume()
    {
        try
        {
            if(productedge!=null)
            {
                Product_Adapter adapter = new Product_Adapter(AllProductListing.this, productedge,currency_symbol,grid);
                int cp = grid.getFirstVisiblePosition();
                grid.setAdapter(adapter);
                grid.setSelection(cp + 1);
                adapter.notifyDataSetChanged();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onResume();
    }
    @Override
    public void onBackPressed()
    {
       super.onBackPressed();
    }

}