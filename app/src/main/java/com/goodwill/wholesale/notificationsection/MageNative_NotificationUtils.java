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
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import com.goodwill.wholesale.R;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@SuppressWarnings("ALL")
class MageNative_NotificationUtils {
    private static String TAG = NotificationUtils.class.getSimpleName();
    private Context mContext;

    public MageNative_NotificationUtils() {
    }
    public MageNative_NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }
    public void showNotificationMessage(String title, String message, String timeStamp, @NonNull Intent intent) {
        showNotificationMessage(title, message, timeStamp, intent, null);


    }

    public void showNotificationMessage(final String title, final String message, final String timeStamp, @NonNull Intent intent, @Nullable String imageUrl) {
        try {
            if (TextUtils.isEmpty(message))
                return;

            final int icon = R.mipmap.ic_launcher;
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            final PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            mContext,
                            0,
                            intent,
                            PendingIntent.FLAG_CANCEL_CURRENT
                    );
            final Notification.Builder mBuilder = new Notification.Builder(
                    mContext);
            final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification");
            if (!TextUtils.isEmpty(imageUrl))
            {

                if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {

                    Bitmap bitmap = getBitmapFromURL(imageUrl);

                    if (bitmap != null) {
                        showBigNotification(bitmap, mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                    } else {
                        showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                    }
                }
            } else {
                showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                playNotificationSound();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void showSmallNotification(@NonNull Notification.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound)
    {

        NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();


        inboxStyle.bigText(message);
        int noti=R.mipmap.ic_launcher;



        /*Notification notification;
        notification = */
        mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(new Notification.InboxStyle().setBigContentTitle(title))
                //   .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(noti)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .build();
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name ="test";
            String description = "desc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this

            mBuilder.setChannelId("CHANNEL1");
            notificationManager.createNotificationChannel(channel);
        }


        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(m, mBuilder.build());
    }

    private void showBigNotification(Bitmap bitmap, @NonNull Notification.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {
        try {

            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.setBigContentTitle(title);
            bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
            bigPictureStyle.bigPicture(bitmap);
            int noti=R.mipmap.ic_launcher;

            Drawable drawable = new BitmapDrawable(mContext.getResources(),bitmap);

            mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                    .setStyle(new Notification.BigPictureStyle().setBigContentTitle(title).bigLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon)).bigPicture(bitmap))
                    //.setWhen(getTimeMilliSec(timeStamp))
                    .setSmallIcon(noti)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                    .setContentText(message)
                    .build();




            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                CharSequence name ="test";
                String description = "desc";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("CHANNEL2", name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this

                mBuilder.setChannelId("CHANNEL2");
                notificationManager.createNotificationChannel(channel);
            }

            Random random = new Random();
            int m = random.nextInt(9998 - 1000) + 1000;
            notificationManager.notify(m, mBuilder.build());
        }
        catch (Exception e)
        {
            Log.i("exception",e.toString());
        }


    }
    @Nullable
    public Bitmap getBitmapFromURL(String strURL)
    {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Playing notification sound
    public void playNotificationSound()
    {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + MyApplication.getInstance().getApplicationContext().getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(MyApplication.getInstance().getApplicationContext(), alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(@NonNull Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    // Clears notification tray messages
    public static void clearNotifications() {
        NotificationManager notificationManager = (NotificationManager) MyApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static long getTimeMilliSec(String timeStamp)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

}