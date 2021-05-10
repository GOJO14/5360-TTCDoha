package com.goodwill.wholesale.requestsection;
import android.content.Context;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.HttpCachePolicy;
import com.goodwill.wholesale.R;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import kotlin.Unit;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient
{
    static WeakReference<Context> contextWeakReference;


    public static Retrofit getCustomAPisClient(Context context)
    {

        String BASE_URL = "https://api.dohawholesale.com/Discount/";
        Retrofit retrofit = null;
        try
        {
            contextWeakReference=new WeakReference<Context>(context);
            int cacheSize = 10 * 1024 * 1024; // 10 MB
            Cache cache = new Cache(contextWeakReference.get().getCacheDir(), cacheSize);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cache(cache)
                    .build();
            if (retrofit==null)
            {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return retrofit;
    }
    public static Retrofit getOnetimeDeliveryClient(Context context)
    {
       // String BASE_URL = "https://shopifymobileapp.cedcommerce.com/shopifymobile/ontimedeliveryappsonrentapi/";
        String BASE_URL = "http://shopifymobileapp.cedcommerce.com/shopifymobile/localdeliveryapi/";

        Retrofit retrofit = null;
        try
        {
            contextWeakReference=new WeakReference<Context>(context);
            int cacheSize = 10 * 1024 * 1024; // 10 MB
            Cache cache = new Cache(contextWeakReference.get().getCacheDir(), cacheSize);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cache(cache)
                    .build();
            if (retrofit==null)
            {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return retrofit;
    }
    public static Retrofit getDohaWholesaleClient(Context context)
    {
        String BASE_URL = "https://api.dohawholesale.com/Discount/";
        Retrofit retrofit = null;
        try
        {
            contextWeakReference=new WeakReference<Context>(context);
            int cacheSize = 10 * 1024 * 1024; // 10 MB
            Cache cache = new Cache(contextWeakReference.get().getCacheDir(), cacheSize);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cache(cache)
                    .build();
            if (retrofit==null)
            {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return retrofit;
    }

    public static Retrofit getClient(Context context)
    {
        String BASE_URL = "https://shopifymobileapp.cedcommerce.com/index.php/shopifymobile/shopifyapi/";
        Retrofit retrofit = null;
        try
        {
            contextWeakReference=new WeakReference<Context>(context);
            int cacheSize = 10 * 1024 * 1024; // 10 MB
            Cache cache = new Cache(contextWeakReference.get().getCacheDir(), cacheSize);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cache(cache)
                    .build();
            if (retrofit==null)
            {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return retrofit;
    }
    public static Retrofit getClientTags(Context context)
    {
        String BASE_URL = "http://shopifymobileapp.cedcommerce.com/shopifymobile/shopifyapi/";
        Retrofit retrofit = null;
        try
        {
            contextWeakReference=new WeakReference<Context>(context);
            int cacheSize = 10 * 1024 * 1024; // 10 MB
            Cache cache = new Cache(contextWeakReference.get().getCacheDir(), cacheSize);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cache(cache)
                    .build();
            if (retrofit==null)
            {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return retrofit;
    }
    public static GraphClient getGraphClient(Context context,boolean invalidate)
    {
        HttpCachePolicy.ExpirePolicy policy;
        if(invalidate)
        {
            policy=HttpCachePolicy.Default.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES);
        }
        else
        {
            policy=HttpCachePolicy.Default.NETWORK_FIRST.expireAfter(5, TimeUnit.MINUTES);
        }
        GraphClient client=null;
        try
        {
            contextWeakReference=new WeakReference<Context>(context);
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder builder = original.newBuilder().method(original.method(), original.body());
                        builder.header("X-Shopify-Storefront-Access-Token", contextWeakReference.get().getResources().getString(R.string.apikey));
                        return chain.proceed(builder.build()); //sometime error crash
                    })
                    .build();
            client=GraphClient.Companion.build(contextWeakReference.get(),
                    contextWeakReference.get().getResources().getString(R.string.shopdomain),
                    contextWeakReference.get().getResources().getString(R.string.apikey),

                    builder -> {
                        builder.setHttpClient(httpClient);
                        builder.httpCache(contextWeakReference.get().getCacheDir(), config -> {
                            config.setCacheMaxSizeBytes(1024 * 1024 * 10);
                            config.setDefaultCachePolicy(policy);
                            return Unit.INSTANCE;
                        });
                        return Unit.INSTANCE;
                    });


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return client;
    }
}
