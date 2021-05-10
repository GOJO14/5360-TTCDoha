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
package com.goodwill.wholesale.homesection;

import android.annotation.SuppressLint;
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

import com.goodwill.wholesale.R;
import com.goodwill.wholesale.maincontainer.MainActivity;
import com.goodwill.wholesale.storagesection.LocalData;

import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressWarnings("ALL")
public class HomeWeblink extends MainActivity
{
    LocalData data=null;
    String postData = null;
    @Nullable @BindView(R.id.MageNative_webview) WebView webView;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ViewGroup content = findViewById(R.id.MageNative_frame_container);
        getLayoutInflater().inflate(R.layout.magenative_webpage, content, true);
        ButterKnife.bind(HomeWeblink.this);
        showbackbutton();
        data=new LocalData(HomeWeblink.this);
       // showTittle(getResources().getString(R.string.checkout));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String url = getIntent().getStringExtra("link");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        setUpWebViewDefaults(webView);
        webView.loadUrl(url);
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
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
            {
                super.onReceivedSslError(view, handler, error);
                Log.i("URL", "" + error.getUrl());
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu)
    {
        return false;
    }

}
