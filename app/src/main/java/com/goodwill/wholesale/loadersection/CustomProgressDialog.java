package com.goodwill.wholesale.loadersection;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.goodwill.wholesale.R;
public class CustomProgressDialog
{
    public static Dialog mDialog;
    public static void showProgress(Context context)
    {
        mDialog = new Dialog(context,R.style.TransparentProgressDialog);
        Window window = mDialog.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.elemento_progress_splash);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
    }
    public static void hideProgress()
    {
        try
        {
            if ((mDialog != null) && mDialog.isShowing())
            {
                mDialog.dismiss();
                Log.i("Dialog","IN");
            }
        }
        catch (final IllegalArgumentException e)
        {
            // Handle or log or ignore
        }
        catch (final Exception e)
        {
            // Handle or log or ignore
        }
        finally
        {
            mDialog = null;
        }
    }
}
