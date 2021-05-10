package com.goodwill.wholesale.storefrontresponse;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphCallResult;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.MutationGraphCall;
import com.shopify.buy3.QueryGraphCall;
import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.Error;
import com.goodwill.wholesale.loadersection.CustomProgressDialog;
import com.goodwill.wholesale.loadersection.Loader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;

import kotlin.Unit;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
public class Response
{
    static WeakReference<Context> reference;
    static Loader progress=null;
    public static void  getGraphQLResponse(QueryGraphCall call,AsyncResponse output,Context context)
    {
         reference=new WeakReference<Context>(context);
         if(progress!=null)
         {
             if(progress.isShowing())
             {
                 closeDialog();
             }
         }
         showDialog();
        call.enqueue(new Handler(Looper.getMainLooper()), result -> {
            if (result instanceof GraphCallResult.Success)
            {
                closeDialog();
                try {
                    if (((GraphCallResult.Success<Storefront.QueryRoot>) result).getResponse().getHasErrors())
                    {
                        List<Error> errors = ((GraphCallResult.Success<Storefront.QueryRoot>) result).getResponse().getErrors();
                        Iterator iterator = errors.iterator();
                        StringBuilder errormessage = new StringBuilder();
                        Error error=null;
                        while (iterator.hasNext())
                        {
                            error = (Error) iterator.next();
                            errormessage.append(error.message());
                        }
                        Log.e("ERROR", "" + errormessage);
                        output.finalOutput(errormessage.toString(),false);
                    }
                    else
                    {
                        output.finalOutput(result,true);
                    }
                    closeDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                closeDialog();
                try {
                    output.finalOutput(((GraphCallResult.Failure) result).getError().getMessage(),false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return Unit.INSTANCE;
        });
         /*call.enqueue(new GraphCall.Callback<Storefront.QueryRoot>()
         {
             @Override
             public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response)
             {
                 try
                 {
                     closeDialog();
                     if (response.hasErrors())
                     {
                         List<Error> errors = response.errors();
                         Iterator iterator = errors.iterator();
                         StringBuilder errormessage = new StringBuilder();
                         Error error=null;
                         while (iterator.hasNext())
                         {
                             error = (Error) iterator.next();
                             errormessage.append(error.message());
                         }
                         Log.e("ERROR", "" + errormessage);
                         output.finalOutput(errormessage.toString(),false);
                     }
                     else
                     {
                         output.finalOutput(response,true);
                     }
                     closeDialog();
                 }
                 catch (Exception e)
                 {
                     e.printStackTrace();
                 }
             }
             @Override
             public void onFailure(@NonNull GraphError error)
             {
                 Log.e("ERROR", "Network Failure", error);
                 closeDialog();
             }
         });*/
     }
    public static void  getMutationGraphQLResponse(MutationGraphCall call, AsyncResponse output,Context context)
    {
        reference=new WeakReference<Context>(context);
        showDialog();
        call.enqueue(new Handler(Looper.getMainLooper()), result -> {
            if (result instanceof GraphCallResult.Success)
            {
                closeDialog();
                try {
                    if (((GraphCallResult.Success<Storefront.Mutation>) result).getResponse().getHasErrors())
                    {
                        List<Error> errors = ((GraphCallResult.Success<Storefront.Mutation>) result).getResponse().getErrors();
                        Iterator iterator = errors.iterator();
                        StringBuilder errormessage = new StringBuilder();
                        Error error=null;
                        while (iterator.hasNext())
                        {
                            error = (Error) iterator.next();
                            errormessage.append(error.message());
                        }
                        Log.e("ERROR", "" + errormessage);
                        output.finalOutput(errormessage.toString(),false);
                    }
                    else
                    {
                        output.finalOutput(result,true);
                    }
                    closeDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                closeDialog();
                try {
                    output.finalOutput(((GraphCallResult.Failure) result).getError().getMessage(),false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return Unit.INSTANCE;
        });
        /*call.enqueue(new GraphCall.Callback<Storefront.Mutation>()
        {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response)
            {
                try
                {
                    closeDialog();
                    if (response.hasErrors())
                    {
                        List<Error> errors = response.errors();
                        Iterator iterator = errors.iterator();
                        StringBuilder errormessage = new StringBuilder();
                        Error error=null;
                        while (iterator.hasNext())
                        {
                            error = (Error) iterator.next();
                            errormessage.append(error.message());
                        }
                        Log.e("ERROR", "" + errormessage);
                        output.finalOutput(errormessage.toString(),false);
                    }
                    else
                    {
                        output.finalOutput(response,true);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(@NonNull GraphError error)
            {
                Log.e("ERROR", "Network Failure", error);
                closeDialog();
            }
        });*/
    }
     public static void getRetrofitResponse(Call<ResponseBody> call,AsyncResponse output,Context context)
     {
         reference=new WeakReference<Context>(context);
         if(progress!=null)
         {
             if(progress.isShowing())
             {
                 closeDialog();
             }
         }
         try
         {
             showDialog();
             call.enqueue(new Callback<ResponseBody>()
             {
                 @Override
                 public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response)
                 {
                     try
                     {
                         closeDialog();
                         Log.i("RESPONSE_URL",""+call.request().url());
                         StringBuilder responsedata = new StringBuilder();
                         int statusCode = response.code();
                         Log.i("RESPONSE_CODE",""+statusCode);
                         if(statusCode== HttpsURLConnection.HTTP_ACCEPTED || statusCode == HttpsURLConnection.HTTP_OK)
                         {
                             String line;
                             BufferedReader br = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                             while ((line = br.readLine()) != null)
                             {
                                 responsedata.append(line);
                             }
                             Log.i("RESPONSE_RESULT",""+ responsedata);
                             output.finalOutput(responsedata,true);
                         }
                         else
                         {
                             Log.e("ErrorstatusCode",""+statusCode);
                             output.finalOutput(statusCode,false);
                         }
                     }
                     catch (Exception e)
                     {
                         e.printStackTrace();
                     }
                 }
                 @Override
                 public void onFailure(Call<ResponseBody> call, Throwable t)
                 {
                     try
                     {
                         closeDialog();
                         output.finalOutput(t.getMessage(),false);
                     }
                     catch (Exception e)
                     {
                         e.printStackTrace();
                     }
                 }
             });
         }
         catch (Exception e)
         {
             e.printStackTrace();
         }
     }
     public static void showDialog()
     {
         try
         {
             if(progress==null)
             {
                 progress=new Loader(reference.get());
                 progress.show();
             }

         }catch (Exception e)
         {
             e.printStackTrace();
         }

     }
     public static void closeDialog()
     {
         try
         {
             if(progress.isShowing() && progress!=null)
             {
                 progress.dismiss();
             }
         }
         catch (Exception e)
         {
             e.printStackTrace();
         }

     }
}
