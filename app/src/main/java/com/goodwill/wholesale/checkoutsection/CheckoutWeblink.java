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
package com.goodwill.wholesale.checkoutsection;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.goodwill.wholesale.R;
import com.goodwill.wholesale.homesection.HomePage;
import com.goodwill.wholesale.maincontainer.MainActivity;
import com.goodwill.wholesale.requestsection.ApiClient;
import com.goodwill.wholesale.requestsection.ApiInterface;
import com.goodwill.wholesale.splashsection.Splash;
import com.goodwill.wholesale.storagesection.LocalData;
import com.goodwill.wholesale.storefrontresponse.AsyncResponse;
import com.goodwill.wholesale.storefrontresponse.Response;

import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;

@SuppressWarnings("ALL")
public class CheckoutWeblink extends MainActivity
{
    LocalData data=null;
    String postData = null;
    ApiInterface apiService;
    String currentUrl;
    @Nullable @BindView(R.id.MageNative_webview) WebView webView;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ViewGroup content = findViewById(R.id.MageNative_frame_container);
        getLayoutInflater().inflate(R.layout.magenative_webpage, content, true);
        ButterKnife.bind(CheckoutWeblink.this);
        showbackbutton();
        apiService = ApiClient.getClient(CheckoutWeblink.this).create(ApiInterface.class);
        data=new LocalData(CheckoutWeblink.this);
        showTittle(getResources().getString(R.string.checkout));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        currentUrl = getIntent().getStringExtra("link");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        setUpWebViewDefaults(webView);
        if(data.isLogin())
        {
            try
            {
                String checkouturl="https://"+getResources().getString(R.string.shopdomain)+"/account/login";
                postData = "checkout_url=" + URLEncoder.encode(currentUrl.replace("https://"+getResources().getString(R.string.shopdomain),""), "UTF-8") +
                        "&form_type=" + URLEncoder.encode("customer_login", "UTF-8")
                        + "&customer[email]=" + URLEncoder.encode(data.getEmail(), "UTF-8")
                        + "&customer[password]=" + URLEncoder.encode(data.getPassword(), "UTF-8");
                webView.postUrl(checkouturl,postData.getBytes());

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            webView.loadUrl(currentUrl);
        }
        webView.setWebChromeClient(new WebChromeClient());
    }
    @SuppressLint("SetJavaScriptEnabled")
    private void setUpWebViewDefaults(WebView webView)
    {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.i("URL", "" + description);
            }
            @Override
            public void onLoadResource(WebView view, @NonNull String url)
            {
                Log.i("URL", "" + url);
                if(url.contains("thank_you"))
                {
                    data.clearCoupon();
                    data.clearCheckoutId();
                    data.clearLineItems();
                    Toast.makeText(CheckoutWeblink.this,getResources().getString(R.string.ordersuccessfulyplaced),Toast.LENGTH_LONG).show();
                    Call<ResponseBody> call=apiService.setOrder(getResources().getString(R.string.mid),CartListing.checkout_id);
                    try
                    {
                        Response.getRetrofitResponse(call,new AsyncResponse()
                        {
                            @Override
                            public void finalOutput(@NonNull Object output, @NonNull boolean error )
                            {

                            }
                        },CheckoutWeblink.this);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    /*Intent intent=new Intent(CheckoutWeblink.this, HomePage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.magenative_slide_in,R.anim.magenative_slide_out);*/
                }
            }
            @Override
            public void onPageFinished(WebView view, String url)
            {
                Log.i("pageURL", "" + url);
                String javascript="javascript: document.getElementsByClassName('section__header')[0].style.display = 'none' ";
                String javascript1="javascript: document.getElementsByClassName('logged-in-customer-information')[0].style.display = 'none' ";
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                {
                    webView.evaluateJavascript(javascript, new ValueCallback<String>()
                    {
                        @Override
                        public void onReceiveValue(String value) {
                            Log.i("pageVALUE1", "" + value);
                        }
                    });
                    webView.evaluateJavascript(javascript1, new ValueCallback<String>()
                    {
                        @Override
                        public void onReceiveValue(String value) {
                            Log.i("pageVALUE1", "" + value);
                        }
                    });
                }
                else
                {
                    webView.loadUrl(javascript);
                    webView.loadUrl(javascript1);
                }

                if(url.contains("thank_you"))
                {
                    data.clearCoupon();
                    data.clearCheckoutId();
                    data.clearLineItems();
                    Toast.makeText(CheckoutWeblink.this,getResources().getString(R.string.ordersuccessfulyplaced),Toast.LENGTH_LONG).show();
                    Call<ResponseBody> call=apiService.setOrder(getResources().getString(R.string.mid),CartListing.checkout_id);
                    try
                    {
                        Response.getRetrofitResponse(call,new AsyncResponse()
                        {
                            @Override
                            public void finalOutput(@NonNull Object output, @NonNull boolean error )
                            {

                            }
                        },CheckoutWeblink.this);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    /*Intent intent=new Intent(CheckoutWeblink.this, HomePage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.magenative_slide_in,R.anim.magenative_slide_out);*/
                }
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
            {
                super.onReceivedSslError(view, handler, error);
                Log.i("URL", "" + error.getUrl());
            }
           /* @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.equals(currentUrl)) {
                    view.loadUrl(url);
                }
                return true;
            }*/
        });
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            Intent intent=new Intent(CheckoutWeblink.this, HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.magenative_slide_in,R.anim.magenative_slide_out);
            finish();
        } else {
            finish();
            super.onBackPressed();

        }
    }
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu)
    {
        return false;
    }

}
