/*
 *  /**
 *      * CedCommerce
 *      *
 *      * NOTICE OF LICENSE
 *      *
 *      * This source file is subject to the End User License Agreement (EULA)
 *      * that is bundled with this package in the file LICENSE.txt.
 *      * It is also available through the world-wide-web at this URL:
 *      * http://cedcommerce.com/license-agreement.txt
 *      *
 *      * @category  Ced
 *      * @package   MageNative
 *      * @author    CedCommerce Core Team <connect@cedcommerce.com >
 *      * @copyright Copyright CEDCOMMERCE (http://cedcommerce.com/)
 *      * @license      http://cedcommerce.com/license-agreement.txt
 *
 */
package com.goodwill.wholesale.notificationsection;
import android.annotation.SuppressLint;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.requestsection.ApiClient;
import com.goodwill.wholesale.requestsection.ApiInterface;
import com.goodwill.wholesale.storefrontresponse.AsyncResponse;
import com.goodwill.wholesale.storefrontresponse.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
public class MageNative_MyFirebaseInstanceIDService extends FirebaseMessagingService
{
    ApiInterface apiService;
    @Override
    public void onNewToken(String refreshedToken)
    {
        super.onNewToken(refreshedToken);
        Log.d("NEW_TOKEN", refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }
    private void sendRegistrationToServer(String token)
    {
        Log.i("SetDevices","IN"+token);
        Log.i("SetDevices","IN");
        apiService = ApiClient.getClient(MageNative_MyFirebaseInstanceIDService.this).create(ApiInterface.class);
        @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Call<ResponseBody> call = apiService.setDevices(getResources().getString(R.string.mid),token," ","android",deviceId);
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        try
        {

            Response.getRetrofitResponse(call,new AsyncResponse()
            {
                @Override
                public void finalOutput(@NonNull Object output, @NonNull boolean error )
                {
                    if(error)
                    {
                      // creatHomePage(output.toString());
                    }
                    else
                    {
                        Log.i("ErrorHomePage",""+output.toString());
                    }
                }
            },MageNative_MyFirebaseInstanceIDService.this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
