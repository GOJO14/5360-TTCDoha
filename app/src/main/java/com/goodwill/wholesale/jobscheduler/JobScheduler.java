package com.goodwill.wholesale.jobscheduler;
import android.app.ActivityManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.goodwill.wholesale.R;
import com.goodwill.wholesale.checkoutsection.CartListing;
import com.goodwill.wholesale.notificationsection.NotificationUtils;
import com.goodwill.wholesale.storagesection.LocalData;

import java.util.List;

public class JobScheduler extends JobService
{
    private JobExecutor executor;
    @Override
    public boolean onStartJob(JobParameters jobParameters)
    {
        executor=new JobExecutor(getApplicationContext())
        {
            @Override
            protected void onPostExecute(String s)
            {
                Log.i("CartValues2",""+s);
                if(s.equals("cart"))
                {
                    if(isAppIsInBackground(getApplicationContext()))
                    {
                        showCartNotification();
                    }
                }
                jobFinished(jobParameters,true);
            }
        };
        executor.execute();
        return true;
    }

    private void showCartNotification()
    {
        try
        {
            LocalData data=new LocalData(getApplicationContext());
            String tittle="";

            if(data.isLogin())
            {
                tittle=getApplicationContext().getResources().getString(R.string.heyuser)+" "+data.getFirstName()+" "+data.getLastName();
            }
            else
            {
                tittle=getResources().getString(R.string.app_name);
            }
            Intent intent=new Intent(getApplicationContext(), CartListing.class);
            NotificationUtils   notificationUtils = new NotificationUtils(getApplicationContext());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notificationUtils.showNotificationMessage(tittle, getResources().getString(R.string.somethingleftinyourcart), intent);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters)
    {
        Log.i("CartValues2","cancel");
        executor.cancel(true);
        jobFinished(jobParameters,true);
        return false;
    }
    public static boolean isAppIsInBackground(Context context)
    {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH)
        {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses)
            {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
                {
                    for (String activeProcess : processInfo.pkgList)
                    {
                        if (activeProcess.equals(context.getPackageName()))
                        {
                            isInBackground = false;
                        }
                    }
                }
            }
        }
        else
        {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName()))
            {
                isInBackground = false;
            }
        }
        return isInBackground;
    }

}
