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
 *           * @license   http://cedcommerce.com/license-agreement.txt
 *
 */
package com.goodwill.wholesale.checkoutsection;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.icu.math.BigDecimal;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.currencysection.CurrencyFormatter;
import com.goodwill.wholesale.loadersection.Loader;
import com.goodwill.wholesale.loginandregistrationsection.Login;
import com.goodwill.wholesale.maincontainer.MainActivity;
import com.goodwill.wholesale.requestsection.ApiClient;
import com.goodwill.wholesale.requestsection.ApiInterface;
import com.goodwill.wholesale.storagesection.LocalData;
import com.goodwill.wholesale.storefrontqueries.MutationQuery;
import com.goodwill.wholesale.storefrontresponse.AsyncResponse;
import com.goodwill.wholesale.storefrontresponse.Response;
import com.shopify.buy3.GraphCallResult;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.MutationGraphCall;
import com.shopify.buy3.Storefront;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class CartListing extends MainActivity {
    public static Spinner time_storepickup_slots;
    static String checkout_id = "noid";
    static String is = "noid";
    static String sweekdayname, sdate, smonthname, syear;
    static String deliveryDate = "";
    static String deliveryTime = "";
    static TextView storepickup_calendarslots;
    static LinearLayout storepickup_slotssection;
    static JSONObject attributesobject;
    static ApiInterface apiServiceOneTimeDelivery;
    static ApiInterface apiServiceOneTimeDelivery1;
    final boolean[] coupon = {false};
    public String min_date = "";
    @Nullable
    @BindView(R.id.cartcontainer)
    LinearLayout cartcontainer;
    @Nullable
    @BindView(R.id.gift_header)
    TextView gift_header;
    @Nullable
    @BindView(R.id.giftcontainer)
    LinearLayout giftcontainer;
    @Nullable
    @BindView(R.id.relativemain)
    RelativeLayout relativemain;
    @Nullable
    @BindView(R.id.taxsection)
    RelativeLayout taxsection;
    @Nullable
    @BindView(R.id.subtotalprice)
    TextView subtotalprice;
    @Nullable
    @BindView(R.id.discountprice)
    TextView discountprice;
    @Nullable
    @BindView(R.id.taxprice)
    TextView taxprice;
    @Nullable
    @BindView(R.id.grandtotalprice)
    TextView grandtotalprice;
    @Nullable
    @BindView(R.id.MageNative_applycoupantag)
    EditText MageNative_applycoupantag;
    @Nullable
    @BindView(R.id.MageNative_applycoupan)
    Button MageNative_applycoupan;
    @Nullable
    @BindView(R.id.upperpart)
    LinearLayout upperpart;
    @Nullable
    @BindView(R.id.MageNative_couponcode)
    LinearLayout MageNative_couponcode;
    @Nullable
    @BindView(R.id.imageView_applycoupon)
    ImageView imageView_applycoupon;
    @Nullable
    @BindView(R.id.MageNative_checkout)
    TextView MageNative_checkout;
    @Nullable
    @BindView(R.id.deliverysection)
    RelativeLayout deliverysection;
    LocalData data = null;
    GraphClient client = null;
    String cursor = "nocursor";
    CheckoutLineItems items = null;
    boolean isfromqtycheck = false;
    String couponcode = "nocouponcode";
    boolean isapplied = false;
    String checkouturl;
    List<Storefront.CheckoutLineItemEdge> lineedges;
    ApiInterface apiService;
    String arr_id = "";
    String arr_quantity = "";
    double discount = 0.0d;
    String invalid_day = "";
    int max_date = 0;
    Loader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            ViewGroup content = findViewById(R.id.MageNative_frame_container);
            getLayoutInflater().inflate(R.layout.magenative_activity_cart_listing, content, true);
            ButterKnife.bind(CartListing.this);
            showbackbutton();
            showTittle(getResources().getString(R.string.shopingcart));
            data = new LocalData(CartListing.this);
            items = new CheckoutLineItems(CartListing.this);
            client = ApiClient.getGraphClient(CartListing.this, true);
            apiServiceOneTimeDelivery = ApiClient.getOnetimeDeliveryClient(CartListing.this).create(ApiInterface.class);
            apiServiceOneTimeDelivery1 = ApiClient.getOnetimeDeliveryClient(CartListing.this).create(ApiInterface.class);
            apiService = ApiClient.getCustomAPisClient(CartListing.this).create(ApiInterface.class);
            client = ApiClient.getGraphClient(CartListing.this, true);
            attributesobject = new JSONObject();
            storepickup_calendarslots = (TextView) findViewById(R.id.storepickup_calendarslots);
            storepickup_slotssection = (LinearLayout) findViewById(R.id.storepickup_slotssection);
            time_storepickup_slots = (Spinner) findViewById(R.id.time_storepickup_slots);
            loader = new Loader(CartListing.this);
            Call<ResponseBody> c = apiServiceOneTimeDelivery.details(getResources().getString(R.string.mid));
            Response.getRetrofitResponse(c, new AsyncResponse() {
                @Override
                public void finalOutput(Object output, boolean error) throws Exception {
                    if (error) {
                        try {
                            loader.show();
                            deliverysection.setVisibility(View.VISIBLE);
                            JSONObject jsonObject = new JSONObject(output.toString());
                            JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                            //   min_date = jsonObjectData.getString("min_date");
                            invalid_day = jsonObjectData.getJSONArray("unavailable_days").getString(0);
                            //max_date = Integer.parseInt(jsonObjectData.getString("max_date").replace("+", "").replace("d", "").toString());

                            // datetext.setText(jsonObjectData.getString("calendar_heading").replace("&amp;","&"));

                            /*storepickup_calendarslots.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showToDatePickerDialog(view);
                                    loadTime(storepickup_calendarslots.getText().toString());
                                }
                            });*/
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, CartListing.this);

            storepickup_calendarslots.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showToDatePickerDialog(view);
                    loadTime(storepickup_calendarslots.getText().toString());
                }
            });

            if (data.getCheckoutId() != null) {
                checkout_id = data.getCheckoutId();
                isapplied = true;
            }
            if (data.getCoupon() != null) {
                couponcode = data.getCoupon();
                MageNative_applycoupan.setText(getResources().getString(R.string.remove));
                MageNative_applycoupantag.setText(couponcode);
                MageNative_applycoupantag.setEnabled(false);
            }

            MageNative_applycoupan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MageNative_applycoupantag.getText().toString().isEmpty()) {
                        Toast.makeText(CartListing.this, getResources().getString(R.string.empty), Toast.LENGTH_LONG).show();
                    } else {
                        if (MageNative_applycoupan.getText().equals(getResources().getString(R.string.remove))) {
                            data.clearCheckoutId();
                            data.clearCoupon();
                            checkout_id = "noid";
                            couponcode = "nocouponcode";
                            isapplied = false;
                            getCartData();
                            MageNative_applycoupan.setText(getResources().getString(R.string.apply));
                            MageNative_applycoupantag.setText(" ");
                            MageNative_applycoupantag.setEnabled(true);
                        } else {
                            isapplied = true;
                            data.saveCouponCode(MageNative_applycoupantag.getText().toString());
                            couponcode = MageNative_applycoupantag.getText().toString();
                            getCartData();
                        }
                    }
                }
            });
            upperpart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (coupon[0]) {
                        imageView_applycoupon.setImageDrawable(getResources().getDrawable(R.drawable.caretdown));
                        MageNative_couponcode.setVisibility(View.GONE);
                        coupon[0] = false;
                    } else {
                        imageView_applycoupon.setImageDrawable(getResources().getDrawable(R.drawable.sortup));
                        MageNative_couponcode.setVisibility(View.VISIBLE);
                        coupon[0] = true;
                    }
                }
            });
            MageNative_checkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("attributesobject",attributesobject+"");

                    if (attributesobject.length() == 2) // for delivery date,Delivery Time and Delivery Day
                    {
/************************************************************************ Discount Section **************************************************************************************/

                        String checkOutString = "get_checkout_mobile.php?draft=1" + "&";
                        String items_arr = "";
                        String quantity = "";
                        String collections = "";
                        if (lineedges.size() > 0) {
                            Storefront.CheckoutLineItemEdge edge = null;

                            int sizee = lineedges.size() - 1;

                            for (int i = sizee; i >= 0; i--) {
                                edge = lineedges.get(i);
                                String price = "";

                                price = CurrencyFormatter.setsymbol(edge.getNode().getVariant().getPrice(), data.getMoneyFormat());

                                if (edge.getNode().getVariant().getCompareAtPrice() != null) {
                                    price = CurrencyFormatter.setsymbol(edge.getNode().getVariant().getCompareAtPrice(), data.getMoneyFormat());
                                    Log.e("vaibhav line_price if", price + "");
                                } else {
                                    price = CurrencyFormatter.setsymbol(edge.getNode().getVariant().getPrice(), data.getMoneyFormat());
                                    Log.e("vaibhav line_price else", price + "");
                                }

                                int size = edge.getNode().getVariant().getProduct().getCollections().getEdges().size();
                           /* for (int d=0;d<size;d++ ){
                                collections+="cart_collections["+getBase64Decode(edge.getNode().getVariant().getId().toString())+"][]="+getBase64Decode(edge.getNode().getVariant().getProduct().getCollections().getEdges().get(d).getNode().getId().toString())+"&";
                                Log.e("stark",""+getBase64Decode(edge.getNode().getVariant().getProduct().getCollections().getEdges().get(d).getNode().getId().toString()));

                            }*/
                                price = price.replace("QAR", "");
                                price = price.replace(",", "");
                                Float value = Float.parseFloat(price);
                                value = value * 100;
                                int pricee = Math.round(value);
                                int qty = Integer.parseInt(String.valueOf(edge.getNode().getQuantity()));
                                int line_price = (int) (value * qty);
                                collections = "";
                                items_arr += getBase64Decode(edge.getNode().getVariant().getId().toString()) + ",";
                                quantity += edge.getNode().getQuantity() + ",";

                            }

                        }
                  /*  if (isapplied) {
                        checkOutString += "is_volume_discount=true&is_product_option=false&store_id=" + getResources().getString(R.string.shopdomain) + "&discount_code="+isapplied;

                    } else {
                        checkOutString += "is_volume_discount=true&is_product_option=false&store_id=" + getResources().getString(R.string.shopdomain);
                    }*/
                        items_arr = items_arr.substring(0, items_arr.length() - 1);
                        quantity = quantity.substring(0, quantity.length() - 1);
                        checkOutString = checkOutString + "items_arr=" + items_arr + "&quantity_arr=" + quantity + "&delivery_date=" + deliveryDate + "&delivery_time=" + deliveryTime;
                        Log.d("Checkout ", "" + checkOutString);
                        Log.d("Checkout ", "" + collections);
                        Call<ResponseBody> call = apiService.getCheckoutUrl(checkOutString);
                        try {

                            Response.getRetrofitResponse(call, new AsyncResponse() {
                                @Override
                                public void finalOutput(@NonNull Object output, @NonNull boolean error) {
                                    Log.d("vaibhav VolumeOptions", "" + output);
                                    if (error) {
                                        System.out.println("output==getCheckoutUrl= " + output.toString());
                                        //Toast.makeText(ProductView.this, ""+output, Toast.LENGTH_SHORT).show();
                                        try {
                                            checkouturl = output.toString();
                                            if (data.isLogin()) {
                                                Intent intent = new Intent(CartListing.this, CheckoutWeblink.class);
                                                intent.putExtra("link", checkouturl);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                                            } else {
                                                showPopUp();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }, CartListing.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(CartListing.this, "Please select Date & Delivery Time", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            if (data.getLineItems() != null) {
                getCartData();
            } else {
                Toast.makeText(CartListing.this, getResources().getString(R.string.emptycart), Toast.LENGTH_LONG).show();
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void getCartData() {
        try {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                Log.i("CartDate", "" + formatter.format(date));
                data.saveCartDate(formatter.format(date));
                MutationGraphCall call = client.mutateGraph(MutationQuery.createCheckout(CartListing.this, cursor, couponcode, isapplied, checkout_id));
                Response.getMutationGraphQLResponse(call, new AsyncResponse() {
                    @Override
                    public void finalOutput(@NonNull Object output, @NonNull boolean error) {
                        if (error) {
                            GraphResponse<Storefront.Mutation> response = ((GraphCallResult.Success<Storefront.Mutation>) output).getResponse();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    List<Storefront.UserError> errors = null;
                                    if (isapplied) {
                                        errors = response.getData().getCheckoutDiscountCodeApply().getUserErrors();
                                    } else {
                                        errors = response.getData().getCheckoutCreate().getUserErrors();
                                    }
                                    if (errors.size() > 0) {
                                        boolean iserror = false;
                                        Iterator iterator = errors.iterator();
                                        String err = "";
                                        String csv = "";
                                        while (iterator.hasNext()) {
                                            Storefront.UserError error = (Storefront.UserError) iterator.next();
                                            err += error.getMessage();
                                            csv = android.text.TextUtils.join("-", error.getField());
                                            Log.e("USERerrors", "" + csv);
                                            if (error.getField().size() == 4) {
                                                if (error.getField().get(1).equals("lineItems") && error.getField().get(3).equals("quantity")) {
                                                    try {
                                                        iserror = true;
                                                        String str = error.getMessage().replaceAll("[^0-9]+", "");
                                                        JSONObject object = new JSONObject(data.getLineItems());
                                                        if (object.has(items.getLineItems().get(Integer.parseInt(error.getField().get(2))).getVariantId().toString())) {
                                                            if (Integer.parseInt(str.trim()) == 0) {
                                                                object.remove(items.getLineItems().get(Integer.parseInt(error.getField().get(2))).getVariantId().toString());
                                                            } else {
                                                                object.put(items.getLineItems().get(Integer.parseInt(error.getField().get(2))).getVariantId().toString(), Integer.parseInt(str.trim()));
                                                            }
                                                            data.saveLineItems(object);
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    if (isfromqtycheck) {
                                                        Toast.makeText(CartListing.this, err, Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }
                                        }
                                        if (csv.equals("discountCode")) {
                                            data.clearCheckoutId();
                                            data.clearCoupon();
                                            checkout_id = "noid";
                                            couponcode = "nocouponcode";
                                            isapplied = false;
                                            Toast.makeText(CartListing.this, err, Toast.LENGTH_LONG).show();
                                            getCartData();
                                        }
                                        if (iserror) {
                                            Toast.makeText(CartListing.this, err, Toast.LENGTH_LONG).show();
                                            getCartData();
                                        } else {
                                            Toast.makeText(CartListing.this, err, Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        processCartData(response);
                                    }
                                }
                            });
                        } else {
                            Log.i("ResponseError", "" + output.toString());
                        }
                    }
                }, CartListing.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processCartData(GraphResponse<Storefront.Mutation> response) {
        try {
            Storefront.Checkout checkout = null;
            if (isapplied) {
                checkout = response.getData().getCheckoutDiscountCodeApply().getCheckout();
                MageNative_applycoupan.setText(getResources().getString(R.string.remove));
                MageNative_applycoupantag.setText(couponcode);
                MageNative_applycoupantag.setEnabled(false);
            } else {
                checkout = response.getData().getCheckoutCreate().getCheckout();
            }
            checkout_id = checkout.getId().toString();
            if (data.getCoupon() != null) {
                data.saveCheckoutId(checkout_id);
            }
            checkouturl = checkout.getWebUrl();
            lineedges = checkout.getLineItems().getEdges();
            List<Storefront.CheckoutLineItemEdge> lineedges = checkout.getLineItems().getEdges();
            if (lineedges.size() > 0) {

                final Storefront.CheckoutLineItemEdge[] edge = {null};
                Iterator<Storefront.CheckoutLineItemEdge> iterator = lineedges.iterator();
                if (cartcontainer.getChildCount() > 0) {
                    cartcontainer.removeAllViews();
                }

                while (iterator.hasNext()) {
                    edge[0] = iterator.next();
                    arr_id = arr_id + MainActivity.getBase64Decode(edge[0].getNode().getVariant().getId().toString()) + ",";
                    arr_quantity = arr_quantity + edge[0].getNode().getQuantity().toString() + ",";
                }
                arr_id = arr_id.substring(0, arr_id.length() - 1);
                arr_quantity = arr_quantity.substring(0, arr_quantity.length() - 1);

                Log.e("getDohaWholeSale", "" + arr_id);
                Log.e("getDohaWholeSale", "" + arr_quantity);

                apiService = ApiClient.getDohaWholesaleClient(CartListing.this).create(ApiInterface.class);
                Call<ResponseBody> call = apiService.get_cart_offer_mobile(arr_id, arr_quantity);
                try {
                    Storefront.Checkout finalCheckout = checkout;
                    Response.getRetrofitResponse(call, new AsyncResponse() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void finalOutput(@NonNull Object output, @NonNull boolean error) {
                            Log.d("getDohaWholeSale", "" + output);
                            if (error) {
                                System.out.println("getDohaWholeSaleDiscount" + output.toString());
                                try {

                                    getDohaCheckoutDetails();
                                    JSONObject cart_discount = null;
                                    JSONObject free_quantity = null;
                                    JSONObject discount_price = null;
                                    JSONObject object = new JSONObject(output.toString());
                                    if (object.get("cart_discount") instanceof JSONObject) {
                                        cart_discount = (object.getJSONObject("cart_discount"));
                                    }
                                    if (object.get("free_quantity") instanceof JSONObject) {
                                        free_quantity = (object.getJSONObject("free_quantity"));
                                    }
                                    if (object.get("discount_price") instanceof JSONObject) {
                                        discount_price = (object.getJSONObject("discount_price"));
                                    }

                                    /****************************************** Cart Prepration **********************************************************/
                                    Iterator<Storefront.CheckoutLineItemEdge> iterator = lineedges.iterator();
                                    View cart_item = null;
                                    TextView pro_id = null;
                                    TextView var_id = null;
                                    TextView productname = null;
                                    TextView available_stock = null;
                                    TextView price = null;
                                    TextView special_price = null;
                                    TextView variantoptions = null;
                                    TextView cart_discount_view = null;
                                    EditText quantity = null;
                                    View outofstockback = null;
                                    ImageView productimage = null;
                                    ImageView increase = null;
                                    ImageView decrese = null;
                                    RelativeLayout deletesection = null;
                                    Button updatequantity = null;


                                    while (iterator.hasNext()) {

                                        cart_item = View.inflate(CartListing.this, R.layout.cart_item, null);
                                        pro_id = cart_item.findViewById(R.id.pro_id);
                                        var_id = cart_item.findViewById(R.id.var_id);
                                        productname = cart_item.findViewById(R.id.productname);
                                        productimage = cart_item.findViewById(R.id.productimage);
                                        outofstockback = cart_item.findViewById(R.id.outofstockback);
                                        available_stock = cart_item.findViewById(R.id.available_stock);
                                        special_price = cart_item.findViewById(R.id.special_price);
                                        price = cart_item.findViewById(R.id.price);
                                        variantoptions = cart_item.findViewById(R.id.options);
                                        cart_discount_view = cart_item.findViewById(R.id.cart_discount);
                                        quantity = cart_item.findViewById(R.id.quantity);
                                        increase = cart_item.findViewById(R.id.increase);
                                        decrese = cart_item.findViewById(R.id.decrese);
                                        deletesection = cart_item.findViewById(R.id.deletesection);
                                        updatequantity = cart_item.findViewById(R.id.updatequantity);
                                        edge[0] = iterator.next();
                                        pro_id.setText(edge[0].getNode().getId().toString());
                                        productname.setText(edge[0].getNode().getTitle());
                                        quantity.setText(edge[0].getNode().getQuantity().toString());
                                        LinearLayout.LayoutParams llp = null;
                                        if (!(edge[0].getNode().getVariant().getAvailableForSale())) {
                                            outofstockback.setVisibility(View.VISIBLE);
                                            available_stock.setVisibility(View.VISIBLE);
                                        }

                                        Log.i("ResponseErrorcurr", "cart " + data.getMoneyFormat());

                                        String price_normal = CurrencyFormatter.setsymbol(edge[0].getNode().getVariant().getPrice(), data.getMoneyFormat());
                                        price.setText(price_normal);
                                        var_id.setText(edge[0].getNode().getVariant().getId().toString());
                                        if (edge[0].getNode().getVariant().getCompareAtPrice() != null) {
                                            if (edge[0].getNode().getVariant().getCompareAtPrice().compareTo(edge[0].getNode().getVariant().getPrice()) == 1) {
                                                String price_special = CurrencyFormatter.setsymbol(edge[0].getNode().getVariant().getCompareAtPrice(), data.getMoneyFormat());
                                                price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                                special_price.setText(price_normal);
                                                price.setText(price_special);
                                            } else {
                                                special_price.setVisibility(View.GONE);
                                            }
                                        } else {
                                            special_price.setVisibility(View.GONE);
                                        }
                                        String url = "";
                                        if (edge[0].getNode().getVariant().getImage() != null) {
                                            url = edge[0].getNode().getVariant().getImage().getTransformedSrc();
                                            Log.i("url", "" + url);
                                        }
                                        Glide.with(CartListing.this)
                                                .load(url)
                                                .thumbnail(0.5f)
                                                .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder).dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL))
                                                .into(productimage);

                                        String id = MainActivity.getBase64Decode(edge[0].getNode().getVariant().getId().toString());

                                        if (cart_discount != null) {

                                            if (cart_discount.has(id)) {
                                                cart_discount_view.setText(Html.fromHtml(cart_discount.getString(id)));
                                            } else {
                                                cart_discount_view.setVisibility(View.GONE);
                                            }
                                        }

                                        if (free_quantity != null) {
                                            if (free_quantity.has(id)) {
                                                String qty = free_quantity.getString(id);
                                                bindFreeGifts(edge[0].getNode().getId().toString(), edge[0].getNode().getId().toString(), edge[0].getNode().getTitle(), url, qty);
                                            }
                                        }

                                        if (discount_price != null) {
                                            if (discount_price.has(id)) {

                                                double pricenormal = Double.parseDouble(price_normal.replace("QAR", ""));
                                                double discountedprice = pricenormal - Double.parseDouble(discount_price.getString(id));
                                                Log.d("discount", "" + discountedprice);

                                                price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                                special_price.setText("QAR" + discountedprice);
                                                price.setText(price_normal);
                                                price.setTextColor(getResources().getColor(R.color.black));
                                                special_price.setTextColor(getResources().getColor(R.color.black));
                                                price.setVisibility(View.VISIBLE);
                                                special_price.setVisibility(View.VISIBLE);

                                                String discount_per_item = discount_price.getString(id);
                                                calculateDiscount(discount_per_item, edge[0].getNode().getQuantity().toString());
                                            }
                                        }


                                        List<Storefront.SelectedOption> options = edge[0].getNode().getVariant().getSelectedOptions();
                                        Iterator<Storefront.SelectedOption> optionIterator = options.iterator();
                                        Storefront.SelectedOption option = null;
                                        String optiondata = "";
                                        while (optionIterator.hasNext()) {
                                            option = optionIterator.next();
                                            optiondata = optiondata + option.getName() + " : " + option.getValue() + "\n";
                                        }
                                        variantoptions.setText(optiondata);
                                        EditText finalQuantity = quantity;
                                        TextView finalVar_id = var_id;

                                     /* quantity.addTextChangedListener(new TextWatcher() {
                                          @Override
                                          public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                          }

                                          @Override
                                          public void onTextChanged(CharSequence s, int start, int before, int count) {

                                          }

                                          @Override
                                          public void afterTextChanged(Editable s) {
                                              if (!finalQuantity.getText().toString().equalsIgnoreCase(""))
                                              {
                                                  final Handler handler = new Handler(Looper.getMainLooper());
                                                  handler.postDelayed(new Runnable() {
                                                      @Override
                                                      public void run() {
                                                          int qty = Integer.parseInt(finalQuantity.getText().toString());
                                                          updatecheckout(finalVar_id.getText().toString(),qty);                                                      }
                                                  }, 1000);

                                              }
                                          }
                                      });*/

                                        quantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                            @Override
                                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                                if ((event != null && (event.getKeyCode() == KeyEvent.ACTION_DOWN)) || (actionId == EditorInfo.IME_ACTION_SEND)) {
                                                    if (!finalQuantity.getText().toString().equalsIgnoreCase("")) {
                                                        int qty = Integer.parseInt(finalQuantity.getText().toString());
                                                        updatecheckout(finalVar_id.getText().toString(), qty);
                                                    } else {
                                                        Toast.makeText(CartListing.this, "Please enter quantity!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                return false;
                                            }
                                        });

                                        updatequantity.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (!finalQuantity.getText().toString().equalsIgnoreCase("")) {
                                                    int qty = Integer.parseInt(finalQuantity.getText().toString());
                                                    updatecheckout(finalVar_id.getText().toString(), qty);
                                                } else {
                                                    Toast.makeText(CartListing.this, "Please enter quantity!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                        increase.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                int qty = Integer.parseInt(finalQuantity.getText().toString());
                                                updatecheckout(finalVar_id.getText().toString(), qty + 1);
                                            }
                                        });
                                        decrese.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                int qty = Integer.parseInt(finalQuantity.getText().toString());
                                                if (qty > 1) {
                                                    updatecheckout(finalVar_id.getText().toString(), qty - 1);
                                                }
                                            }
                                        });
                                        deletesection.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                updatecheckout(finalVar_id.getText().toString(), -1);
                                            }
                                        });

                                        llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        llp.setMargins(4, 2, 4, 2);
                                        cart_item.setLayoutParams(llp);
                                        cartcontainer.addView(cart_item);
                                    }

                                    Double discountedPrice = finalCheckout.getSubtotalPrice().doubleValue() - discount;
                                    BigDecimal numBigDecimal = BigDecimal.valueOf(discountedPrice);
                                    String subtotal = CurrencyFormatter.setsymbol(finalCheckout.getSubtotalPrice(), data.getMoneyFormat());
                                    String subbtotal = data.getMoneyFormat() + " " + numBigDecimal.doubleValue();
                                    subtotalprice.setText(subbtotal.substring(0, subbtotal.indexOf(".") + 2));
                                    if (finalCheckout.getTaxExempt()) {
                                        taxsection.setVisibility(View.VISIBLE);
                                        String tax = CurrencyFormatter.setsymbol(finalCheckout.getTotalTax(), data.getMoneyFormat());
                                        taxprice.setText(tax);
                                    }
                                    String grandtotal = CurrencyFormatter.setsymbol(finalCheckout.getTotalPrice(), data.getMoneyFormat());
                                    grandtotalprice.setText(subbtotal.substring(0, subbtotal.indexOf(".") + 2));
                                    // grandtotalprice.setText(subbtotal);
                                    relativemain.setVisibility(View.VISIBLE);
                                    loader.dismiss();
                                    /****************************************** Cart Prepration **********************************************************/

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, CartListing.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(CartListing.this, getResources().getString(R.string.emptycart), Toast.LENGTH_LONG).show();
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDohaCheckoutDetails() {
        /******************************************** Checkout *******************************************************************/
        apiService = ApiClient.getDohaWholesaleClient(CartListing.this).create(ApiInterface.class);

        Call<ResponseBody> call1 = apiService.get_checkout_mobile("1", arr_id, arr_quantity);
        try {

            Response.getRetrofitResponse(call1, new AsyncResponse() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void finalOutput(@NonNull Object output, @NonNull boolean error) {
                    Log.d("getDohaWholeSale", "" + output);
                    if (error) {
                        System.out.println("getDohaWholeSaleDiscount" + output.toString());
                        checkouturl = output.toString();
                    }
                }
            }, CartListing.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /******************************************** Checkout *******************************************************************/
    }

    private void calculateDiscount(String discount_per_item, String qunatity) {
        Double discountperproduct = Double.parseDouble(discount_per_item);
        Double qty = Double.parseDouble(qunatity);
        discount += discountperproduct * qty;
        Log.d("discount", "" + discount);
    }

    private void bindFreeGifts(String prodId, String varId, String title, String url, String qty) {
        TextView pro_id = null;
        TextView var_id = null;
        TextView productname = null;
        TextView available_stock = null;
        TextView price = null;
        TextView special_price = null;
        TextView variantoptions = null;
        TextView cart_discount_view = null;
        EditText quantity = null;
        View outofstockback = null;
        ImageView productimage = null;
        ImageView increase = null;
        ImageView decrese = null;
        RelativeLayout deletesection = null;
        LinearLayout.LayoutParams llp = null;
        View cart_item = View.inflate(CartListing.this, R.layout.cart_item, null);

        pro_id = cart_item.findViewById(R.id.pro_id);
        var_id = cart_item.findViewById(R.id.var_id);
        productname = cart_item.findViewById(R.id.productname);
        productimage = cart_item.findViewById(R.id.productimage);
        deletesection = cart_item.findViewById(R.id.deletesection);
        special_price = cart_item.findViewById(R.id.special_price);
        price = cart_item.findViewById(R.id.price);
        variantoptions = cart_item.findViewById(R.id.options);
        quantity = cart_item.findViewById(R.id.quantity);


        pro_id.setText(prodId);
        var_id.setText(varId);
        Glide.with(CartListing.this)
                .load(url)
                .thumbnail(0.5f)
                .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder).dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(productimage);
        variantoptions.setText(R.string.free_gift);
        productname.setText(title);
        quantity.setText(qty);
        quantity.setEnabled(false);
        special_price.setVisibility(View.GONE);
        deletesection.setVisibility(View.GONE);
        price.setVisibility(View.GONE);
        llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        llp.setMargins(4, 2, 4, 2);
        cart_item.setLayoutParams(llp);
        giftcontainer.addView(cart_item);

        gift_header.setVisibility(View.VISIBLE);
        giftcontainer.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        invalidateOptionsMenu();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        return false;
    }

    public void updatecheckout(String variant_id, int qty) {
        try {
            hideKeyboard(CartListing.this);
            JSONObject object = new JSONObject(data.getLineItems());
            if (object.has(variant_id)) {
                if (qty == -1) {
                    object.remove(variant_id);
                } else {
                    object.put(variant_id, qty);
                }
                isfromqtycheck = true;
                data.saveLineItems(object);
             /* if(data.getCoupon()==null)
              {
                  checkout_id="noid";
                  Log.i("checkout_id",""+checkout_id);
              }*/
                arr_quantity = "";
                arr_id = "";
                discount = 0.0d;
                gift_header.setVisibility(View.GONE);
                giftcontainer.removeAllViews();
                getCartData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPopUp() {
        try {
            final Dialog listDialog = new Dialog(CartListing.this, R.style.PauseDialog);
            ((ViewGroup) ((ViewGroup) Objects.requireNonNull(listDialog.getWindow()).getDecorView()).getChildAt(0))
                    .getChildAt(1)
                    .setBackgroundColor(CartListing.this.getResources().getColor(R.color.black));
            listDialog.setTitle(Html.fromHtml("<font color='#ffffff'>" + getResources().getString(R.string.logintype) + "</font>"));
            LayoutInflater li = (LayoutInflater) CartListing.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View loginoptions = Objects.requireNonNull(li).inflate(R.layout.magenative_custom_options, null, false);
            RadioButton Guest = loginoptions.findViewById(R.id.Guest);
            RadioButton User = loginoptions.findViewById(R.id.User);
            int id = Resources.getSystem().getIdentifier("btn_check_holo_light", "drawable", "android");
            Guest.setButtonDrawable(id);
            User.setButtonDrawable(id);
            Guest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        listDialog.dismiss();
                        Intent intent = new Intent(CartListing.this, CheckoutWeblink.class);
                        intent.putExtra("link", checkouturl);
                        startActivity(intent);
                        overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                    }
                }
            });
            User.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        listDialog.dismiss();
                        Intent intent = new Intent(CartListing.this, Login.class);
                        intent.putExtra("link", checkouturl);
                        intent.putExtra("checkout", "checkout");
                        startActivity(intent);
                        overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                    }
                }
            });
            listDialog.setContentView(loginoptions);
            listDialog.setCancelable(true);
            listDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /************************************************ Delivery Date & Time ***************************************************************/
    private void loadTime(String date) {
        try {


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showToDatePickerDialog(View v) {
        Bundle bundle = new Bundle();
        bundle.putInt("max_date", max_date);
        bundle.putString("invalid_day", invalid_day);
        DialogFragment newFragment = new CartListing.ToDatePickerFragment();
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    public static class ToDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        int max_date;
        String invalid_day;
        Context mContext;

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            mContext = context;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            //Locale locale = getResources().getConfiguration().locale;

            final Calendar currentcal = Calendar.getInstance();
            int currentyear = currentcal.get(Calendar.YEAR);
            int currentmonth = currentcal.get(Calendar.MONTH);
            int currentday = currentcal.get(Calendar.DAY_OF_MONTH);
            currentcal.add(Calendar.DAY_OF_MONTH, 1);
            String currentdate = currentday + "/" + (currentmonth + 1) + "/" + currentyear;

            Bundle bundle = this.getArguments();
            if (bundle != null) {
                max_date = bundle.getInt("max_date", 0);
                invalid_day = bundle.getString("invalid_day", "");
            }

            String getfromdate = currentdate.trim();
            String[] getfrom = getfromdate.split("/");

            int year, month, day;
            year = Integer.parseInt(getfrom[2]);

            int temp = Integer.parseInt(getfrom[1]);
            month = temp - 1;
            day = Integer.parseInt(getfrom[0]);

            final Calendar c = Calendar.getInstance();
            c.set(year, month, day + 1);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);

            long now = System.currentTimeMillis() - 1000;
            datePickerDialog.getDatePicker().setMinDate(now);
            // datePickerDialog.getDatePicker().setMaxDate(now + (1000 * 60 * 60 * 24 * max_date)); //After max_date Days from Now

            return datePickerDialog;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void onDateSet(DatePicker view, int year, int month, int day) {
            try {
                month = month + 1;
                Log.i("currentmonthdate", "month " + day + "/" + month + "/" + year);

                try {
                    attributesobject.put("Delivery Date", day + "/" + month + "/" + year);
                    deliveryDate = day + "/" + month + "/" + year;
                    //storepickup_calendarslots.setText(sweekdayname+", "+sdate+" "+smonthname+", "+syear);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                String[] monthNameArray = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                SimpleDateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date myDate = null;
                SimpleDateFormat simpleDateFormat = null;
                try {
                    myDate = inFormat.parse(day + "-" + month + "-" + year);
                    simpleDateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                    sweekdayname = simpleDateFormat.format(myDate).substring(0, 3);
                    String invalidday = "";

                    switch (invalid_day) {
                        case "Sunday":
                            invalidday = "Sun";
                            attributesobject.put("Delivery Day", "Sunday");
                            break;
                        case "Monday":
                            invalidday = "Mon";
                            attributesobject.put("Delivery Day", "Monday");
                            break;
                        case "Tuesday":
                            invalidday = "Tue";
                            attributesobject.put("Delivery Day", "Tuesday");
                            break;
                        case "Wednesday":
                            invalidday = "Wed";
                            attributesobject.put("Delivery Day", "Wednesday");
                            break;
                        case "Thrusday":
                            invalidday = "Thu";
                            attributesobject.put("Delivery Day", "Thrusday");
                            break;
                        case "Friday":
                            invalidday = "Fri";
                            attributesobject.put("Delivery Day", "Friday");
                            break;
                        case "Saturday":
                            invalidday = "Sat";
                            attributesobject.put("Delivery Day", "Saturday");
                            break;
                    }

                    if (sweekdayname.equals(invalidday)) {
                        showpop();
                        attributesobject.remove("Delivery Day");
                        storepickup_calendarslots.setText(getResources().getString(R.string.choosedatetime));
                        storepickup_slotssection.setVisibility(View.GONE);
                    } else {
                        smonthname = monthNameArray[month - 1];
                        syear = String.valueOf(year);
                        sdate = String.valueOf(day);
                        String selected_date = year + "-" + month + "-" + day;
                        storepickup_calendarslots.setText(sweekdayname + ", " + smonthname + " " + sdate + ", " + syear);
                    }


                    /************************************************* Slots *******************************************************************/
                    Call<ResponseBody> c = apiServiceOneTimeDelivery1.getslots(getResources().getString(R.string.mid), storepickup_calendarslots.getText().toString(), "2");
                    Response.getRetrofitResponse(c, new AsyncResponse() {
                        @Override
                        public void finalOutput(Object output, boolean error) throws Exception {
                            if (error) {
                                try {
                                    JSONObject jsonObject = new JSONObject(output.toString());
                                    JSONArray slots = null;
                                    ArrayList array_display_slots = new ArrayList();
                                    if (jsonObject.getBoolean("success")) {
                                        slots = jsonObject.getJSONArray("slots");
                                        Log.d("Slotsssss", "" + slots);
                                        for (int i = 0; i < slots.length(); i++) {
                                            array_display_slots.add(slots.getString(i));
                                        }
                                    }
                                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, R.layout.text, array_display_slots);
                                    time_storepickup_slots.setAdapter(dataAdapter);
                                    time_storepickup_slots.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            try {
                                                attributesobject.put("Delivery Time", time_storepickup_slots.getSelectedItem());
                                                deliveryTime = time_storepickup_slots.getSelectedItem().toString();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });
                                    storepickup_slotssection.setVisibility(View.VISIBLE);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, getContext());
                    /************************************************* Slots *******************************************************************/

                } catch (ParseException e) {
                    e.printStackTrace();
                }


                //sendRequestDeliveryDate(getResources().getString(R.string.shopdomain),deliverypostalcode.getText().toString(),selected_date,location_id);
                //sendRequestPickupDate(postal_code,selected_date,location_id,simpleDateFormat.format(myDate));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void showpop() {
            try {
                final Dialog listDialog = new Dialog(getActivity(), R.style.PauseDialog);
                ((ViewGroup) ((ViewGroup) Objects.requireNonNull(listDialog.getWindow()).getDecorView()).getChildAt(0))
                        .getChildAt(1)
                        .setBackgroundColor(getActivity().getResources().getColor(R.color.red));
                listDialog.setTitle(Html.fromHtml("<font color='#ffffff'>" + getResources().getString(R.string.logintypes) + "</font>"));
                LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                @SuppressLint("InflateParams") View loginoptions = Objects.requireNonNull(li).inflate(R.layout.magenative_custom_optionss, null, false);
                int id = Resources.getSystem().getIdentifier("btn_check_holo_light", "drawable", "android");
                listDialog.setContentView(loginoptions);
                listDialog.setCancelable(true);
                listDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    /************************************************ Delivery Date & Time ***************************************************************/


}