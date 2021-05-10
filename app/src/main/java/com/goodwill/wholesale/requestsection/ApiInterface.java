package com.goodwill.wholesale.requestsection;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface
{
    @GET("getstatus")
    Call<ResponseBody> getStatus(@Query("mid") String mid, @Query("device_type") String device_type);
    @GET("getcategorymenus")
    Call<ResponseBody> getCategoryMenus(@Query("mid") String mid);
    @GET("appdata")
    Call<ResponseBody> getAppdata(@Query("mid") String mid);
    @GET("homepagedata")
    Call<ResponseBody> getHomePage(@Query("mid") String mid);
    @GET("setorder")
    Call<ResponseBody> setOrder(@Query("mid") String mid,@Query("checkout_token") String checkout_token);
    @GET("setdevices")
    Call<ResponseBody> setDevices(@Query("mid") String mid,@Query("device_id") String device_id,@Query("email") String email,@Query("type") String type,@Query("unique_id") String unique_id);
    @GET("hulkvolumediscountoptionsapi/getvolumeoptions")
    Call<ResponseBody> getVolumeOptions(@Query("shop") String shop,@Query("product_id") String product_id);
    @GET("getcustomerdata")
    Call<ResponseBody> getcustomerdata(@Query("fields") String fields,@Query("cid") String cid,@Query("mid") String mid);
    @GET("product_offer_app.php")
    Call<ResponseBody> getproductoffer(@Query("product_id") String product_id);
    @GET("collection_offer_mobile.php")
    Call<ResponseBody> getcollectionoffer(@Query("arr_id") String arr_id);
    @GET("cart_offer_mobile.php")
    Call<ResponseBody> get_cart_offer_mobile(@Query("arr_id") String arr_id,@Query("arr_quantity") String quantity_arr);
    @GET("get_checkout_mobile.php")
    Call<ResponseBody> get_checkout_mobile(@Query("draft") String draft,@Query("items_arr") String arr_id,@Query("quantity_arr") String quantity_arr);
    @GET("installedstatus")
    Call<ResponseBody> details(@Query("mid") String mid);
    @GET("getslots")
    Call<ResponseBody> getslots(@Query("mid") String mid,@Query("date") String date,@Query("ver")String vrsion);
    @GET
    Call<ResponseBody> getCheckoutUrl( @Url String url);
    @GET("getcollectionproductsbytags")
    Call<ResponseBody> getProductsbyTags(@Query("mid") String mid,@Query("handle") String handle,@Query("sort") String sort,@Query("page") int page,@Query("tags") String tags);
}
