package com.goodwill.wholesale.checkoutsection;

import android.content.Context;
import android.util.Log;

import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;
import com.goodwill.wholesale.storagesection.LocalData;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
public  class CheckoutLineItems
{
    WeakReference<Context> reference;
    LocalData data=null;
    public CheckoutLineItems(Context context)
    {
        reference=new WeakReference<Context>(context);
        data=new LocalData(reference.get());
    }
    public int getItemcounts()
    {
        return getLineItems().size();
    }

    public List<Storefront.CheckoutLineItemInput> getLineItems()
    {
        List<Storefront.CheckoutLineItemInput> checkoutLineItemInputs = new ArrayList<Storefront.CheckoutLineItemInput>();
        try
        {

            if(data.getLineItems()!=null)
            {
                JSONObject object=new JSONObject(data.getLineItems());
                Iterator<String> iter = object.keys();
                Storefront.CheckoutLineItemInput itemInput=null;
                while (iter.hasNext())
                {
                    String key = iter.next();
                    int value = object.getInt(key);
                    itemInput = new Storefront.CheckoutLineItemInput(value, new ID(key));
                    checkoutLineItemInputs.add(itemInput);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return checkoutLineItemInputs;
    }
}
