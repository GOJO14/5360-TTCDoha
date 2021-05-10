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
package com.goodwill.wholesale.splashsection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.shopify.buy3.GraphCallResult;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.MutationGraphCall;
import com.shopify.buy3.QueryGraphCall;
import com.shopify.buy3.Storefront;
import com.goodwill.wholesale.BuildConfig;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.homesection.HomePage;
import com.goodwill.wholesale.langauagesection.LoadLanguage;
import com.goodwill.wholesale.maincontainer.MainActivity;
import com.goodwill.wholesale.productviewsection.ProductView;
import com.goodwill.wholesale.requestsection.ApiClient;
import com.goodwill.wholesale.requestsection.ApiInterface;
import com.goodwill.wholesale.storagesection.LocalData;
import com.goodwill.wholesale.storefrontqueries.MutationQuery;
import com.goodwill.wholesale.storefrontqueries.Query;
import com.goodwill.wholesale.storefrontresponse.AsyncResponse;
import com.goodwill.wholesale.storefrontresponse.Response;
import com.goodwill.wholesale.trialexpiresection.TrialExpired;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.support.constraint.Constraints.TAG;

public class Splash extends Activity {
    public static final String WORK_DURATION_KEY = BuildConfig.APPLICATION_ID + ".WORK_DURATION_KEY";
    @Nullable
    @BindView(R.id.splash)
    ImageView splash;
    ApiInterface apiService;
    LocalData localData;
    boolean menusavailable = true;
    String product_id = "no_id";
    GraphClient client = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            FirebaseApp.initializeApp(Splash.this);
            setContentView(R.layout.magenative_splash);
            ButterKnife.bind(Splash.this);
            apiService = ApiClient.getClient(Splash.this).create(ApiInterface.class);
            client = ApiClient.getGraphClient(Splash.this, true);
            localData = new LocalData(Splash.this);
            LoadLanguage.setLocale(localData.getLangCode(), Splash.this);
            if (getIntent().getDataString() != null) {
                try {
                    String link = getIntent().getDataString();
                    if (link.contains("pid=")) {
                        String parts[] = link.split("pid=");
                        product_id = parts[1];
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            checkValidity();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Call<ResponseBody> call = apiService.getAppdata(getResources().getString(R.string.mid));
                    com.goodwill.wholesale.storefrontresponse.Response.getRetrofitResponse(call, new AsyncResponse() {
                        @Override
                        public void finalOutput(@NonNull Object output, @NonNull boolean error) {
                            if (error) {
                                //localData.saveBackgroundImages(output.toString());
                                try{

                                JSONObject object=new JSONObject(output.toString());
                                String splashimg = object.getString("launch_screen");
                                String loginback = object.getString("login_page_background_image");
                                String headerlogo = object.getString("app_header_logo");
                                localData.saveSplash(splashimg);
                                localData.saveLoginback(loginback);
                                localData.saveHeaderLogo(headerlogo);
                                Log.i("splashsplash",""+splashimg);
                                /*Glide.with(Splash.this)
                                            .asBitmap()
                                            .load(splashimg)
                                            .into(splash);*/

                                }catch (JSONException e){e.printStackTrace();}
                            }
                        }
                    }, Splash.this);
                }
            });
            //Log.i("splashsplash",""+localData.getSplash());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            splash.setImageResource(R.drawable.splash);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkValidity() {
        try {
            if (localData.getValidity() != null) {
                String valid = localData.getValidity();
                String parts[] = valid.split("#");
                if (parts[0].equals(getcurrentdate())) {
                    if (parts[1].equals("true")) {
                        if (menusavailable) {
                            getCategoryMenus();
                        } else {
                            checkToken();
                        }
                    } else {
                        trialExpired();
                    }
                } else {
                    sendRequest("checkvalidity");
                }
            } else {
                //Scheduling JOB;
                Log.i("CartValues3", "start");
                ComponentName mServiceComponent = new ComponentName(this, com.goodwill.wholesale.jobscheduler.JobScheduler.class);
                JobInfo.Builder builder = new JobInfo.Builder(101, mServiceComponent);
                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
                builder.setRequiresDeviceIdle(false);
                builder.setMinimumLatency(0);
                builder.setRequiresCharging(false);
                builder.setPersisted(true);
                PersistableBundle extras = new PersistableBundle();
                extras.putLong(WORK_DURATION_KEY, 10000);
                builder.setExtras(extras);
                Log.d(TAG, "Scheduling job");
                JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                tm.schedule(builder.build());
                sendRequest("checkvalidity");
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }
                                String token = task.getResult().getToken();
                                Log.d(TAG, "" + token);
                                sendRegistrationToServer(token);
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkToken() {
        try {
            if (localData.isLogin()) {
                Log.i("DeveloperTest", "3");
                if (localData.getExipry() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    Date expiretime = sdf.parse(localData.getExipry());
                    Date currentDate = new Date();
                    long diff = expiretime.getTime() - currentDate.getTime();
                    long seconds = diff / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    long days = hours / 24;
                    Log.i("Days", "" + days);
                    if (days == 0) {
                        Log.i("DeveloperTest", "2");
                        renewToken();
                    } else {
                        Log.i("DeveloperTest", "3");
                        processFurther();
                    }
                }
            } else {
                Log.i("DeveloperTest", "1");
                processFurther();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renewToken() {
        try {
            MutationGraphCall call = client.mutateGraph(MutationQuery.renewToken(localData.getAccessToken()));
            com.goodwill.wholesale.storefrontresponse.Response.getMutationGraphQLResponse(call, new AsyncResponse() {
                @Override
                public void finalOutput(@NonNull Object output, @NonNull boolean error) {
                    if (error) {
                        GraphResponse<Storefront.Mutation> response = ((GraphCallResult.Success<Storefront.Mutation>) output).getResponse();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<Storefront.UserError> errors = response.getData().getCustomerAccessTokenRenew().getUserErrors();
                                ;
                                if (errors.size() > 0) {
                                    Iterator iterator = errors.iterator();
                                    String err = "";
                                    while (iterator.hasNext()) {
                                        Storefront.UserError error = (Storefront.UserError) iterator.next();
                                        err += error.getMessage();
                                    }

                                    Toast.makeText(Splash.this, err, Toast.LENGTH_LONG).show();
                                } else {
                                    processToken(response);
                                }
                            }
                        });
                    } else {
                        Log.i("ResponseError", "" + output.toString());
                    }
                }
            }, Splash.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processToken(GraphResponse<Storefront.Mutation> response) {
        try {
            Storefront.CustomerAccessToken token = response.getData().getCustomerAccessTokenRenew().getCustomerAccessToken();
            Log.i("ExpireAt", "" + token.getExpiresAt().toLocalDateTime().toString());
            localData.saveAccesstokenWithExpiry(token.getAccessToken(), token.getExpiresAt().toLocalDateTime().toString());
            QueryGraphCall call = client.queryGraph(Query.getCustomerDetails(token.getAccessToken()));
            com.goodwill.wholesale.storefrontresponse.Response.getGraphQLResponse(call, new AsyncResponse() {
                @Override
                public void finalOutput(@NonNull Object output, @NonNull boolean error) {
                    if (error) {
                        GraphResponse<Storefront.QueryRoot> response = ((GraphCallResult.Success<Storefront.QueryRoot>) output).getResponse();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                localData.saveFirstName(response.getData().getCustomer().getFirstName());
                                localData.saveCustomerId(response.getData().getCustomer().getId().toString());
                                localData.saveLastName(response.getData().getCustomer().getLastName());
                                localData.savephone(response.getData().getCustomer().getLastName());
                                processFurther();
                            }
                        });
                    } else {
                        Log.i("ResponseError", "" + output.toString());
                    }
                }
            }, Splash.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(String origin) {
        try {
            Call<ResponseBody> call = null;
            if (origin.equals("checkvalidity")) {
                call = apiService.getStatus(getResources().getString(R.string.mid), getResources().getString(R.string.device_type));
            }
            if (origin.equals("getmenus")) {
                call = apiService.getCategoryMenus(getResources().getString(R.string.mid));
            }
            getResponse(call, origin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getResponse(Call<ResponseBody> call, final String origin) {
        try {
            com.goodwill.wholesale.storefrontresponse.Response.getRetrofitResponse(call, new AsyncResponse() {
                @Override
                public void finalOutput(@NonNull Object output, @NonNull boolean error) {
                    if (error) {
                        if (origin.equals("checkvalidity")) {
                            processValidity(output.toString());
                        }
                        if (origin.equals("getmenus")) {
                            processMenus(output.toString());
                        }

                    } else {
                        checkToken();
                    }
                }
            }, Splash.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getcurrentdate() {
        String formatted = null;
        try {
            Calendar date = Calendar.getInstance();
            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
            formatted = format1.format(date.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formatted;
    }

    private void processFurther() {
        try {
            Intent intent = null;
            if (product_id.equals("no_id")) {
                intent = new Intent(Splash.this, HomePage.class);
            } else {
                intent = new Intent(Splash.this, ProductView.class);
                intent.putExtra("id", product_id);
            }
            startActivity(intent);
            overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void trialExpired() {
        try {
            Intent intent = new Intent(Splash.this, TrialExpired.class);
            startActivity(intent);
            overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processValidity(String data) {
        try {
            JSONObject object = new JSONObject(data);
            if (object.getBoolean("success")) {
                if (object.getString("status").equals("active")) {
                    localData.saveValidity(getcurrentdate() + "#true");
                    if (menusavailable) {
                        getCategoryMenus();
                    } else {
                        checkToken();
                    }
                } else {
                    localData.saveValidity(getcurrentdate() + "#false");
                    trialExpired();
                }
            } else {
                localData.saveValidity(getcurrentdate() + "#false");
                trialExpired();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCategoryMenus() {
        try {
            sendRequest("getmenus");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void processMenus(String data) {
        try {
            localData.saveMenus(data);
            checkToken();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendRegistrationToServer(String token) {
        Log.i("SetDevices", "IN" + token);
        Log.i("SetDevices", "IN");
        @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Call<ResponseBody> call = apiService.setDevices(getResources().getString(R.string.mid), token, " ", "android", deviceId);
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        try {

            Response.getRetrofitResponse(call, new AsyncResponse() {
                @Override
                public void finalOutput(@NonNull Object output, @NonNull boolean error) {
                    if (error) {
                        // creatHomePage(output.toString());
                    } else {
                        Log.i("ErrorHomePage", "" + output.toString());
                    }
                }
            }, Splash.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

