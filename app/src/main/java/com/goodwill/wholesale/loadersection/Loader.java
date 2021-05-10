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
package com.goodwill.wholesale.loadersection;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.goodwill.wholesale.R;
public class Loader extends ProgressDialog
{
    Context context_new;
    public Loader(Context context)
    {
        super(context, R.style.TransparentProgressDialog);
        try
        {
            context_new = context;
            WindowManager.LayoutParams wlmp = getWindow().getAttributes();
            Window window = this.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            window.addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            wlmp.gravity = Gravity.CENTER_HORIZONTAL;
            getWindow().setAttributes(wlmp);
            getWindow().requestFeature(Window.FEATURE_PROGRESS);
            setTitle(null);
            setCancelable(true);
            setOnCancelListener(null);
            getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            View view = View.inflate(context_new, R.layout.elemento_progress_splash, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            addContentView(view, params);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void show()
    {
        try
        {
            super.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void dismiss()
    {
        try
        {
            super.dismiss();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
