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

package com.goodwill.wholesale.notificationsection;
import android.app.Application;
public class MyApplication extends Application
{
    private static MyApplication mInstance;
    public static synchronized MyApplication getInstance()
    {
        return mInstance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
}
