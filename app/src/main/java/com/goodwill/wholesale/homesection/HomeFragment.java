package com.goodwill.wholesale.homesection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shopify.buy3.GraphCallResult;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.QueryGraphCall;
import com.shopify.buy3.Storefront;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.adaptersection.RecentlyViewedProducts_Adapter;
import com.goodwill.wholesale.adaptersection.RelatedProducts_Adapter;
import com.goodwill.wholesale.loadersection.CustomProgressDialog;
import com.goodwill.wholesale.loadersection.Loader;
import com.goodwill.wholesale.maincontainer.MainActivity;
import com.goodwill.wholesale.productlistingsection.ProductListing;
import com.goodwill.wholesale.productviewsection.CirclePageIndicator;
import com.goodwill.wholesale.productviewsection.ProductView;
import com.goodwill.wholesale.requestsection.ApiClient;
import com.goodwill.wholesale.requestsection.ApiInterface;
import com.goodwill.wholesale.storagesection.LocalData;
import com.goodwill.wholesale.storefrontqueries.Query;
import com.goodwill.wholesale.storefrontresponse.AsyncResponse;
import com.goodwill.wholesale.storefrontresponse.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class HomeFragment extends Fragment {
    @Nullable
    Unbinder unbinder;
    ApiInterface apiService;
    @Nullable
    @BindView(R.id.MageNative_homepagebanner)
    ViewPager MageNative_homepagebanner;
    @Nullable
    @BindView(R.id.MageNative_indicator2)
    CirclePageIndicator MageNative_indicator2;
    @Nullable
    @BindView(R.id.cat1image)
    ImageView cat1image;
    @Nullable
    @BindView(R.id.cat1id)
    TextView cat1id;
    @Nullable
    @BindView(R.id.cat1section)
    RelativeLayout cat1section;
    @Nullable
    @BindView(R.id.cat2image)
    ImageView cat2image;
    @Nullable
    @BindView(R.id.cat2id)
    TextView cat2id;
    @Nullable
    @BindView(R.id.cat2section)
    RelativeLayout cat2section;
    @Nullable
    @BindView(R.id.cat3image)
    ImageView cat3image;
    @Nullable
    @BindView(R.id.cat3id)
    TextView cat3id;
    @Nullable
    @BindView(R.id.cat3section)
    RelativeLayout cat3section;
    @Nullable
    @BindView(R.id.cat4image)
    ImageView cat4image;
    @Nullable
    @BindView(R.id.cat4id)
    TextView cat4id;
    @Nullable
    @BindView(R.id.cat4section)
    RelativeLayout cat4section;
    @Nullable
    @BindView(R.id.cat5image)
    ImageView cat5image;
    @Nullable
    @BindView(R.id.cat5id)
    TextView cat5id;
    @Nullable
    @BindView(R.id.cat5section)
    RelativeLayout cat5section;
    @Nullable
    @BindView(R.id.cat6image)
    ImageView cat6image;
    @Nullable
    @BindView(R.id.cat6id)
    TextView cat6id;
    @Nullable
    @BindView(R.id.cat6section)
    RelativeLayout cat6section;
    @Nullable
    @BindView(R.id.cat7image)
    ImageView cat7image;
    @Nullable
    @BindView(R.id.cat7id)
    TextView cat7id;
    @Nullable
    @BindView(R.id.cat7section)
    RelativeLayout cat7section;
    @Nullable
    @BindView(R.id.cat8image)
    ImageView cat8image;
    @Nullable
    @BindView(R.id.cat8id)
    TextView cat8id;
    @Nullable
    @BindView(R.id.cat8section)
    RelativeLayout cat8section;
    @Nullable
    @BindView(R.id.cat9image)
    ImageView cat9image;
    @Nullable
    @BindView(R.id.cat9id)
    TextView cat9id;
    @Nullable
    @BindView(R.id.cat9section)
    RelativeLayout cat9section;
    @Nullable
    @BindView(R.id.grid9imagesection)
    RelativeLayout grid9imagesection;
    @Nullable
    @BindView(R.id.tittle)
    TextView grid9tittle;
    @Nullable
    @BindView(R.id.catid)
    TextView catid;
    @Nullable
    @BindView(R.id.viewcats)
    ImageView catviewallcard;
    @Nullable
    @BindView(R.id.main)
    NestedScrollView main;
    @Nullable
    @BindView(R.id.staggaredgridsection)
    RelativeLayout staggaredgridsection;
    @Nullable
    @BindView(R.id.staggeredcatname)
    TextView staggeredcatname;
    @Nullable
    @BindView(R.id.staggeredcatid)
    TextView staggeredcatid;
    @Nullable
    @BindView(R.id.staggeredproducts)
    RecyclerView staggeredproducts;
    @Nullable
    @BindView(R.id.sectionviewall)
    ImageView sectionviewall;
    @Nullable
    @BindView(R.id.thirdcollections)
    RelativeLayout thirdcollections;
    @Nullable
    @BindView(R.id.thirdcatname)
    TextView thirdcatname;
    @Nullable
    @BindView(R.id.thirdcatid)
    TextView thirdcatid;
    @Nullable
    @BindView(R.id.products)
    RecyclerView products;
    @Nullable
    @BindView(R.id.viewall)
    ImageView viewall;
    @Nullable
    @BindView(R.id.fourthcollections)
    RelativeLayout fourthcollections;
    @Nullable
    @BindView(R.id.fourthcatname)
    TextView fourthcatname;
    @Nullable
    @BindView(R.id.fourthcatid)
    TextView fourthcatid;
    @Nullable
    @BindView(R.id.fourthproducts)
    RecyclerView fourthproducts;
    @Nullable
    @BindView(R.id.fourthviewall)
    ImageView fourthviewall;
    @Nullable
    @BindView(R.id.fivecollections)
    RelativeLayout fivecollections;
    @Nullable
    @BindView(R.id.fivecatname)
    TextView fivecatname;
    @Nullable
    @BindView(R.id.fivecatid)
    TextView fivecatid;
    @Nullable
    @BindView(R.id.fiveproducts)
    RecyclerView fiveproducts;
    @Nullable
    @BindView(R.id.fiveviewall)
    ImageView fiveviewall;
    @Nullable
    @BindView(R.id.sixcollections)
    RelativeLayout sixcollections;
    @Nullable
    @BindView(R.id.sixcatname)
    TextView sixcatname;
    @Nullable
    @BindView(R.id.sixcatid)
    TextView sixcatid;
    @Nullable
    @BindView(R.id.sixproducts)
    RecyclerView sixproducts;
    @Nullable
    @BindView(R.id.sixviewall)
    ImageView sixviewall;
    @Nullable
    @BindView(R.id.middlebanner)
    ViewPager middlebanner;
    @Nullable
    @BindView(R.id.bannersection)
    LinearLayout bannersection;
    @Nullable
    @BindView(R.id.recentlyviewedsection)
    RelativeLayout recentlyviewedsection;
    @Nullable
    @BindView(R.id.recentsproducts)
    RecyclerView recentsproducts;

    @Nullable
    @BindView(R.id.ricecollection)
    LinearLayout ricecollection;
    @Nullable
    @BindView(R.id.sugarcollection)
    LinearLayout sugarcollection;
    @Nullable
    @BindView(R.id.pulsescollection)
    LinearLayout pulsescollection;
    @Nullable
    @BindView(R.id.wholespicecollection)
    LinearLayout wholespicecollection;
    @Nullable
    @BindView(R.id.powderspicecollection)
    LinearLayout powderspicecollection;
    @Nullable
    @BindView(R.id.Flourcollection)
    LinearLayout Flourcollection;
    @Nullable
    @BindView(R.id.oilcollection)
    LinearLayout oilcollection;
    @Nullable
    @BindView(R.id.dryfruitscollection)
    LinearLayout dryfruitscollection;
    @Nullable
    @BindView(R.id.cookingingridientscollection)
    LinearLayout cookingingridientscollection;

    List<Storefront.ProductEdge> grid9data;
    Timer timer;
    Timer timer2;
    int page = 0;
    int page2 = 0;
    JSONArray banners_widget;
    JSONArray collection_widget = null;
    JSONArray banners_widget2;
    GraphClient client;
    LocalData data = null;
    Loader loader;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_layout_hompage, container, false);
        unbinder = ButterKnife.bind(this, view);
        apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        client = ApiClient.getGraphClient(getActivity(), true);
        Call<ResponseBody> call = apiService.getHomePage(getResources().getString(R.string.mid));
        data = new LocalData(getActivity());
        loader = new Loader(getActivity());

        ricecollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProductListing.class);
                String s1 = "gid://shopify/Collection/" + "172016107619";
                intent.putExtra("cat_id", getBase64Encode(s1));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
            }
        });

        sugarcollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProductListing.class);
                String s1 = "gid://shopify/Collection/" + "172016173155";
                intent.putExtra("cat_id", getBase64Encode(s1));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
            }
        });

        pulsescollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProductListing.class);
                String s1 = "gid://shopify/Collection/" + "172016861283";
                intent.putExtra("cat_id", getBase64Encode(s1));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
            }
        });
        wholespicecollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProductListing.class);
                String s1 = "gid://shopify/Collection/" + "172016205923";
                intent.putExtra("cat_id", getBase64Encode(s1));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
            }
        });
        powderspicecollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProductListing.class);
                String s1 = "gid://shopify/Collection/" + "172016238691";
                intent.putExtra("cat_id", getBase64Encode(s1));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
            }
        });
        Flourcollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProductListing.class);
                String s1 = "gid://shopify/Collection/" + "172016304227";
                intent.putExtra("cat_id", getBase64Encode(s1));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
            }
        });
        oilcollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProductListing.class);
                String s1 = "gid://shopify/Collection/" + "172017221731";
                intent.putExtra("cat_id", getBase64Encode(s1));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
            }
        });
        dryfruitscollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProductListing.class);
                String s1 = "gid://shopify/Collection/" + "172016336995";
                intent.putExtra("cat_id", getBase64Encode(s1));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
            }
        });
        cookingingridientscollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProductListing.class);
                String s1 = "gid://shopify/Collection/" + "172017254499";
                intent.putExtra("cat_id", getBase64Encode(s1));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
            }
        });

        getResponse(call);
        return view;
    }

    private void getResponse(Call<ResponseBody> call) {
        loader.show();
        try {
            Response.getRetrofitResponse(call, new AsyncResponse() {
                @Override
                public void finalOutput(@NonNull Object output, @NonNull boolean error) {
                    if (error) {
                        if (CustomProgressDialog.mDialog != null) {
                            CustomProgressDialog.mDialog.dismiss();
                            CustomProgressDialog.mDialog = null;
                        }
                        creatHomePage(output.toString());
                    } else {
                        loader.dismiss();
                        Log.i("ErrorHomePage", "" + output.toString());
                    }
                }
            }, getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void creatHomePage(String result) {
        try {
            JSONObject object = new JSONObject(result);
            String success = object.getString("success");
            if (success.equals("true")) {
                banners_widget = object.getJSONArray("banners");
                collection_widget = object.getJSONArray("collections");
                Log.i("collection_widget", "" + object.getJSONArray("collections"));
                this.MageNative_homepagebanner.setAdapter(new HomePageBanner(getActivity().getSupportFragmentManager(), getActivity(), object.getJSONArray("banners")));
                pageSwitcher(10);
                this.MageNative_indicator2.setViewPager(this.MageNative_homepagebanner);
                fetchCollectionData(object.getJSONArray("collections").getJSONObject(0).getString("id"), "sixthcollection", 50, object.getJSONArray("collections").getJSONObject(0).getString("title"));
                //  fetchCollectionData(object.getJSONArray("collections").getJSONObject(1).getString("id"),"staggeredgrid",10,object.getJSONArray("collections").getJSONObject(1).getString("title"));
                // fetchCollectionData(collection_widget.getJSONObject(2).getString("id"),"thirdcollection",10,collection_widget.getJSONObject(2).getString("title"));

               /* if(object.getJSONArray("collections").length()==4){
                    fetchCollectionData(object.getJSONArray("collections").getJSONObject(3).getString("id"),"fourthcollection",6,object.getJSONArray("collections").getJSONObject(3).getString("title"));
                }
                else if(object.getJSONArray("collections").length()==5){
                    fetchCollectionData(object.getJSONArray("collections").getJSONObject(3).getString("id"),"fourthcollection",6,object.getJSONArray("collections").getJSONObject(3).getString("title"));
                    fetchCollectionData(object.getJSONArray("collections").getJSONObject(4).getString("id"),"fifthcollection",6,object.getJSONArray("collections").getJSONObject(4).getString("title"));

                }
                else if(object.getJSONArray("collections").length()==6){
                    fetchCollectionData(object.getJSONArray("collections").getJSONObject(3).getString("id"),"fourthcollection",6,object.getJSONArray("collections").getJSONObject(3).getString("title"));
                    fetchCollectionData(object.getJSONArray("collections").getJSONObject(4).getString("id"),"fifthcollection",6,object.getJSONArray("collections").getJSONObject(4).getString("title"));
                    fetchCollectionData(object.getJSONArray("collections").getJSONObject(5).getString("id"),"sixthcollection",6,object.getJSONArray("collections").getJSONObject(5).getString("title"));
                }*/


                if (object.has("bannersAdditional")) {
                    if (object.get("bannersAdditional") instanceof JSONObject) {
                        if (object.getJSONObject("bannersAdditional").has("middle")) {
                            banners_widget2 = object.getJSONObject("bannersAdditional").getJSONArray("middle");
                            this.middlebanner.setAdapter(new HomePageBanner(getActivity().getSupportFragmentManager(), getActivity(), object.getJSONObject("bannersAdditional").getJSONArray("middle")));
                            pageSwitcher2(10);
                            middlebanner.setVisibility(View.VISIBLE);
                        }
                        if (object.getJSONObject("bannersAdditional").has("bottom")) {
                            inflateBottomBanners(object.getJSONObject("bannersAdditional").getJSONArray("bottom"));
                            bannersection.setVisibility(View.VISIBLE);
                        }
                    }
                }
                main.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        main.scrollTo(0, 0);
                    }
                }, 100);
            }

        } catch (Exception e) {
            loader.dismiss();
            e.printStackTrace();
        }
    }

    private void pageSwitcher2(int i) {
        try {
            timer2 = new Timer(); // At this line a new Thread will be created
            timer2.scheduleAtFixedRate(new RemindTask2(), 0, i * 1000); // delay
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inflateBottomBanners(JSONArray jsonArray) {
        try {
            View view = null;
            ImageView image = null;
            TextView id = null;
            TextView link = null;
            JSONObject object = null;
            for (int i = 0; i < jsonArray.length(); i++) {
                object = jsonArray.getJSONObject(i);
                view = View.inflate(getActivity(), R.layout.banner_layout, null);
                image = view.findViewById(R.id.banner);
                id = view.findViewById(R.id.id);
                link = view.findViewById(R.id.link_to);
                Glide.with(getActivity())
                        .load(object.getString("url"))
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.bannerplaceholder)
                        )
                        .into(image);
                id.setText(object.getString("id"));
                link.setText(object.getString("link_to"));
                TextView finalLink = link;
                TextView finalId = id;
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (finalLink.getText().toString().equals("collection")) {
                            String s1 = "gid://shopify/Collection/" + finalId.getText().toString();
                            Intent intent = new Intent(getActivity(), ProductListing.class);
                            intent.putExtra("cat_id", getBase64Encode(s1));
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                        }
                        if (finalLink.getText().toString().equals("product")) {
                            String s1 = "gid://shopify/Product/" + finalId.getText().toString();
                            Intent prod_link = new Intent(getActivity(), ProductView.class);
                            prod_link.putExtra("id", getBase64Encode(s1));
                            startActivity(prod_link);
                            getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                        }
                        if (finalLink.getText().toString().equals("web_address")) {
                            Intent weblink = new Intent(getActivity(), HomeWeblink.class);
                            weblink.putExtra("link", finalId.getText().toString());
                            startActivity(weblink);
                            getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                        }
                    }
                });
                bannersection.addView(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchCollectionData(String cat_id, String origin, int number, String tittle) {
        try {
            String s1 = "gid://shopify/Collection/" + cat_id;
            QueryGraphCall call = client.queryGraph(Query.getProducts(getBase64Encode(s1), "nocursor", Storefront.ProductCollectionSortKeys.CREATED, false, number));
            loader.show();
            Response.getGraphQLResponse(call, new AsyncResponse() {
                @Override
                public void finalOutput(@NonNull Object output, @NonNull boolean error) {
                    if (error) {
                        GraphResponse<Storefront.QueryRoot> response = ((GraphCallResult.Success<Storefront.QueryRoot>) output).getResponse();
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (origin.equals("9grid")) {
                                    if (CustomProgressDialog.mDialog != null) {
                                        CustomProgressDialog.mDialog.dismiss();
                                        CustomProgressDialog.mDialog = null;
                                    }
                                    process9Grids(getBase64Encode(s1), response, tittle);
                                }
                                if (origin.equals("staggeredgrid")) {
                                    if (CustomProgressDialog.mDialog != null) {
                                        CustomProgressDialog.mDialog.dismiss();
                                        CustomProgressDialog.mDialog = null;
                                    }
                                    processStaggeredGrid(getBase64Encode(s1), response, tittle);
                                }
                                if (origin.equals("thirdcollection")) {
                                    if (CustomProgressDialog.mDialog != null) {
                                        CustomProgressDialog.mDialog.dismiss();
                                        CustomProgressDialog.mDialog = null;
                                    }
                                    processThirdCollection(getBase64Encode(s1), response, tittle);
                                }
                                if (origin.equals("fourthcollection")) {
                                    if (CustomProgressDialog.mDialog != null) {
                                        CustomProgressDialog.mDialog.dismiss();
                                        CustomProgressDialog.mDialog = null;
                                    }
                                    processFourCollection(getBase64Encode(s1), response, tittle);
                                }
                                if (origin.equals("fifthcollection")) {
                                    if (CustomProgressDialog.mDialog != null) {
                                        CustomProgressDialog.mDialog.dismiss();
                                        CustomProgressDialog.mDialog = null;
                                    }
                                    processFiveCollection(getBase64Encode(s1), response, tittle);
                                }
                                if (origin.equals("sixthcollection")) {
                                    if (CustomProgressDialog.mDialog != null) {
                                        CustomProgressDialog.mDialog.dismiss();
                                        CustomProgressDialog.mDialog = null;
                                    }
                                    processSixCollection(getBase64Encode(s1), response, tittle);
                                }
                            }
                        });

                    } else {
                        loader.dismiss();
                        Log.i("ResponseError", "" + output.toString());
                    }
                }
            }, getActivity());
        } catch (Exception e) {
            loader.dismiss();
            e.printStackTrace();
        }
    }

    private void processFourCollection(String id, GraphResponse<Storefront.QueryRoot> response, String tittle) {
        try {
            Storefront.Collection collectionEdge = (Storefront.Collection) response.getData().getNode();
            List<Storefront.ProductEdge> data = collectionEdge.getProducts().getEdges();
            if (data.size() > 0) {
                fourthcollections.setVisibility(View.VISIBLE);
                fourthcatid.setText(id);
                fourthcatname.setText(Html.fromHtml(tittle));
                fourthcatname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openCategory(fourthcatid.getText().toString(), fourthcatname.getText().toString());
                    }
                });
                fourthviewall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openCategory(fourthcatid.getText().toString(), fourthcatname.getText().toString());
                    }
                });


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
                                data.addAll(productEdges);
                            }

                            @Override
                            public void onError(Throwable e) {
                            }
                        });//done


                /***************************************** Doha Discount API *********************************************************/
                String arr_id = "";
                final JSONObject[] cart_discount = new JSONObject[1];
                Iterator iterator = data.iterator();
                Storefront.ProductEdge storefront;
                while (iterator.hasNext()) {
                    storefront = (Storefront.ProductEdge) iterator.next();
                    arr_id = arr_id + MainActivity.getBase64Decode(storefront.getNode().getId().toString()) + ",";
                    Log.d("Product IDs", "" + arr_id);
                }
                apiService = ApiClient.getDohaWholesaleClient(getActivity()).create(ApiInterface.class);
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
                                    if (object.get("cart_discount") instanceof JSONObject) {
                                        cart_discount[0] = object.getJSONObject("cart_discount");
                                        MainActivity.mergeJSONObjects(cart_discount[0]);
                                    }

                                    fourthproducts.setHasFixedSize(true);
                                    fourthproducts.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                    fourthproducts.setNestedScrollingEnabled(false);
                                    RelatedProducts_Adapter related = new RelatedProducts_Adapter(getActivity(), data, false);
                                    fourthproducts.setAdapter(related);
                                    related.notifyDataSetChanged();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, getActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /***************************************** Doha Discount API *********************************************************/


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processFiveCollection(String id, GraphResponse<Storefront.QueryRoot> response, String tittle) {
        try {
            Storefront.Collection collectionEdge = (Storefront.Collection) response.getData().getNode();
            List<Storefront.ProductEdge> data = collectionEdge.getProducts().getEdges();
            Log.i("datasizzee", "five " + data.size());
            Log.i("datasizzee", "five " + tittle);
            if (data.size() > 0) {
                fivecollections.setVisibility(View.VISIBLE);
                fivecatid.setText(id);
                fivecatname.setText(Html.fromHtml(tittle)

                );
                fivecatname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openCategory(fivecatid.getText().toString(), fivecatname.getText().toString());
                    }
                });
                fiveviewall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openCategory(fivecatid.getText().toString(), fivecatname.getText().toString());
                    }
                });

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
                                data.addAll(productEdges);
                            }

                            @Override
                            public void onError(Throwable e) {
                            }
                        });//done


                /***************************************** Doha Discount API *********************************************************/
                String arr_id = "";
                final JSONObject[] cart_discount = new JSONObject[1];
                Iterator iterator = data.iterator();
                Storefront.ProductEdge storefront;
                while (iterator.hasNext()) {
                    storefront = (Storefront.ProductEdge) iterator.next();
                    arr_id = arr_id + MainActivity.getBase64Decode(storefront.getNode().getId().toString()) + ",";
                    Log.d("Product IDs", "" + arr_id);
                }
                apiService = ApiClient.getDohaWholesaleClient(getActivity()).create(ApiInterface.class);
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
                                    if (object.get("cart_discount") instanceof JSONObject) {
                                        cart_discount[0] = object.getJSONObject("cart_discount");
                                        MainActivity.mergeJSONObjects(cart_discount[0]);
                                    }

                                    fiveproducts.setHasFixedSize(true);
                                    fiveproducts.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                    fiveproducts.setNestedScrollingEnabled(false);
                                    RelatedProducts_Adapter related = new RelatedProducts_Adapter(getActivity(), data, false);
                                    fiveproducts.setAdapter(related);
                                    related.notifyDataSetChanged();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, getActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /***************************************** Doha Discount API *********************************************************/


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processSixCollection(String id, GraphResponse<Storefront.QueryRoot> response, String tittle) {
        try {
            Storefront.Collection collectionEdge = (Storefront.Collection) response.getData().getNode();
            List<Storefront.ProductEdge> data = collectionEdge.getProducts().getEdges();
            Log.i("datasizzee", "six " + data.size());
            if (data.size() > 0) {
                final List<Storefront.ProductEdge>[] productedge = new List[]{new ArrayList<Storefront.ProductEdge>()};
                ;
                sixcollections.setVisibility(View.VISIBLE);
                sixcatid.setText(id);
                sixcatname.setText(Html.fromHtml(tittle));
                sixcatname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openCategory(sixcatid.getText().toString(), sixcatname.getText().toString());
                    }
                });
                sixviewall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openCategory(sixcatid.getText().toString(), sixcatname.getText().toString());
                    }
                });

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
                                List<Storefront.ProductEdge> allowedList = new ArrayList<>();
                                for (int i = 0; i < productEdges.size(); i++) {
                                    ArrayList<String> tags = (ArrayList<String>) productEdges.get(i).getNode().getTags();
                                    for (int j = 0; j < MainActivity.productTags.size(); j++) {
                                        if (tags.contains(MainActivity.productTags.get(j))) {
                                            allowedList.add(productEdges.get(i));
                                            //productEdges.remove(i);
                                        }
                                    }
                                }
                                Log.d("tagsss", "" + allowedList);
                                if (allowedList.size() > 0) {
                                    if (productedge[0] == null) {
                                        productedge[0] = allowedList;
                                    } else {
                                        productedge[0].addAll(allowedList);
                                    }
                                }

                                /***************************************** Doha Discount API *********************************************************/
                                String arr_id = "";
                                final JSONObject[] cart_discount = new JSONObject[1];
                                Iterator iterator = data.iterator();
                                Storefront.ProductEdge storefront;
                                while (iterator.hasNext()) {
                                    storefront = (Storefront.ProductEdge) iterator.next();
                                    arr_id = arr_id + MainActivity.getBase64Decode(storefront.getNode().getId().toString()) + ",";
                                    Log.d("Product IDs", "" + arr_id);
                                }
                                apiService = ApiClient.getDohaWholesaleClient(getActivity()).create(ApiInterface.class);
                                Call<ResponseBody> call = apiService.getcollectionoffer(arr_id);
                                try {
                                    Response.getRetrofitResponse(call, new AsyncResponse() {
                                        @Override
                                        public void finalOutput(@NonNull Object output, @NonNull boolean error) {
                                            Log.d("getDohaWholeSale", "" + output);
                                            if (error) {
                                                System.out.println("getDohaWholeSaleDiscount" +" --- "+output.toString());
                                                try {
                                                    JSONObject object = new JSONObject(output.toString());
                                                    if (object.get("cart_discount") instanceof JSONObject) {
                                                        cart_discount[0] = object.getJSONObject("cart_discount");
                                                        MainActivity.mergeJSONObjects(cart_discount[0]);
                                                    }

                                                    sixproducts.setHasFixedSize(true);
                                                    sixproducts.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                                    sixproducts.setNestedScrollingEnabled(false);
                                                    RelatedProducts_Adapter related = new RelatedProducts_Adapter(getActivity(), productedge[0], true);
                                                    sixproducts.setAdapter(related);
                                                    related.notifyDataSetChanged();
                                                    loader.dismiss();

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }, getActivity());

                                } catch (Exception e) {
                                    loader.dismiss();
                                    e.printStackTrace();
                                }
                                /***************************************** Doha Discount API *********************************************************/

                            }

                            @Override
                            public void onError(Throwable e) {
                            }
                        });//done
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processThirdCollection(String id, GraphResponse<Storefront.QueryRoot> response, String tittle) {
        try {
            Storefront.Collection collectionEdge = (Storefront.Collection) response.getData().getNode();
            List<Storefront.ProductEdge> data = collectionEdge.getProducts().getEdges();
            if (data.size() > 0) {
                thirdcollections.setVisibility(View.VISIBLE);
                thirdcatid.setText(id);
                thirdcatname.setText(Html.fromHtml(tittle)

                );
                thirdcatname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openCategory(thirdcatid.getText().toString(), thirdcatname.getText().toString());
                    }
                });
                viewall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openCategory(thirdcatid.getText().toString(), thirdcatname.getText().toString());
                    }
                });

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
                                List<Storefront.ProductEdge> allowedList = new ArrayList<>();
                                for (int i = 0; i < productEdges.size(); i++) {
                                    ArrayList<String> tags = (ArrayList<String>) productEdges.get(i).getNode().getTags();
                                    for (int j = 0; j < MainActivity.productTags.size(); j++) {
                                        if (tags.contains(MainActivity.productTags.get(j))) {
                                            allowedList.add(productEdges.get(i));
                                            //productEdges.remove(i);
                                        }
                                    }

                                }
                                data.addAll(allowedList);
                            }

                            @Override
                            public void onError(Throwable e) {
                            }
                        });//done


                /***************************************** Doha Discount API *********************************************************/
                String arr_id = "";
                final JSONObject[] cart_discount = new JSONObject[1];
                Iterator iterator = data.iterator();
                Storefront.ProductEdge storefront;
                while (iterator.hasNext()) {
                    storefront = (Storefront.ProductEdge) iterator.next();
                    arr_id = arr_id + MainActivity.getBase64Decode(storefront.getNode().getId().toString()) + ",";
                    Log.d("Product IDs", "" + arr_id);
                }
                apiService = ApiClient.getDohaWholesaleClient(getActivity()).create(ApiInterface.class);
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
                                    if (object.get("cart_discount") instanceof JSONObject) {
                                        cart_discount[0] = object.getJSONObject("cart_discount");
                                        MainActivity.mergeJSONObjects(cart_discount[0]);
                                    }

                                    products.setHasFixedSize(true);
                                    products.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                    products.setNestedScrollingEnabled(false);
                                    RelatedProducts_Adapter related = new RelatedProducts_Adapter(getActivity(), data, false);
                                    products.setAdapter(related);
                                    related.notifyDataSetChanged();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, getActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /***************************************** Doha Discount API *********************************************************/


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processStaggeredGrid(String id, GraphResponse<Storefront.QueryRoot> response, String tittle) {
        try {
            Storefront.Collection collectionEdge = (Storefront.Collection) response.getData().getNode();
            List<Storefront.ProductEdge> data = collectionEdge.getProducts().getEdges();
            if (data.size() > 0) {
                staggeredcatname.setText(Html.fromHtml(tittle)

                );
                staggeredcatid.setText(id);
                staggeredcatname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openCategory(staggeredcatid.getText().toString(), staggeredcatname.getText().toString());
                    }
                });
                sectionviewall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openCategory(staggeredcatid.getText().toString(), staggeredcatname.getText().toString());
                    }
                });

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
                                List<Storefront.ProductEdge> allowedList = new ArrayList<>();
                                for (int i = 0; i < productEdges.size(); i++) {
                                    ArrayList<String> tags = (ArrayList<String>) productEdges.get(i).getNode().getTags();
                                    for (int j = 0; j < MainActivity.productTags.size(); j++) {
                                        if (tags.contains(MainActivity.productTags.get(j))) {
                                            allowedList.add(productEdges.get(i));
                                            //productEdges.remove(i);
                                        }
                                    }
                                }

                                data.addAll(allowedList);
                            }

                            @Override
                            public void onError(Throwable e) {
                            }
                        });//done
                /***************************************** Doha Discount API *********************************************************/
                String arr_id = "";
                final JSONObject[] cart_discount = new JSONObject[1];
                Iterator iterator = data.iterator();
                Storefront.ProductEdge storefront;
                while (iterator.hasNext()) {
                    storefront = (Storefront.ProductEdge) iterator.next();
                    arr_id = arr_id + MainActivity.getBase64Decode(storefront.getNode().getId().toString()) + ",";
                    Log.d("Product IDs", "" + arr_id);
                }
                apiService = ApiClient.getDohaWholesaleClient(getActivity()).create(ApiInterface.class);
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
                                    if (object.get("cart_discount") instanceof JSONObject) {
                                        cart_discount[0] = object.getJSONObject("cart_discount");
                                        MainActivity.mergeJSONObjects(cart_discount[0]);
                                    }

                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                                    staggeredproducts.setLayoutManager(linearLayoutManager);
                                    RelatedProducts_Adapter adapter = new RelatedProducts_Adapter(getActivity(), data, false);
                                    staggeredproducts.setAdapter(adapter);
                                    staggaredgridsection.setVisibility(View.VISIBLE);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, getActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /***************************************** Doha Discount API *********************************************************/


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void process9Grids(String id, GraphResponse<Storefront.QueryRoot> response, String tittle) {
        try {
            Storefront.Collection collectionEdge = (Storefront.Collection) Objects.requireNonNull(response.getData()).getNode();
            grid9data = collectionEdge.getProducts().getEdges();
            Log.i("grid9imagesection", "" + grid9data.size());
            Log.i("grid9imagesection1", "" + collectionEdge.getProducts().getEdges().size());
            if (grid9data.size() > 0) {
                grid9tittle.setText(Html.fromHtml(tittle));
                grid9tittle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openCategory(catid.getText().toString(), grid9tittle.getText().toString());
                    }
                });
                catviewallcard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openCategory(catid.getText().toString(), grid9tittle.getText().toString());
                    }
                });
                catid.setText(id);

                Observable.fromIterable(grid9data).subscribeOn(Schedulers.io())
                        .filter(x -> x.getNode().getAvailableForSale())
                        .toList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<List<Storefront.ProductEdge>>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onSuccess(List<Storefront.ProductEdge> productEdges) {
                                List<Storefront.ProductEdge> allowedList = new ArrayList<>();
                                for (int i = 0; i < productEdges.size(); i++) {
                                    ArrayList<String> tags = (ArrayList<String>) productEdges.get(i).getNode().getTags();
                                    for (int j = 0; j < MainActivity.productTags.size(); j++) {
                                        if (tags.contains(MainActivity.productTags.get(j))) {
                                            allowedList.add(productEdges.get(i));
                                            //productEdges.remove(i);
                                        }
                                    }
                                }
                                grid9data.addAll(allowedList);
                            }

                            @Override
                            public void onError(Throwable e) {
                            }
                        });//done


                Iterator<Storefront.ProductEdge> iterator = grid9data.iterator();
                int counter = 0;
                Storefront.ProductEdge edge;
                while (iterator.hasNext()) {
                    edge = iterator.next();
                    counter = counter + 1;
                    /*Log.i("getTransformedSrc1",""+edge.getNode().getImages().getEdges());
                    Log.i("getTransformedSrc2",""+edge.getNode().getImages().getEdges().get(0));
                    Log.i("getTransformedSrc3",""+edge.getNode().getImages().getEdges().get(0).getNode());
                    Log.i("getTransformedSrc4",""+edge.getNode().getImages().getEdges().get(0).getNode().getTransformedSrc());*/
                    gridData(counter, edge.getNode().getImages().getEdges().get(0).getNode().getTransformedSrc(), edge.getNode().getId().toString());
                    //gridData(counter,"https://wow.olympus.eu/webfile/img/1632/oly_testwow_stage.jpg",edge.getNode().getId().toString());
                }
                grid9imagesection.setVisibility(View.VISIBLE);
                catviewallcard.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openCategory(String id, String name) {
        try {
            Intent intent = new Intent(getActivity(), ProductListing.class);
            intent.putExtra("cat_id", id);
            intent.putExtra("cat_name", name);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openProduct(String origin, int position, String id) {
        try {
            Intent intent = new Intent(getActivity(), ProductView.class);
            intent.putExtra("object", (Serializable) grid9data.get(position));
            intent.putExtra("id", id);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        try {
            super.onDestroyView();
            unbinder.unbind();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pageSwitcher(int seconds) {
        timer = new Timer(); // At this line a new Thread will be created
        timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 1000); // delay
        // in
        // milliseconds
    }

    public String getBase64Encode(String id) {
        byte[] data = Base64.encode(id.getBytes(), Base64.DEFAULT);
        try {
            id = new String(data, "UTF-8").trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return id;
    }

    public void gridData(int index, String url, String id) {
        try {
            switch (index) {
                case 1:
                    cat1id.setText(id);
                    cat1section.setVisibility(View.VISIBLE);
                    Glide.with(getActivity())
                            .load(url)
                            .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder).dontTransform())
                            .into(cat1image);
                    cat1section.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openProduct("9grid", 0, cat1id.getText().toString());
                        }
                    });
                    break;
                case 2:
                    cat2id.setText(id);
                    cat2section.setVisibility(View.VISIBLE);
                    Glide.with(getActivity())
                            .load(url)
                            .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder).dontTransform())
                            .into(cat2image);
                    cat2section.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openProduct("9grid", 1, cat2id.getText().toString());
                        }
                    });
                    break;
                case 3:
                    cat3id.setText(id);
                    cat3section.setVisibility(View.VISIBLE);
                    Glide.with(getActivity())
                            .load(url)
                            .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder).dontTransform())
                            .into(cat3image);
                    cat3section.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openProduct("9grid", 2, cat3id.getText().toString());
                        }
                    });
                    break;
                case 4:
                    cat4id.setText(id);
                    cat4section.setVisibility(View.VISIBLE);
                    Glide.with(getActivity())
                            .load(url)
                            .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder).dontTransform())
                            .into(cat4image);
                    cat4section.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openProduct("9grid", 3, cat4id.getText().toString());
                        }
                    });
                    break;
                case 5:
                    cat5id.setText(id);
                    cat5section.setVisibility(View.VISIBLE);
                    Glide.with(getActivity())
                            .load(url)
                            .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder).dontTransform())
                            .into(cat5image);
                    cat5section.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openProduct("9grid", 4, cat5id.getText().toString());
                        }
                    });
                    break;
                case 6:
                    cat6id.setText(id);
                    cat6section.setVisibility(View.VISIBLE);
                    Glide.with(getActivity())
                            .load(url)
                            .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder).dontTransform())
                            .into(cat6image);
                    cat6section.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openProduct("9grid", 5, cat6id.getText().toString());
                        }
                    });
                    break;
                case 7:
                    cat7id.setText(id);
                    cat7section.setVisibility(View.VISIBLE);
                    Glide.with(getActivity())
                            .load(url)
                            .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder).dontTransform())
                            .into(cat7image);
                    cat7section.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openProduct("9grid", 6, cat7id.getText().toString());
                        }
                    });
                    break;
                case 8:
                    cat8id.setText(id);
                    cat8section.setVisibility(View.VISIBLE);
                    Glide.with(getActivity())
                            .load(url)
                            .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder).dontTransform())
                            .into(cat8image);
                    cat8section.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openProduct("9grid", 7, cat8id.getText().toString());
                        }
                    });
                    break;
                case 9:
                    cat9id.setText(id);
                    cat9section.setVisibility(View.VISIBLE);
                    Glide.with(getActivity())
                            .load(url)
                            .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder).dontTransform())
                            .into(cat9image);
                    cat9section.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openProduct("9grid", 8, cat9id.getText().toString());
                        }
                    });
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("Recent", "IN10");
        Log.i("Recent", "IN10" + data.getRecentlyViewed());
        if (data.getRecentlyViewed() != null) {
            recentsproducts.setHasFixedSize(true);
            GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
            mLayoutManager.setReverseLayout(true);
            recentsproducts.setLayoutManager(mLayoutManager);
            recentsproducts.setNestedScrollingEnabled(false);
            RecentlyViewedProducts_Adapter related = new RecentlyViewedProducts_Adapter(getActivity(), data.getRecentlyViewed());
            recentsproducts.setAdapter(related);
            related.notifyDataSetChanged();
            // recentlyviewedsection.setVisibility(View.VISIBLE);
        } else {
            Log.i("Recent", "IN");
            recentlyviewedsection.setVisibility(View.GONE);
        }
        if (collection_widget != null && sixcollections.getChildCount()==0) {
            try {
                //  fetchCollectionData(collection_widget.getJSONObject(2).getString("id"),"thirdcollection",10,collection_widget.getJSONObject(2).getString("title"));
                fetchCollectionData(collection_widget.getJSONObject(0).getString("id"), "sixthcollection", 50, collection_widget.getJSONObject(0).getString("title"));

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class RemindTask extends TimerTask {

        @Override
        public void run() {

            // As the TimerTask run on a seprate thread from UI thread we have
            // to call runOnUiThread to do work on UI thread.
            if (getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                public void run() {

                    if (page > banners_widget.length()) {
                        page = 0;
                        MageNative_homepagebanner.setCurrentItem(page++);
                    } else {
                        MageNative_homepagebanner.setCurrentItem(page++);
                    }
                }
            });

        }
    }

    class RemindTask2 extends TimerTask {

        @Override
        public void run() {

            // As the TimerTask run on a seprate thread from UI thread we have
            // to call runOnUiThread to do work on UI thread.
            if (getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                public void run() {

                    if (page2 > banners_widget2.length()) {
                        page2 = 0;
                        middlebanner.setCurrentItem(page2++);
                    } else {
                        middlebanner.setCurrentItem(page2++);
                    }
                }
            });

        }
    }
}
