package com.goodwill.wholesale.datasection;
import android.content.Context;
import com.goodwill.wholesale.R;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
public class Data
{
    static WeakReference<Context> con;
    public  static JSONArray getSortOptions(Context context)
    {
        con=new WeakReference<Context>(context);
        JSONArray array=new JSONArray();
        array.put(con.get().getResources().getString(R.string.name_a_to_z));
        array.put(con.get().getResources().getString(R.string.name_z_to_a));
        array.put(con.get().getResources().getString(R.string.price_h_to_l));
        array.put(con.get().getResources().getString(R.string.price_l_to_h));
        array.put(con.get().getResources().getString(R.string.best_selling));
        array.put(con.get().getResources().getString(R.string.latest_collection));
        return array;
    }
    public  static JSONArray getSortOptionsAllProducts(Context context)
    {
        con=new WeakReference<Context>(context);
        JSONArray array=new JSONArray();
        array.put(con.get().getResources().getString(R.string.name_a_to_z));
        array.put(con.get().getResources().getString(R.string.name_z_to_a));
        array.put(con.get().getResources().getString(R.string.latest_collection));
        return array;
    }
    public  static JSONArray getLangauages( )
    {

        JSONArray array=new JSONArray();
        try
        {
            JSONObject object1=new JSONObject();
            object1.put("name","English");
            object1.put("code","en");
            array.put(object1);
            JSONObject object2=new JSONObject();
            object2.put("name","Arabic");
            object2.put("code","ar");
            array.put(object2);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return array;
    }
 }
