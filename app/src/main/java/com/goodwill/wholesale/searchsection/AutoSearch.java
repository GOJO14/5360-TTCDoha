/*
 * Copyright/**
 *          * CedCommerce
 *           *
 *           * NOTICE OF LICENSE
 *           *
 *           * This source file is subject to the End User License Agreement (EULA)
 *           * that is bundled with this package in the file LICENSE.txt.
 *           * It is also available through the world-wide-web at this URL:
 *           * http://cedcommerce.com/license-agreement.txt
 *           *
 *           * @category  Ced
 *           * @package   MageNative
 *           * @author    CedCommerce Core Team <connect@cedcommerce.com >
 *           * @copyright Copyright CEDCOMMERCE (http://cedcommerce.com/)
 *           * @license      http://cedcommerce.com/license-agreement.txt
 *
 */
package com.goodwill.wholesale.searchsection;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.shopify.buy3.GraphCallResult;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.QueryGraphCall;
import com.shopify.buy3.Storefront;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.maincontainer.MainActivity;
import com.goodwill.wholesale.productviewsection.ProductView;
import com.goodwill.wholesale.requestsection.ApiClient;
import com.goodwill.wholesale.storefrontqueries.Query;
import com.goodwill.wholesale.storefrontresponse.AsyncResponse;
import com.goodwill.wholesale.storefrontresponse.Response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AutoSearch extends MainActivity {
    public List<Storefront.ProductEdge> productedge = null;
    GraphClient client;
    String cursor = "nocursor";
    @Nullable
    @BindView(R.id.search)
    AutoCompleteTextView search;
    private ArrayList<String> suggestions;
    private HashMap<String, String> name_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup content = findViewById(R.id.MageNative_frame_container);
        getLayoutInflater().inflate(R.layout.magenative_searchpage, content, true);
        ButterKnife.bind(AutoSearch.this);
        showbackbutton();
        showTittle(getResources().getString(R.string.SearchYourProducts));
        suggestions = new ArrayList<String>();
        name_id = new HashMap<>();
        client = ApiClient.getGraphClient(AutoSearch.this, true);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Intent intent = new Intent(getApplicationContext(), SearchView.class);
                    intent.putExtra("cat_id", search.getText().toString());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                    return true;
                }
                return false;
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(@NonNull final CharSequence s, int start, int before, int count) {
                Log.i("Keyword", "" + s.toString());
                if (s.length() >= 4) {
                    searchResult(s.toString());
                }
                popuplate();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void searchResult(String keyword) {
        try {
            QueryGraphCall call = client.queryGraph(Query.getAutoSearchProducts(cursor, keyword, Storefront.ProductSortKeys.CREATED_AT, false));
            Response.getGraphQLResponse(call, new AsyncResponse() {
                @Override
                public void finalOutput(@NonNull Object output, @NonNull boolean error) {
                    if (error) {
                        GraphResponse<Storefront.QueryRoot> response = ((GraphCallResult.Success<Storefront.QueryRoot>) output).getResponse();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                attachDataToTextview(response);
                            }
                        });
                    } else {
                        Log.i("ResponseError", "" + output.toString());
                    }
                }
            }, AutoSearch.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void attachDataToTextview(GraphResponse<Storefront.QueryRoot> response) {
        try {
            List<Storefront.ProductEdge> data = response.getData().getShop().getProducts().getEdges();
            if (data.size() > 0) {


                if (productedge == null) {
                    productedge = data;
                } else {
                    productedge.addAll(data);
                }
                Iterator<Storefront.ProductEdge> iterator = productedge.iterator();
                Storefront.ProductEdge edge = null;
                ArrayList<Storefront.ProductEdge> pe = new ArrayList<>();
                while (iterator.hasNext()) {
                    edge = iterator.next();
                    ArrayList<String> tags = (ArrayList<String>) edge.getNode().getTags();
                    for (int j = 0; j < productTags.size(); j++) {
                        if (tags.contains(MainActivity.productTags.get(j))) {
                          pe.add(edge);
                        }
                    }

                    for (int j=0;j<pe.size();j++){
                        String finalstring = pe.get(j).getNode().getTitle() + "#" + pe.get(j).getNode().getImages().getEdges().get(0).getNode().getTransformedSrc() + "#" + pe.get(j).getNode().getId();
                        if (suggestions.size() > 0) {
                            if (!(suggestions.contains(finalstring))) {
                                suggestions.add(finalstring);
                            }
                        } else {
                            suggestions.add(finalstring);
                        }
                    }
                }

                Log.e("suggestions",""+suggestions);
            }
            if (suggestions.size() > 0) {
                popuplate();
            } else {
                Toast.makeText(AutoSearch.this, getResources().getString(R.string.noresult), Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void popuplate() {
        try {
            if (suggestions.size() > 0) {
                Collections.reverse(suggestions);
                final CustomListAdapter adapter = new CustomListAdapter(getApplicationContext(), R.layout.search, suggestions);
               /* search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(@NonNull AdapterView<?> parent, View view, int position, long id) {
                        TextView name = view.findViewById(R.id.textView);
                        TextView product_id = view.findViewById(R.id.product_id);
                        String[] parts = parent.getItemAtPosition(position).toString().split("#");
                        search.setText(name.getText().toString());
                        product_id.setText(productedge.get(position).getNode().getId()+"");
                        *//*Intent intent = new Intent(getApplicationContext(), ProductView.class);
                        intent.putExtra("id", parts[2]);
                        intent.putExtra("object", productedge.get(position));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);*//*
                    }
                });*/
                search.setThreshold(1);
                search.setAdapter(adapter);
                search.showDropDown();
                search.setSelection(search.getText().length());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        return false;
    }
}
