package com.goodwill.wholesale.jobscheduler;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.goodwill.wholesale.storagesection.LocalData;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class JobExecutor extends AsyncTask<Void,Void,String>
{
    WeakReference<Context> reference;
    LocalData data;
    public JobExecutor(Context context)
    {
       reference=new WeakReference<Context>(context);
       data=new LocalData(reference.get());
    }
    @Override
    protected String doInBackground(Void... voids)
    {
        String cartvalue="nocart";
        try
        {

            if(data.getLineItems()!=null)
            {
                /*Log.i("CartValues1","inlineitems");
                if(data.getcartDate()!=null)
                {
                    Log.i("CartValues1","incartdate");
                    Date date1=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss).parse(data.getcartDate());
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    Date date2=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(formatter.format(date));
                    long diff = date2.getTime() - date1.getTime();
                    long diffSeconds = diff / 1000 % 60;
                    long diffMinutes = diff / (60 * 1000) % 60;
                    long diffHours = diff / (60 * 60 * 1000);
                    int diffInDays = (int) ((date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24));
                    Log.i("CartValues1","seconds"+diffSeconds);
                    Log.i("CartValues1","minutes"+diffMinutes);
                    Log.i("CartValues1","hours"+diffHours);
                    Log.i("CartValues1","days"+diffInDays);*/
                    cartvalue="cart";
                /*}*/
            }
            Log.i("CartValues",""+cartvalue);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return cartvalue;
    }
}
