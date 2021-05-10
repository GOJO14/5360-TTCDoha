package com.goodwill.wholesale.homesection;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.shopify.buy3.GraphCallResult;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.QueryGraphCall;
import com.shopify.buy3.Storefront;
import com.shopify.buy3.Storefront.QueryRoot;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.adaptersection.Collections_Adapter;
import com.goodwill.wholesale.loadersection.CustomProgressDialog;
import com.goodwill.wholesale.productlistingsection.ProductListing;
import com.goodwill.wholesale.requestsection.ApiClient;
import com.goodwill.wholesale.storefrontqueries.Query;
import com.goodwill.wholesale.storefrontresponse.AsyncResponse;
import com.goodwill.wholesale.storefrontresponse.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

public class CategoriesFragment extends Fragment
{
    @Nullable
    Unbinder unbinder;
    @Nullable
    @BindView(R.id.categories_list) ListView categories_list;
    GraphClient client=null;
    String cursor="nocursor";
    boolean hasNextPage=false;
    //JSONArray array=null;
    List<Storefront.CollectionEdge> edges=null;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.magenative_categories, container, false);
        unbinder = ButterKnife.bind(this, view);
        //array=new JSONArray();
        categories_list.setDivider(new ColorDrawable(getResources().getColor(R.color.transparent)));
        categories_list.setDividerHeight(0);
        client= ApiClient.getGraphClient(getActivity(),true);
        getCollections(cursor);
        categories_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                try
                {
                    TextView id=view.findViewById(R.id.cat_id);
                    TextView cat_title=view.findViewById(R.id.cat_title);
                    Intent intent=new Intent(getActivity(), ProductListing.class);
                    intent.putExtra("cat_id",id.getText().toString());
                    intent.putExtra("cat_name",cat_title.getText().toString());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }


            }
        });
        categories_list.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i)
            {

            }
            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                if ((firstVisibleItem + visibleItemCount) != 0)
                {
                    if (((firstVisibleItem + visibleItemCount) == totalItemCount) && hasNextPage)
                    {
                        hasNextPage=false;
                        getCollections(cursor);
                    }
                }
            }
        });
        return  view;
    }

    private void getCollections(String cursor)
    {
        try
        {
            Timber.i("Cursor%s", cursor);
            QueryGraphCall call = client.queryGraph(Query.getCollections(cursor));
            Response.getGraphQLResponse(call,new AsyncResponse()
            {
                @Override
                public void finalOutput(@NonNull Object output,@NonNull boolean error )
                {
                    if(error)
                    {
                        GraphResponse<QueryRoot> response  = ((GraphCallResult.Success<QueryRoot>) output).getResponse();
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if(CustomProgressDialog.mDialog!=null)
                                {
                                    CustomProgressDialog.mDialog.dismiss();
                                    CustomProgressDialog.mDialog=null;
                                }
                                processCollections(response);
                            }
                        });
                    }
                    else
                    {
                        Log.i("ResponseError32",""+output.toString());
                        hasNextPage=false;
                    }
                }
            },getActivity());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @SuppressLint("TimberArgCount")
    private void processCollections(GraphResponse<QueryRoot> response)
    {
        try
        {
            hasNextPage= Objects.requireNonNull(response.getData()).getCollections().getPageInfo().getHasNextPage();
            Timber.i("hasNextPage", "%s", hasNextPage);
            List<Storefront.CollectionEdge>  data=response.getData().getCollections().getEdges();
            if(edges==null)
            {
                edges=data;
            }
            else
            {
                edges.addAll(data);
            }
            cursor=edges.get(edges.size()-1).getCursor();
            Collections_Adapter adapter = new Collections_Adapter(getActivity(), edges);
            int cp = Objects.requireNonNull(categories_list).getFirstVisiblePosition();
            categories_list.setAdapter(adapter);
            categories_list.setSelection(cp);
            adapter.notifyDataSetChanged();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroyView()
    {
        try
        {
            super.onDestroyView();
            unbinder.unbind();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
