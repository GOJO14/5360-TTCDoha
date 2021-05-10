package com.goodwill.wholesale.productlistingsection;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.shopify.buy3.GraphCallResult;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.QueryGraphCall;
import com.shopify.buy3.Storefront;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.adaptersection.RelatedProducts_Adapter;
import com.goodwill.wholesale.adaptersection.Sort_Adapter;
import com.goodwill.wholesale.datasection.Data;
import com.goodwill.wholesale.loadersection.Loader;
import com.goodwill.wholesale.maincontainer.MainActivity;
import com.goodwill.wholesale.productviewsection.ProductView;
import com.goodwill.wholesale.requestsection.ApiClient;
import com.goodwill.wholesale.requestsection.ApiInterface;
import com.goodwill.wholesale.storagesection.LocalData;
import com.goodwill.wholesale.storefrontqueries.Query;
import com.goodwill.wholesale.storefrontresponse.AsyncResponse;
import com.goodwill.wholesale.storefrontresponse.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by cedcoss on 27/4/18.
 */
public class ProductListing extends MainActivity {
    public static List<Storefront.ProductEdge> productedge = null;
    @Nullable
    @BindView(R.id.grid)
    RecyclerView grid;
    @Nullable
    @BindView(R.id.MageNative_main)
    RelativeLayout MageNative_main = null;
    @Nullable
    @BindView(R.id.MageNative_sortsection)
    RelativeLayout MageNative_sortsection = null;
    GraphClient client = null;
    String cursor = "nocursor";
    boolean hasNextPage = false;
    boolean reverse = false;
    Storefront.ProductCollectionSortKeys sort_key = Storefront.ProductCollectionSortKeys.TITLE;
    int sortvalue = 0;
    String cat_id = "";
    LocalData localData = null;
    String currency_symbol = "";
    RelatedProducts_Adapter related;
    Loader loader;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProductListing.this, LinearLayoutManager.VERTICAL, false);
    List<Storefront.ProductEdge> allowedList ;
    ApiInterface apiService;
    int count=0;
    String originn="";

    private Disposable disposables = new CompositeDisposable();
    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = linearLayoutManager.getChildCount();
            int totalItemCount = linearLayoutManager.getItemCount();
            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

            if (hasNextPage) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount/2 && firstVisibleItemPosition >= 0) { // totalItemCount/2 for loading items on reacing center of screen
                    hasNextPage = false;
                   // if (count<=5){
                        getProducts(cat_id, cursor, sort_key, reverse, "scroll");
                  //  }


                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            ViewGroup content = findViewById(R.id.MageNative_frame_container);
            getLayoutInflater().inflate(R.layout.magenative_gridview, content, true);
            localData = new LocalData(ProductListing.this);
            loader = new Loader(ProductListing.this);
            if (localData.getMoneyFormat() != null) {
                currency_symbol = localData.getMoneyFormat();
            }
            productedge = new ArrayList<Storefront.ProductEdge>();
            ButterKnife.bind(ProductListing.this);
            showbackbutton();
            cat_id = getIntent().getStringExtra("cat_id");
            Log.i("category_id", "" + cat_id);
            if (getIntent().getStringExtra("cat_name") != null) {
                showTittle(getIntent().getStringExtra("cat_name"));
            } else {
                showTittle(" ");
            }
            client = ApiClient.getGraphClient(ProductListing.this, true);
            MageNative_sortsection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Sort();
                }
            });
            getProducts(cat_id, cursor, sort_key, reverse, "firsttime");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Sort() {
        try {
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
            list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, @NonNull View view, int i, long l) {
                    listDialog.cancel();
                    listDialog.dismiss();
                    sortvalue = i;
                    switch (i) {
                        case 0:
                            sort_key = Storefront.ProductCollectionSortKeys.TITLE;
                            reverse = false;
                            break;

                        case 1:
                            sort_key = Storefront.ProductCollectionSortKeys.TITLE;
                            reverse = true;
                            break;

                        case 2:
                            sort_key = Storefront.ProductCollectionSortKeys.PRICE;
                            reverse = true;
                            break;

                        case 3:
                            sort_key = Storefront.ProductCollectionSortKeys.PRICE;
                            reverse = false;
                            break;

                        case 4:
                            sort_key = Storefront.ProductCollectionSortKeys.BEST_SELLING;
                            reverse = false;
                            break;

                        case 5:
                            sort_key = Storefront.ProductCollectionSortKeys.CREATED;
                            reverse = false;
                            break;
                    }
                    cursor = "nocursor";
                    hasNextPage = false;
                    productedge = null;
                    getProducts(cat_id, cursor, sort_key, reverse, "firsttime");

                }
            });
            Sort_Adapter adapter = new Sort_Adapter(this, Data.getSortOptions(ProductListing.this), sortvalue);
            list1.setAdapter(adapter);
            listDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getProducts(String cat_id, String cursor, Storefront.ProductCollectionSortKeys keys, boolean reverse, String origin) {
       // loader.show();
        try {
            Log.i("Cursor", "" + cursor);
            QueryGraphCall call = client.queryGraph(Query.getProducts(cat_id, cursor, keys, reverse, 25));
            Response.getGraphQLResponse(call, new AsyncResponse() {
                @Override
                public void finalOutput(@NonNull Object output, @NonNull boolean error) {
                    if (error) {
                        GraphResponse<Storefront.QueryRoot> response = ((GraphCallResult.Success<Storefront.QueryRoot>) output).getResponse();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                count ++ ;
                                processProducts(response, origin);
                            }
                        });
                    } else {
                       // loader.dismiss();
                        Log.i("ResponseError", "" + output.toString());
                        hasNextPage = false;
                    }
                }
            }, ProductListing.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processProducts(GraphResponse<Storefront.QueryRoot> response, String origin) {
        try {
            Storefront.Collection collectionEdge = null;
            if (cat_id.contains("*#*")) {
                Log.i("Inhandle", "2");
                collectionEdge = response.getData().getShop().getCollectionByHandle();
            } else {
                Log.i("Inhandle", "1");
                collectionEdge = (Storefront.Collection) response.getData().getNode();
            }
            hasNextPage = collectionEdge.getProducts().getPageInfo().getHasNextPage();
            Log.i("hasNextPage", "" + hasNextPage);
            List<Storefront.ProductEdge> data = collectionEdge.getProducts().getEdges();
            if (data.size() > 0) {
                Observable.fromIterable(data).subscribeOn(Schedulers.io())
                        .filter(x -> x.getNode().getAvailableForSale())
                        .toList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<List<Storefront.ProductEdge>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(List<Storefront.ProductEdge> productEdges) {
                                Log.d("size pre", "" + productEdges.size());
                                Log.d("MainAct", "3" + productTags);
                                allowedList = new ArrayList<>();
                                for (int i = 0; i < productEdges.size(); i++) {
                                    ArrayList<String> tags = (ArrayList<String>) productEdges.get(i).getNode().getTags();
                                    for (int j = 0; j < productTags.size(); j++) {
                                        if (tags.contains(MainActivity.productTags.get(j))) {
                                            allowedList.add(productEdges.get(i));
                                        }
                                    }
                                }
                                Log.d("tagsss", "" + allowedList);


                                if (allowedList.size() > 0) {
                                    if (productedge == null) {
                                        productedge = allowedList;
                                    } else {
                                        productedge.addAll(allowedList);
                                    }
                                    cursor = productedge.get(productedge.size() - 1).getCursor();
                                    Log.d("cursorrr",cursor);

                                } else {
                                    //  cursor = productedge.get(productedge.size() - 1).getCursor();
                                    Log.d("hasNextPage allowd emp.",""+hasNextPage);
                                    if (hasNextPage) {
                                        getProducts(cat_id, cursor, sort_key, reverse, "scroll");
                                    } else {
                                        if (origin.equalsIgnoreCase("firsttime")){
                                            if (count<=5 && hasNextPage){
                                                getProducts(cat_id, cursor, sort_key, reverse, "scroll");
                                            }else {
                                                Log.d("testt","1"+hasNextPage);
                                                Toast.makeText(ProductListing.this, getResources().getString(R.string.noproducts), Toast.LENGTH_LONG).show();
                                                finish();
                                            }
                                        }
                                    }
                                }
                               originn = origin;
                                if (!ProductListing.this.isDestroyed()){
                                    onResume();
                                }

                            }
                            @Override
                            public void onError(Throwable e) {

                            }
                        });//done
            } else {
                if (origin.equals("firsttime")) {
                    if (count<=5 && hasNextPage){
                        getProducts(cat_id, cursor, sort_key, reverse, "scroll");
                    }else {
                        Log.d("testt","2"+hasNextPage);
                        Toast.makeText(ProductListing.this, getResources().getString(R.string.noproducts), Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            }
           // loader.dismiss();
        } catch (Exception e) {
          //  loader.dismiss();
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onResume() {
        /***************************************** Setting Up Adapter *********************************************************/
        try {
            String arr_id="";
            final JSONObject[] cart_discount = new JSONObject[1];
            if (productedge != null ) {
                /***************************************** Doha Discount API *********************************************************/
                if (!ProductListing.this.isDestroyed())
                loader.show();

                Iterator iterator = productedge.iterator();
                Storefront.ProductEdge storefront;
                while (iterator.hasNext()){
                    storefront = (Storefront.ProductEdge) iterator.next();
                    arr_id = arr_id+MainActivity.getBase64Decode(storefront.getNode().getId().toString())+",";
                    Log.d("Product IDs",""+arr_id);
                }
                apiService = ApiClient.getDohaWholesaleClient(ProductListing.this).create(ApiInterface.class);
                Call<ResponseBody> call = apiService.getcollectionoffer(arr_id);
                try {
                    Response.getRetrofitResponse(call, new AsyncResponse() {
                        @Override
                        public void finalOutput(@NonNull Object output, @NonNull boolean error) {
                            Log.d("getDohaWholeSale", "" + output);
                            if (error) {
                                System.out.println("getDohaWholeSaleDiscount" + output.toString());
                                try {
                                    JSONObject object = new JSONObject(output.toString());
                                    if (object.get("cart_discount") instanceof JSONObject){
                                        cart_discount[0] = object.getJSONObject("cart_discount");
                                        MainActivity.mergeJSONObjects(cart_discount[0]);
                                    }

                                    grid.setHasFixedSize(true);
                                    grid.setLayoutManager(linearLayoutManager);
                                    grid.setNestedScrollingEnabled(false);

                                    if (related == null){
                                        related  = new RelatedProducts_Adapter(ProductListing.this, productedge,true);
                                        grid.setAdapter(related);
                                    }
                                    related.notifyDataSetChanged();
                                    grid.addOnScrollListener(recyclerViewOnScrollListener);

                                    if (!ProductListing.this.isDestroyed())
                                    loader.dismiss();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, ProductListing.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /***************************************** Doha Discount API *********************************************************/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /***************************************** Setting Up Adapter *********************************************************/

        super.onResume();
    }

    private boolean containsTag(ArrayList<String> productTags, ArrayList<String> allowedTags) {
        boolean isAllowed = false;
        for (int i = 0; i < productTags.size(); i++) {
            if (allowedTags.contains(productTags.get(i))) {
                Log.d("isAllowed", productTags.get(i) + "");
                isAllowed = true;
            } else {
                isAllowed = false;
            }
        }
        return isAllowed;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}