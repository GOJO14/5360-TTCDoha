package com.goodwill.wholesale.applicationpackage;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shopify.buy3.GraphClient;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.requestsection.ApiClient;
import com.goodwill.wholesale.requestsection.ApiInterface;
import com.goodwill.wholesale.splashsection.Splash;
import com.goodwill.wholesale.storagesection.LocalData;
import com.goodwill.wholesale.storefrontresponse.AsyncResponse;
import com.squareup.okhttp.ResponseBody;

import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import org.acra.ACRA;
import org.acra.ReportField;


@ReportsCrashes(formKey = "", // will not be used
        mailTo = "abhishekdubey@magenative.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)
public class ShopifyApplication extends MultiDexApplication {
    private static ShopifyApplication mInstance;
    public static synchronized ShopifyApplication getInstance() {
        return mInstance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        ACRA.init(this);
        mInstance = this;


    }
}