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
package com.goodwill.wholesale.trialexpiresection;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.homesection.HomePage;
import com.goodwill.wholesale.requestsection.ApiClient;
import com.goodwill.wholesale.requestsection.ApiInterface;
import com.goodwill.wholesale.splashsection.Splash;
import com.goodwill.wholesale.storagesection.LocalData;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.net.ssl.HttpsURLConnection;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class TrialExpired extends Activity
{
    @Nullable
    @BindView(R.id.expired) ImageView expired;
    @Nullable
    @BindView(R.id.tryagain) TextView tryagain;
    ApiInterface apiService;
    LocalData localData;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magenative_expiry_layout);
        ButterKnife.bind(TrialExpired.this);
        apiService = ApiClient.getClient(TrialExpired.this).create(ApiInterface.class);
        localData=new LocalData(TrialExpired.this);
    }
    @Override
    protected void onResume()
    {
        try
        {
            super.onResume();
            expired.setImageResource(R.drawable.trial_image);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @OnClick(R.id.tryagain)
    public void tryagain()
    {
        sendStatusRequest();
    }
    private void sendStatusRequest()
    {
        try
        {
            Call<ResponseBody> call = apiService.getStatus(getResources().getString(R.string.mid),getResources().getString(R.string.device_type));
            getStatusResponse(call);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void getStatusResponse(Call<ResponseBody> call)
    {
        try
        {
            call.enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                {
                    try
                    {
                        Log.i("RESPONSE_URL",""+call.request().url());
                        StringBuilder responsedata = new StringBuilder();
                        int statusCode = response.code();
                        Log.i("RESPONSE_CODE",""+statusCode);
                        if(statusCode== HttpsURLConnection.HTTP_ACCEPTED)
                        {
                            String line;
                            BufferedReader br = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                            while ((line = br.readLine()) != null)
                            {
                                responsedata.append(line);
                            }
                            Log.i("RESPONSE_RESULT",""+ responsedata);
                            JSONObject object=new JSONObject(responsedata.toString());
                            if(object.getBoolean("success"))
                            {
                                if(object.getString("status").equals("active"))
                                {
                                    localData.saveValidity(getcurrentdate()+"#true");
                                    processFurther();
                                }
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {
                    Log.i("RESPONSE_ERROR", t.toString());
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public String getcurrentdate()
    {
        String formatted = null;
        try
        {
            Calendar date= Calendar.getInstance();
            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
            formatted = format1.format(date.getTime());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return  formatted;
    }
    private void processFurther()
    {
        try
        {
            Intent intent=new Intent(TrialExpired.this, HomePage.class);
            startActivity(intent);
            overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
            finish();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
