package com.goodwill.wholesale.maincontainer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.checkoutsection.CartListing;
import com.goodwill.wholesale.checkoutsection.CheckoutLineItems;
import com.goodwill.wholesale.checkoutsection.CheckoutWeblink;
import com.goodwill.wholesale.homesection.HomePage;
import com.goodwill.wholesale.langauagesection.LoadLanguage;
import com.goodwill.wholesale.loginandregistrationsection.Login;
import com.goodwill.wholesale.requestsection.ApiClient;
import com.goodwill.wholesale.requestsection.ApiInterface;
import com.goodwill.wholesale.searchsection.AutoSearch;
import com.goodwill.wholesale.splashsection.Splash;
import com.goodwill.wholesale.storagesection.LocalData;
import com.goodwill.wholesale.storefrontqueries.MutationQuery;
import com.goodwill.wholesale.storefrontqueries.Query;
import com.goodwill.wholesale.storefrontresponse.AsyncResponse;
import com.goodwill.wholesale.storefrontresponse.Response;
import com.goodwill.wholesale.wishlistsection.WishListing;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shopify.buy3.GraphCallResult;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.MutationGraphCall;
import com.shopify.buy3.QueryGraphCall;
import com.shopify.buy3.Storefront;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {
    public static ArrayList<String> productTags;
    public static HashMap<String, String> mapVariantsIds = null;
    public static HashMap<String, String> cart_discount = null;
    public static HashMap<String, HashMap<String, String>> mapVariantsPrices = null;
    public static String count = "0";
    @Nullable
    @BindView(R.id.toolimage)
    ImageView toolimage;
    @Nullable
    @BindView(R.id.tooltext)
    TextView tooltext;
    @Nullable
    @BindView(R.id.MageNative_toolbar)
    Toolbar mToolbar;
    @Nullable
    @BindView(R.id.MageNative_drawer_layout)
    DrawerLayout drawerLayout;
    LocalData localData = null;
    GraphClient apiclient = null;
    ApiInterface apiService;
    CheckoutLineItems items = null;
    @Nullable
    private Dialog langlistDialog = null;

    public static String getBase64Decode(String id) {
        byte[] data = Base64.decode(id, Base64.DEFAULT);
        String text = new String(data, StandardCharsets.UTF_8);
        String[] datavalue = text.split("/");
        String valueid = datavalue[datavalue.length - 1];
        String[] datavalue2 = valueid.split("key");
        text = datavalue2[0];
        Log.i("getBase64Decode", "" + text);
        return text;
    }

    public static void mergeJSONObjects(JSONObject json) {
        try {
            Iterator<?> keys = json.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                String value = json.getString(key);
                cart_discount.put(key, value);
            }
            Log.d("cart_discount", "" + cart_discount);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(MainActivity.this);
        localData = new LocalData(MainActivity.this);
        LoadLanguage.setLocale(localData.getLangCode(), MainActivity.this);
        apiclient = ApiClient.getGraphClient(MainActivity.this, true);
        langlistDialog = new Dialog(MainActivity.this, R.style.PauseDialog);
        items = new CheckoutLineItems(MainActivity.this);
        getMoneyFormat();
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getAttributes().flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setSupportActionBar(mToolbar);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.white));
        Log.d("checkkkkkk", "" + localData.getAlreadyLogin());

        //sendRequest();

        if (localData.isLogin()) {
            if (!localData.getAlreadyLogin().isEmpty()) {
                Log.d("getAlreadyLogin","If "+localData.getAlreadyLogin());
                Gson gson = new Gson();
                productTags = new ArrayList<>();
                String json = localData.getAlreadyLogin();
                Type type = new TypeToken<List<String>>() {
                }.getType();
                List<String> arrPackageData = gson.fromJson(json, type);
                for (String data : arrPackageData) {
                    productTags.add(data);
                }
            }
            else {
                Log.d("getAlreadyLogin","Else");
                sendRequest();
            }

        } else {
            productTags = new ArrayList<>();
            productTags.add("guest");
            productTags.add("guests");
        }
        Log.d("ProductTags MainAct 1", productTags + "");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Glide.with(MainActivity.this)
                            .asBitmap()
                            .load(localData.getHeaderLogo())
                            .into(toolimage);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //toolimage.setImageResource(R.drawable.logo);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        FragmentDrawer drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.MageNative_fragment_navigation_drawer);
        drawerFragment.setUp(R.id.MageNative_fragment_navigation_drawer, drawerLayout, mToolbar);
    }

    private void sendRequest() {
        productTags = new ArrayList<>();
        Log.d("CustomerId", "" + localData.getCustomerId());
        apiService = ApiClient.getClientTags(MainActivity.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.getcustomerdata("tags,id", getBase64Decode(localData.getCustomerId()), getResources().getString(R.string.mid));
        com.goodwill.wholesale.storefrontresponse.Response.getRetrofitResponse(call, new AsyncResponse() {
            @Override
            public void finalOutput(@NonNull Object output, @NonNull boolean error) {
                if (error) {
                    //localData.saveBackgroundImages(output.toString());
                    try {
                        JSONObject jsonObject = new JSONObject(output.toString());
                        JSONObject data;
                        if (jsonObject.getBoolean("success")) {
                            data = jsonObject.getJSONObject("data");
                            String tags = data.getString("tags");
                            if (!tags.isEmpty()) {
                                if (tags.contains(",")) {
                                    String[] arrayTags = tags.split(",");
                                    for (int i = 0; i < arrayTags.length; i++) {
                                        Log.d("vaibhavtagss",""+arrayTags[i]);
                                        productTags.add(arrayTags[i]);
                                    }
                                } else {
                                    productTags.add(tags);
                                }
                                Gson gson = new Gson();
                                String json = gson.toJson(productTags);
                                localData.setAlreadyLogin(json);
                            } else {
                                productTags.add("guest");
                                productTags.add("guests");
                            }
                        }

                        Log.d("ProductTags MainAct 2", productTags + "");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, MainActivity.this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        changecount();
    }

    protected void showbackbutton() {
        Objects.requireNonNull(FragmentDrawer.mDrawerToggle).setDrawerIndicatorEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(FragmentDrawer.mDrawerToggle).setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void showhumburger() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        FragmentDrawer.mDrawerToggle.setDrawerIndicatorEnabled(true);
        FragmentDrawer.mDrawerToggle.setToolbarNavigationClickListener(null);
    }

    protected void showTittle(String tittle) {
        Objects.requireNonNull(tooltext).setVisibility(View.VISIBLE);
        Objects.requireNonNull(toolimage).setVisibility(View.GONE);
        tooltext.setText(tittle);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

    }

    public void getMoneyFormat() {
        try {
            if (localData.getMoneyFormat() == null) {
                QueryGraphCall call = apiclient.queryGraph(Query.getShopDetails());
                Response.getGraphQLResponse(call, new AsyncResponse() {
                    @Override
                    public void finalOutput(@NonNull Object output, @NonNull boolean error) {
                        if (error) {
                            GraphResponse<Storefront.QueryRoot> response = ((GraphCallResult.Success<Storefront.QueryRoot>) output).getResponse();
                            Log.i("ResponseErrorcurr", "" + response.getData().getShop().getPaymentSettings().getCurrencyCode().toString());
                            localData.saveMoneyFormat(response.getData().getShop().getPaymentSettings().getCurrencyCode().toString());
                            localData.savePrivacyPolicy(response.getData().getShop().getPrivacyPolicy().getUrl(), response.getData().getShop().getPrivacyPolicy().getTitle());
                            localData.saveRefundPolicy(response.getData().getShop().getRefundPolicy().getUrl(), response.getData().getShop().getRefundPolicy().getTitle());
                            localData.saveTerms(response.getData().getShop().getTermsOfService().getUrl(), response.getData().getShop().getTermsOfService().getTitle());
                        } else {
                            Log.i("ResponseError", "" + output.toString());
                        }
                    }
                }, MainActivity.this);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_homepage, menu);
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

    public void changecount() {
        if (localData.getLineItems() != null) {
            count = String.valueOf(items.getItemcounts());
        } else {
            count = "0";
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.wishlist:

                Intent intent = new Intent(MainActivity.this, WishListing.class);
                startActivity(intent);
                overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                return true;
            case R.id.MageNative_action_cart:
                Intent cart = new Intent(MainActivity.this, CartListing.class);
                startActivity(cart);
                overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                return true;
            case R.id.MageNative_action_search:
                Intent searchintent = new Intent(MainActivity.this, AutoSearch.class);
                startActivity(searchintent);
                overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void LoginUser(String username, String password, String origin, String weburl) {
        try {
            MutationGraphCall call = apiclient.mutateGraph(MutationQuery.createCustomerAccessToken(username, password));
            Response.getMutationGraphQLResponse(call, new AsyncResponse() {
                @Override
                public void finalOutput(@NonNull Object output, @NonNull boolean error) {
                    if (error) {
                        GraphResponse<Storefront.Mutation> response = ((GraphCallResult.Success<Storefront.Mutation>) output).getResponse();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<Storefront.UserError> errors = response.getData().getCustomerAccessTokenCreate().getUserErrors();
                                if (errors.size() > 0) {
                                    Iterator iterator = errors.iterator();
                                    String err = "";
                                    while (iterator.hasNext()) {
                                        Storefront.UserError error = (Storefront.UserError) iterator.next();
                                        err += error.getMessage();
                                    }
                                    Toast.makeText(MainActivity.this, err, Toast.LENGTH_LONG).show();
                                } else {
                                    processLogin(response, username, password, origin, weburl);
                                    Login.getInstance().finish();
                                }
                            }
                        });
                    } else {
                        Log.i("ResponseError", "" + output.toString());
                    }
                }
            }, MainActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processLogin(GraphResponse<Storefront.Mutation> response, String username, String password, String origin, String weburl) {
        try {
            Storefront.CustomerAccessToken token = response.getData().getCustomerAccessTokenCreate().getCustomerAccessToken();
            localData.createSession(username, password);
            Log.i("ExpireAt", "" + token.getExpiresAt().toLocalDateTime().toString());
            localData.saveAccesstokenWithExpiry(token.getAccessToken(), token.getExpiresAt().toLocalDateTime().toString());
            Toast.makeText(MainActivity.this, getResources().getString(R.string.successfullogin), Toast.LENGTH_LONG).show();
            if (localData.getFirstName() == null) {
                QueryGraphCall call = apiclient.queryGraph(Query.getCustomerDetails(token.getAccessToken()));
                Response.getGraphQLResponse(call, new AsyncResponse() {
                    @Override
                    public void finalOutput(@NonNull Object output, @NonNull boolean error) {
                        if (error) {
                            GraphResponse<Storefront.QueryRoot> response = ((GraphCallResult.Success<Storefront.QueryRoot>) output).getResponse();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    process(response, origin, weburl);
                                }
                            });
                        } else {
                            Log.i("ResponseError", "" + output.toString());
                        }
                    }
                }, MainActivity.this);
            } else {
                if (origin.equals("normal")) {
                    Intent intent = new Intent(MainActivity.this, HomePage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(MainActivity.this, CheckoutWeblink.class);
                    intent.putExtra("link", weburl);
                    startActivity(intent);
                    overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void process(GraphResponse<Storefront.QueryRoot> response, String origin, String weburl) {
        try {
            localData.saveFirstName(response.getData().getCustomer().getFirstName());
            localData.saveLastName(response.getData().getCustomer().getLastName());
            localData.saveCustomerId(response.getData().getCustomer().getId().toString());
            localData.savephone(response.getData().getCustomer().getPhone());
            Log.d("Customer_id", response.getData().getCustomer().getId().toString());

            if (origin.equals("normal")) {
                Intent intent = new Intent(MainActivity.this, HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(MainActivity.this, CheckoutWeblink.class);
                intent.putExtra("link", weburl);
                startActivity(intent);
                overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getCountryList() {
        ArrayList<String> list = new ArrayList<String>();
        try {
            JSONArray array = new JSONObject(AssetJSONFile("country.json", MainActivity.this)).names();
            try {
                for (int i = 0, l = array.length(); i < l; i++) {
                    list.add(array.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<String> getStateList(String country) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            JSONObject object = new JSONObject(AssetJSONFile("country.json", MainActivity.this));
            try {
                JSONArray array = object.getJSONArray(country);
                if (array.length() > 0) {
                    for (int i = 0, l = array.length(); i < l; i++) {
                        list.add(array.getString(i));
                    }
                    return list;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String AssetJSONFile(String filename, Context context) {
        byte[] formArray = new byte[0];
        try {
            AssetManager manager = context.getAssets();
            InputStream file = manager.open(filename);
            formArray = new byte[file.available()];
            file.read(formArray);
            file.close();
            return new String(formArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(formArray);
    }

    public void showlangpop(@NonNull JSONArray languages) {
        try {
            ((ViewGroup) ((ViewGroup) Objects.requireNonNull(Objects.requireNonNull(langlistDialog).getWindow()).getDecorView()).getChildAt(0))
                    .getChildAt(1)
                    .setBackgroundColor(getResources().getColor(R.color.black));
            Objects.requireNonNull(langlistDialog).setTitle(Html.fromHtml("<center><font color='#ffffff'>" + getResources().getString(R.string.lang) + "</font></center>"));
            LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View v = Objects.requireNonNull(li).inflate(R.layout.langlist, null, false);
            RadioGroup storelist = v.findViewById(R.id.langlist);
            JSONObject object;
            RadioButton button;
            for (int i = 0; i < languages.length(); i++) {
                object = languages.getJSONObject(i);
                button = new RadioButton(MainActivity.this);
                button.setText(object.getString("name"));
                button.setTag(object.getString("code"));
                if (localData.getLangCode().equals(object.getString("code"))) {
                    button.setChecked(true);
                }
                final RadioButton finalButton = button;
                button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            localData.saveLangCode(String.valueOf(finalButton.getTag()));
                            LoadLanguage.setLocale(localData.getLangCode(), MainActivity.this);
                            Intent intent = new Intent(MainActivity.this, Splash.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            langlistDialog.dismiss();
                        }
                    }
                });
                storelist.addView(button);
            }
            langlistDialog.setContentView(v);
            langlistDialog.setCancelable(true);
            langlistDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
