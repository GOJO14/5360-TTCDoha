package com.goodwill.wholesale.storagesection;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.shopify.buy3.Storefront;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LocalData
{
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int Private_Mode=0;
    WeakReference<Context> context;
    private static final String LocalData="LocalData";
    public  LocalData (Context context)
    {
        this.context=new WeakReference<Context>(context);
        pref=this.context.get().getSharedPreferences(LocalData,Private_Mode);
        editor = pref.edit();
        editor.apply();
    }
    public String getValidity()
    {
        return pref.getString("valid", null);
    }
    public void saveValidity(String valid)
    {
        editor.putString("valid", valid);
        editor.commit();
    }
    public void saveMenus(String menus)
    {
        editor.putString("menus",menus);
        editor.commit();
    }
    public String getMenus()
    {
        return pref.getString("menus",null);
    }

    /*public void saveBackgroundImages(String images)
    {
        editor.putString("images",images);
        editor.commit();
    }
    public String getBackgroundImages()
    {
        return pref.getString("images",null);
    }*/



    public void saveSplash(String image)
    {
        editor.putString("splash",image);
        editor.commit();
    }
    public String getSplash()
    {
        return pref.getString("splash",null);
    }


    public void saveLoginback(String image)
    {
        editor.putString("loginback",image);
        editor.commit();
    }
    public String getLoginback()
    {
        return pref.getString("loginback",null);
    }

    public void saveHeaderLogo(String image)
    {
        editor.putString("logo",image);
        editor.commit();
    }
    public String getHeaderLogo()
    {
        return pref.getString("logo",null);
    }





    public void saveMoneyFormat(String format)
    {
        editor.putString("format",format);
        editor.commit();
    }
    public String getMoneyFormat()
    {
       return pref.getString("format",null);
    }
    public void saveLineItems(JSONObject object)
    {
        if(object.length()>0)
        {
            editor.putString("LineItems",object.toString());
            editor.commit();
        }
        else
        {
            clearLineItems();
        }
    }
    public String getLineItems()
    {
        return pref.getString("LineItems",null);
    }
    public void clearLineItems()
    {
        editor.remove("LineItems");
        editor.commit();
    }
    public void saveCouponCode(String coupon)
    {
        editor.putString("coupon",coupon);
        editor.commit();
    }
    public String getCoupon()
    {
        return pref.getString("coupon",null);
    }
    public void clearCoupon()
    {
        editor.remove("coupon");
        editor.commit();
    }
    public void saveCheckoutId(String checkout_id)
    {
        editor.putString("checkout_id",checkout_id);
        editor.commit();
    }
    public String getCheckoutId()
    {
        return pref.getString("checkout_id",null);
    }
    public void clearCheckoutId()
    {
        editor.remove("checkout_id");
        editor.commit();
    }
    public void saveWishList(JSONObject object)
    {
        try
        {
            if(object.length()>0)
            {
                editor.putString("wishlist",object.toString());
                editor.commit();
            }
            else
            {
                clearwishList();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public String getWishList()
    {
        return pref.getString("wishlist",null);
    }
    public void clearwishList()
    {
        editor.remove("wishlist");
        editor.commit();
    }
    public void createSession(String username,String password)
    {
        editor.putString("username",username);
        editor.putString("password",password);
        editor.putBoolean("islogin",true);

        editor.commit();
    }
    public String getEmail()
    {
        return pref.getString("username",null);
    }
    public String getPassword()
    {
        return pref.getString("password",null);
    }
    public void saveFirstName(String firstname)
    {
        editor.putString("firstname",firstname);
        editor.commit();
    }
    public String getFirstName()
    {
        return pref.getString("firstname",null);
    }
    public void saveLastName(String lastname)
    {
        editor.putString("lastname",lastname);
        editor.commit();
    }
    public String getLastName()
    {
        return pref.getString("lastname",null);
    }
    public void saveCustomerId(String lastname)
    {
        editor.putString("customerid",lastname);
        editor.commit();
    }
    public String getCustomerId()
    {
        return pref.getString("customerid",null);
    }
    public boolean isLogin()
    {
       return pref.getBoolean("islogin",false);
    }
    public String getAlreadyLogin()
    {
       return pref.getString("isAlreadyLogin","");
    }
    public void setAlreadyLogin(String isAlreadyLogin)
    {
       editor.putString("isAlreadyLogin",isAlreadyLogin);
       editor.commit();
    }
    public void saveAccesstokenWithExpiry(String accesstoken,String times)
    {
        editor.putString("accesstoken",accesstoken);
        editor.putString("expiry",times);
        editor.commit();
    }
    public String getAccessToken()
    {
        return pref.getString("accesstoken",null);
    }

    public String getExipry()
    {
        return pref.getString("expiry",null);
    }
    public void logout()
    {
        clearLineItems();
        clearwishList();
        clearCheckoutId();
        clearCoupon();
        editor.remove("islogin");
        editor.remove("username");
        editor.remove("password");
        editor.remove("firstname");
        editor.remove("lastname");
        editor.remove("accesstoken");
        editor.remove("expiry");
        editor.remove("isAlreadyLogin");
        editor.commit();
    }
    public void saveLangCode(String code)
    {
        editor.putString("lang",code);
        editor.commit();
    }
    public String getLangCode()
    {
        return pref.getString("lang","en");
    }
    public  void setRecentlyViewed(JSONObject value, String id)
    {
        try
        {
            editor.putString("recent", value.toString());
            editor.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public JSONObject getRecentlyViewed()
    {
        JSONObject productFromShared=null;
        String jsonPreferences = pref.getString("recent", null);
        if(jsonPreferences==null)
        {
            productFromShared= null;
        }
        else
        {
            try
            {
                productFromShared=new JSONObject(jsonPreferences);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return  productFromShared ;
    }
    public void savePrivacyPolicy(String url,String name)
    {
        editor.putString("privacy_url",url);
        editor.putString("privacy_name",name);
        editor.commit();
    }
    public String getPrivacyPolicy()
    {
        return pref.getString("privacy_name",null)+"#"+pref.getString("privacy_url",null);
    }
    public void saveRefundPolicy(String url,String name)
    {
        editor.putString("refund_url",url);
        editor.putString("refund_name",name);
        editor.commit();
    }
    public String getRefundPolicy()
    {
        return pref.getString("refund_name",null)+"#"+pref.getString("refund_url",null);
    }
    public void saveTerms(String url,String name)
    {
        editor.putString("terms_url",url);
        editor.putString("terms_name",name);
        editor.commit();
    }
    public String getTerms()
    {
        return pref.getString("terms_name",null)+"#"+pref.getString("terms_url",null);
    }
    public void saveCartDate(String date)
    {
        editor.putString("cartdate",date);
        editor.commit();
    }
    public String getcartDate()
    {
       return pref.getString("cartdate",null);
    }

    public void savephone(String phone) {
        editor.putString("phone",phone);
        editor.commit();
    }
    public String getphone() {
        return pref.getString("phone","");
    }
}
