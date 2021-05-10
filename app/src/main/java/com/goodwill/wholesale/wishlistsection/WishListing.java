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
package com.goodwill.wholesale.wishlistsection;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.goodwill.wholesale.checkoutsection.CartListing;
import com.goodwill.wholesale.checkoutsection.CheckoutLineItems;
import com.goodwill.wholesale.maincontainer.MainActivity;
import com.goodwill.wholesale.productviewsection.ProductView;
import com.goodwill.wholesale.requestsection.ApiClient;
import com.goodwill.wholesale.storagesection.LocalData;
import com.goodwill.wholesale.storefrontqueries.Query;
import com.goodwill.wholesale.storefrontresponse.AsyncResponse;
import com.goodwill.wholesale.storefrontresponse.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WishListing extends MainActivity {
    LocalData data = null;
    CheckoutLineItems items = null;
    String count = "0";
    GraphClient client = null;
    @Nullable
    @BindView(R.id.wishlistsection)
    LinearLayout wishlistsection;
    Storefront.Product product = null;
    Storefront.ProductEdge edge = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup content = findViewById(R.id.MageNative_frame_container);
        getLayoutInflater().inflate(R.layout.wishlisting, content, true);
        ButterKnife.bind(WishListing.this);
        data = new LocalData(WishListing.this);
        client = ApiClient.getGraphClient(WishListing.this, true);
        items = new CheckoutLineItems(WishListing.this);
        showbackbutton();
        showTittle(getResources().getString(R.string.saved_items));
    }

    @Override
    protected void onResume() {
        if (data.getLineItems() != null) {
            count = String.valueOf(items.getItemcounts());
        } else {
            count = "0";
        }
        if (data.getWishList() != null) {
            if (wishlistsection.getChildCount() > 0) {
                wishlistsection.removeAllViews();
            }
            createWishList();
        } else {
            Toast.makeText(WishListing.this, getResources().getString(R.string.nowish), Toast.LENGTH_LONG).show();
            finish();
        }
        invalidateOptionsMenu();
        super.onResume();
    }

    private void createWishList() {
        try {
            JSONObject object = new JSONObject(data.getWishList());
            JSONArray array = object.names();
            JSONObject jsonObject = null;
            View view = null;
            ImageView image = null;
            TextView product_id = null;
            TextView name = null;
            TextView addtocart = null;
            TextView variant_id = null;
            TextView variant_count = null;
            TextView MageNative_specialprice = null;
            TextView MageNative_reguralprice = null;
            ImageView deleteicon = null;
            Spinner variantsSpnr = null;
            EditText quantity = null;
            String variant_idd = "";
            for (int i = 0; i < array.length(); i++) {
                jsonObject = object.getJSONObject(array.getString(i));
                view = View.inflate(WishListing.this, R.layout.magenative_wish_comp, null);
                image = view.findViewById(R.id.image);
                product_id = view.findViewById(R.id.product_id);
                name = view.findViewById(R.id.name);
                deleteicon = view.findViewById(R.id.deleteicon);
                addtocart = view.findViewById(R.id.addtocart);
                variant_count = view.findViewById(R.id.variant_count);
                MageNative_specialprice = view.findViewById(R.id.MageNative_specialprice);
                MageNative_reguralprice = view.findViewById(R.id.MageNative_reguralprice);
                variant_id = view.findViewById(R.id.variant_id);
                variantsSpnr = view.findViewById(R.id.variants);
                quantity = view.findViewById(R.id.quantity);
                Glide.with(WishListing.this)
                        .load(jsonObject.getString("image"))
                        .thumbnail(0.5f)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.placeholder)
                        )
                        .into(image);
                variant_id.setText(jsonObject.getString("varinatid"));
                variant_count.setText(jsonObject.getString("varinats"));
                product_id.setText(jsonObject.getString("product_id"));
                name.setText(jsonObject.getString("product_name"));

                Log.e("jsonObject", "" + jsonObject);

                if (jsonObject.has("product_specialprice")) {
                    String specialprice = jsonObject.getString("product_specialprice");
                    MageNative_reguralprice.setText("QAR" + specialprice);
                    MageNative_reguralprice.setPaintFlags(MageNative_reguralprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    MageNative_reguralprice.setTextColor(getResources().getColor(R.color.black));
                    MageNative_specialprice.setVisibility(View.VISIBLE);
                    MageNative_specialprice.setText("QAR" + jsonObject.getString("product_price"));
                } else {
                    MageNative_specialprice.setVisibility(View.GONE);
                    MageNative_reguralprice.setText("QAR" + jsonObject.getString("product_price"));
                    MageNative_reguralprice.setPaintFlags(MageNative_reguralprice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }


                TextView finalProduct_id = product_id;
                ImageView finalDeleteicon = deleteicon;
                deleteicon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {

                            JSONObject object1 = new JSONObject(data.getWishList());
                            object1.remove(finalProduct_id.getText().toString());
                            data.saveWishList(object1);
                            View view1 = (View) finalDeleteicon.getParent();
                            wishlistsection.removeView(view1);
                            if (wishlistsection.getChildCount() == 0) {
                                onResume();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                TextView finalVariant_id = variant_id;
                TextView finalName = name;
                TextView finalAddtocart1 = addtocart;
                final int[] quant = {1};
                EditText finalQuantity = quantity;
                addtocart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {

                            if (!finalQuantity.getText().toString().isEmpty()) {
                                quant[0] = Integer.parseInt(finalQuantity.getText().toString());
                                if (data.getWishList() != null) {
                                    JSONObject jsonObject = new JSONObject(data.getWishList());
                                    if (jsonObject.has(finalProduct_id.getText().toString())) {
                                        jsonObject.remove(finalProduct_id.getText().toString());
                                        data.saveWishList(jsonObject);
                                    }
                                }
                                if (data.getCheckoutId() != null) {
                                    data.clearCheckoutId();
                                    data.clearCoupon();
                                }
                                if (data.getLineItems() != null) {
                                    int qty = 1;
                                    JSONObject object = new JSONObject(data.getLineItems());
                                    if (object.has(finalVariant_id.getText().toString())) {
                                        qty = Integer.parseInt(object.getString(finalVariant_id.getText().toString()));
                                        qty = qty + quant[0];
                                    }
                                    object.put(finalVariant_id.getText().toString(), qty);
                                    data.saveLineItems(object);
                                } else {
                                    JSONObject object = new JSONObject();
                                    object.put(finalVariant_id.getText().toString(), quant[0]);
                                    data.saveLineItems(object);
                                }
                                String message = finalName.getText().toString() + " " + getResources().getString(R.string.addedtocart);
                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
                                snackbar.show();
                                changecount();
                                View view1 = (View) finalVariant_id.getParent();
                                wishlistsection.removeView(view1);
                                Log.d("removedd", "" + wishlistsection.getChildCount());
                                if (wishlistsection.getChildCount() == 0) {
                                    onResume();
                                }
                            } else {
                                Toast.makeText(WishListing.this, "Please Enter Quantity", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(WishListing.this, ProductView.class);
                        intent.putExtra("id", finalProduct_id.getText().toString());
                        startActivity(intent);
                        overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                    }
                });

                /******************************************Request For Variants**********************************************************************/

                try {
                    QueryGraphCall call = client.queryGraph(Query.getSingleProduct(finalProduct_id.getText().toString()));
                    Spinner finalVariantsSpnr = variantsSpnr;
                    TextView finalVariant_id1 = variant_id;
                    TextView finalName1 = name;
                    TextView finalMageNative_reguralprice = MageNative_reguralprice;
                    TextView finalMageNative_specialprice = MageNative_specialprice;
                    Response.getGraphQLResponse(call, new AsyncResponse() {
                        @Override
                        public void finalOutput(@NonNull Object output, @NonNull boolean error) {
                            if (error) {
                                GraphResponse<Storefront.QueryRoot> response = ((GraphCallResult.Success<Storefront.QueryRoot>) output).getResponse();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Storefront.Product productEdge = null;

                                            if (finalProduct_id.getText().toString().contains("*#*")) {
                                                productEdge = response.getData().getProductByHandle();
                                            } else {
                                                productEdge = (Storefront.Product) response.getData().getNode();
                                            }


                                            Storefront.ImageConnection connection = productEdge.getImages();
                                            Iterator imageiterator = connection.getEdges().iterator();

                                            Storefront.ProductVariantConnection variants = productEdge.getVariants();
                                            List<Storefront.ProductVariantEdge> productvariantedge = variants.getEdges();
                                            Log.i("productvariantedge", "" + variants);
                                            Log.i("productvariantedge", "" + productvariantedge.size());

                                            /**************************************** Variants Code ******************************************************/
                                            Log.i("productvariantedge", "" + productvariantedge);
                                            Log.i("productvariantedge", "" + productvariantedge.size());
                                            int totalvariantsize = productvariantedge.size();
                                            if (productvariantedge != null) {
                                                Log.i("productvariantedge", "" + productvariantedge.size());
                                                Iterator iterator = productvariantedge.iterator();
                                                Storefront.ProductVariantEdge edge = null;
                                                List<Storefront.SelectedOption> selctedoption = null;
                                                Storefront.SelectedOption option = null;
                                                List<String> categories = categories = new ArrayList<String>();
                                                while (iterator.hasNext()) {
                                                    edge = (Storefront.ProductVariantEdge) iterator.next();


                                                    if (edge.getNode().getAvailableForSale()) {
                                                        selctedoption = edge.getNode().getSelectedOptions();
                                                        if (productvariantedge.size() >= 1) {


                                                            Iterator iterator1 = selctedoption.iterator();
                                                            int counter = 0;
                                                            while (iterator1.hasNext()) {
                                                                counter = counter + 1;
                                                                option = (Storefront.SelectedOption) iterator1.next();
                                                                //String finalvalue=option.getName()+" : "+option.getValue();
                                                                String finalvalue = option.getValue();

                                                                String title = finalName1.getText().toString();
                                                                HashMap<String, String> mapPrice = null;
                                                                if (counter == 1) {
                                                                    MainActivity.mapVariantsIds.put(title + finalvalue, edge.getNode().getId().toString());
                                                                    mapPrice = new HashMap();
                                                                    if (edge.getNode().getCompareAtPrice() != null) {
                                                                        mapPrice.put("special", edge.getNode().getCompareAtPrice().toString());
                                                                        mapPrice.put("normal", edge.getNode().getPrice().toString());
                                                                    } else {
                                                                        mapPrice.put("normal", edge.getNode().getPrice().toString());
                                                                    }
                                                                    Log.d("productvariantedge", "" + finalvalue);
                                                                    categories.add(finalvalue);


                                                                }
                                                                if (counter == 2) {
                                                                    MainActivity.mapVariantsIds.put(title + finalvalue, edge.getNode().getId().toString());
                                                                    mapPrice = new HashMap();
                                                                    if (edge.getNode().getCompareAtPrice() != null) {
                                                                        mapPrice.put("special", edge.getNode().getCompareAtPrice().toString());
                                                                        mapPrice.put("normal", edge.getNode().getPrice().toString());
                                                                    } else {
                                                                        mapPrice.put("normal", edge.getNode().getPrice().toString());
                                                                    }
                                                                    MainActivity.mapVariantsPrices.put(title + finalvalue, mapPrice);
                                                                    Log.d("productvariantedge", "" + finalvalue);
                                                                    categories.add(finalvalue);

                                                                }
                                                                if (counter == 3) {
                                                                    MainActivity.mapVariantsIds.put(title + finalvalue, edge.getNode().getId().toString());
                                                                    mapPrice = new HashMap();
                                                                    if (edge.getNode().getCompareAtPrice() != null) {
                                                                        mapPrice.put("special", edge.getNode().getCompareAtPrice().toString());
                                                                        mapPrice.put("normal", edge.getNode().getPrice().toString());
                                                                    } else {
                                                                        mapPrice.put("normal", edge.getNode().getPrice().toString());
                                                                    }
                                                                    Log.d("productvariantedge", "" + finalvalue);
                                                                    categories.add(finalvalue);

                                                                }
                                                            }


                                                        } else {
                                                            finalVariant_id1.setText(edge.getNode().getId().toString());

                                                        }
                                                    } else {
                                                        if (totalvariantsize == 1) {

                                                            finalAddtocart1.setText(getResources().getString(R.string.outofstock));
                                                            finalAddtocart1.setEnabled(false);
                                                        }
                                                    }
                                                }

                                                Log.d("categoriesvarinats", "" + MainActivity.mapVariantsIds);
                                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(WishListing.this, R.layout.text, categories);
                                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                finalVariantsSpnr.setAdapter(dataAdapter);
                                                final String[] finalVariant_id = {finalVariant_id1.getText().toString()};
                                                final HashMap<String, String>[] map = new HashMap[]{null};
                                                finalVariantsSpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                    @Override
                                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                        String varId = finalName.getText().toString() + finalVariantsSpnr.getSelectedItem();
                                                        finalVariant_id[0] = MainActivity.mapVariantsIds.get(varId);
                                                        Log.d("SelectedVarId*", "" + varId);
                                                        Log.d("SelectedVarId", "" + finalVariant_id[0]);
                                                        finalVariant_id1.setText(finalVariant_id[0]);
                                                        map[0] = MainActivity.mapVariantsPrices.get(varId);
                                                        if (!map[0].containsKey("special")) {
                                                            finalMageNative_specialprice.setVisibility(View.GONE);
                                                            finalMageNative_reguralprice.setText(data.getMoneyFormat() + map[0].get("normal"));
                                                            finalMageNative_reguralprice.setPaintFlags(finalMageNative_reguralprice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                                                        } else {
                                                            if (map[0].get("special").compareTo(map[0].get("normal")) == 1) {
                                                                finalMageNative_reguralprice.setText(data.getMoneyFormat() + map[0].get("special"));
                                                                finalMageNative_reguralprice.setPaintFlags(finalMageNative_reguralprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                                                finalMageNative_reguralprice.setTextColor(getResources().getColor(R.color.black));
                                                                finalMageNative_specialprice.setVisibility(View.VISIBLE);
                                                                finalMageNative_specialprice.setText(data.getMoneyFormat() + map[0].get("normal"));
                                                            } else {
                                                                finalMageNative_specialprice.setVisibility(View.GONE);
                                                                finalMageNative_reguralprice.setText(data.getMoneyFormat() + map[0].get("normal"));
                                                                finalMageNative_reguralprice.setPaintFlags(finalMageNative_reguralprice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                                                            }

                                                        }

                                                        Log.d("SelectedVarIdPrice", "" + map[0]);
                                                    }

                                                    @Override
                                                    public void onNothingSelected(AdapterView<?> parent) {

                                                    }
                                                });

                                            }
                                            /**************************************** Variants Code ******************************************************/
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } else {
                                Log.i("ResponseError", "" + output.toString());
                            }
                        }
                    }, WishListing.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                /******************************************Request For Variants**********************************************************************/


                wishlistsection.addView(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_wish, menu);
        MenuItem item = menu.findItem(R.id.MageNative_action_cart);
        item.setActionView(R.layout.magenative_feed_update_count);
        View notifCount = item.getActionView();
        TextView textView = notifCount.findViewById(R.id.MageNative_hotlist_hot);
        textView.setText(count);
        notifCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CartListing.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        invalidateOptionsMenu();
        super.onBackPressed();
    }

    public void changecount() {
        if (data.getLineItems() != null) {
            count = String.valueOf(items.getItemcounts());
        } else {
            count = "0";
        }
        invalidateOptionsMenu();
    }
}
