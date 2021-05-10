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
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.homesection.HomeWeblink;
import com.goodwill.wholesale.productlistingsection.ProductListing;
import com.goodwill.wholesale.productviewsection.ProductView;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

public class MageNative_MyFirebaseMessagingService extends FirebaseMessagingService
{
    private static final String TAG = "FirebaseMessageService";
    private NotificationUtils notificationUtils;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage)
    {
        try
        {
            Log.d(TAG, "From: " + remoteMessage.getFrom());
            Log.i("notification_test", ""+remoteMessage.getData().toString());
            JSONObject object=new JSONObject(remoteMessage.getData().toString());
            JSONObject data = object.getJSONObject("data");
            String title =data.getString("title");
            String mesg = data.getString("message");
            String imageUri = data.getString("image");
            String link_type = data.getJSONObject("payload").getString("link_type");
            String link_id = data.getJSONObject("payload").getString("link_id");
            Intent resultIntent = null;
            switch (link_type) {
                case "product":
                    String product_id = "gid://shopify/Product/" + link_id;
                    resultIntent = new Intent(getApplicationContext(), ProductView.class);
                    resultIntent.putExtra("id", getBase64Encode(product_id));
                    break;
                case "collection":
                    String s1 = "gid://shopify/Collection/" + link_id;
                    resultIntent = new Intent(getApplicationContext(), ProductListing.class);
                    resultIntent.putExtra("cat_id",getBase64Encode(s1));
                    resultIntent.putExtra("cat_name",title);
                    break;
                case "web_address":
                    resultIntent = new Intent(getApplicationContext(), HomeWeblink.class);
                    resultIntent.putExtra("link", link_id);
                    break;
            }
            if (TextUtils.isEmpty(imageUri)) {
                showNotificationMessage(getApplicationContext(), title, mesg, Objects.requireNonNull(resultIntent));
            } else {
                showNotificationMessageWithBigImage(getApplicationContext(), title, mesg, Objects.requireNonNull(resultIntent), imageUri);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void showNotificationMessage(Context context, String title, String message, Intent intent)
    {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, intent);
    }
    private void showNotificationMessageWithBigImage(Context context, String title, String message, Intent intent, @NonNull String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, intent, imageUrl);
    }
    public String getBase64Encode(String id)
    {
        byte[] data = Base64.encode(id.getBytes(), Base64.DEFAULT);
        try {
            id = new String(data, "UTF-8").trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return id;
    }
}