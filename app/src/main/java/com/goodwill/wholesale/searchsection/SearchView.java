package com.goodwill.wholesale.searchsection;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
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
import com.goodwill.wholesale.adaptersection.RelatedProducts_Adapter;
import com.goodwill.wholesale.loadersection.Loader;
import com.goodwill.wholesale.maincontainer.MainActivity;
import com.goodwill.wholesale.productlistingsection.ProductListing;
import com.goodwill.wholesale.productviewsection.ProductView;
import com.goodwill.wholesale.requestsection.ApiClient;
import com.goodwill.wholesale.requestsection.ApiInterface;
import com.goodwill.wholesale.storagesection.LocalData;
import com.goodwill.wholesale.storefrontqueries.Query;
import com.goodwill.wholesale.storefrontresponse.AsyncResponse;
import com.goodwill.wholesale.storefrontresponse.Response;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by cedcoss on 27/4/18.
 */
public class SearchView extends MainActivity {
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
    @Nullable
    @BindView(R.id.MageNative_sortingsection)
    LinearLayout MageNative_sortingsection = null;
    GraphClient client = null;
    String cursor = "nocursor";
    boolean hasNextPage = false;
    boolean reverse = false;
    Storefront.ProductSortKeys sort_key = Storefront.ProductSortKeys.CREATED_AT;
    int sortvalue = 0;
    String cat_id = "";
    LocalData localData = null;
    String currency_symbol = "";
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchView.this, LinearLayoutManager.VERTICAL, false);
    RelatedProducts_Adapter related;
    Loader loader;
    ApiInterface apiService;

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
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    hasNextPage = false;
                    getProducts(cat_id, cursor, sort_key, reverse, "scroll");

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
            localData = new LocalData(SearchView.this);
            if (localData.getMoneyFormat() != null) {
                currency_symbol = localData.getMoneyFormat();
            }
            loader = new Loader(SearchView.this);
            productedge = new ArrayList<Storefront.ProductEdge>();
            ButterKnife.bind(SearchView.this);
            showbackbutton();
            MageNative_sortingsection.setVisibility(View.GONE);
            cat_id = getIntent().getStringExtra("cat_id");
            Log.i("category_id", "" + cat_id);
            String Tittle = getResources().getString(R.string.search) + " " + cat_id;
            showTittle(Tittle);
            client = ApiClient.getGraphClient(SearchView.this, true);
            /*grid.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                }

                @Override
                public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if ((firstVisibleItem + visibleItemCount) != 0) {
                        if (((firstVisibleItem + visibleItemCount) == totalItemCount) && hasNextPage) {
                            hasNextPage = false;
                            getProducts(cat_id, cursor, sort_key, reverse, "scroll");
                        }
                    }
                }
            });
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    TextView id = view.findViewById(R.id.product_id);
                    Intent intent = new Intent(SearchView.this, ProductView.class);
                    intent.putExtra("id", id.getText().toString());
                    intent.putExtra("object", productedge.get(i));
                    startActivity(intent);
                    overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                }
            });*/
            getProducts(cat_id, cursor, sort_key, reverse, "firsttime");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getProducts(String cat_id, String cursor, Storefront.ProductSortKeys keys, boolean reverse, String origin) {
        try {
            loader.show();
            Log.i("Cursor", "" + cursor);
            QueryGraphCall call = client.queryGraph(Query.getAutoSearchProducts(cursor, cat_id, keys, reverse));
            Response.getGraphQLResponse(call, new AsyncResponse() {
                @Override
                public void finalOutput(@NonNull Object output, @NonNull boolean error) {
                    if (error) {
                        GraphResponse<Storefront.QueryRoot> response = ((GraphCallResult.Success<Storefront.QueryRoot>) output).getResponse();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                processProducts(response, origin);
                            }
                        });
                    } else {
                        loader.dismiss();
                        Log.i("ResponseError", "" + output.toString());
                        hasNextPage = false;
                    }
                }
            }, SearchView.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processProducts(GraphResponse<Storefront.QueryRoot> response, String origin) {
        try {

            hasNextPage = response.getData().getShop().getProducts().getPageInfo().getHasNextPage();
            Log.i("hasNextPage", "" + hasNextPage);
            List<Storefront.ProductEdge> data = response.getData().getShop().getProducts().getEdges();
            if (data.size() > 0) {
                Observable.fromIterable(data).subscribeOn(Schedulers.io())
                        .filter(x -> x.getNode().getAvailableForSale())
                        .toList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<List<Storefront.ProductEdge>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(List<Storefront.ProductEdge> productEdges) {
                                Log.d("size pre", "" + productEdges.size());
                                List<Storefront.ProductEdge> allowedList = new ArrayList<>();
                                for (int i = 0; i < productEdges.size(); i++) {
                                    ArrayList<String> tags = (ArrayList<String>) productEdges.get(i).getNode().getTags();
                                    for (int j = 0; j < productTags.size(); j++) {
                                        if (tags.contains(MainActivity.productTags.get(j))) {
                                            allowedList.add(productEdges.get(i));
                                            //productEdges.remove(i);
                                        }
                                    }
                                }
                                Log.d("tagsss", "" + allowedList);


                                Log.d("size post", "" + productEdges.size());

                                if (allowedList.size() > 0) {
                                    if (productedge == null) {
                                        productedge = allowedList;
                                    } else {
                                        productedge.addAll(allowedList);
                                    }
                                    cursor = productedge.get(productedge.size() - 1).getCursor();
                                    Log.d("cursorrr",cursor);

                                } else {
                                    cursor = productEdges.get(productEdges.size() - 1).getCursor();
                                    if (hasNextPage) {
                                        getProducts(cat_id, cursor, sort_key, reverse, "scroll");
                                    } else {
                                        Toast.makeText(SearchView.this, getResources().getString(R.string.noproducts), Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                }

                                /***************************************** Setting Up Adapter *********************************************************/
                                try {
                                    if (productedge != null ) {


                                        /***************************************** Doha Discount API *********************************************************/
                                        String arr_id="";
                                        final JSONObject[] cart_discount = new JSONObject[1];
                                        Iterator iterator = data.iterator();
                                        Storefront.ProductEdge storefront;
                                        while (iterator.hasNext()){
                                            storefront = (Storefront.ProductEdge) iterator.next();
                                            arr_id = arr_id+MainActivity.getBase64Decode(storefront.getNode().getId().toString())+",";
                                            Log.d("Product IDs",""+arr_id);
                                        }
                                        apiService = ApiClient.getDohaWholesaleClient(SearchView.this).create(ApiInterface.class);
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
                                                            if (origin.equalsIgnoreCase("firsttime")){
                                                                related  = new RelatedProducts_Adapter(SearchView.this, productedge,true);
                                                                grid.setAdapter(related);
                                                            }
                                                            related.notifyDataSetChanged();
                                                            grid.addOnScrollListener(recyclerViewOnScrollListener);

                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            }, SearchView.this);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        /***************************************** Doha Discount API *********************************************************/

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                /***************************************** Setting Up Adapter *********************************************************/
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });//done
            } else {
                if (origin.equals("firsttime")) {
                    Toast.makeText(SearchView.this, getResources().getString(R.string.noproducts), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            loader.dismiss();
        } catch (Exception e) {
            loader.dismiss();
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        try {
           /* if (productedge != null) {
                Product_Adapter adapter = new Product_Adapter(SearchView.this, productedge, currency_symbol, grid);
                grid.setAdapter(adapter);
                int cp = grid.getFirstVisiblePosition();
                grid.setSelection(cp + 1);
                adapter.notifyDataSetChanged();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        return false;
    }
}