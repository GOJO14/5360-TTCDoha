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
package com.goodwill.wholesale.orderssection;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shopify.buy3.GraphCallResult;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.QueryGraphCall;
import com.shopify.buy3.Storefront;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.adaptersection.OrderListAdapter;
import com.goodwill.wholesale.maincontainer.MainActivity;
import com.goodwill.wholesale.requestsection.ApiClient;
import com.goodwill.wholesale.storagesection.LocalData;
import com.goodwill.wholesale.storefrontqueries.Query;
import com.goodwill.wholesale.storefrontresponse.AsyncResponse;
import com.goodwill.wholesale.storefrontresponse.Response;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
public class OrderListing extends MainActivity
{
    @Nullable @BindView(R.id.orderlist) ListView OrderList;
    GraphClient client;
    String cursor="nocursor";
    boolean hasNextPage=false;
    LocalData data;
    List<Storefront.OrderEdge> edge;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ViewGroup content = findViewById(R.id.MageNative_frame_container);
        getLayoutInflater().inflate(R.layout.magenative_showorder_page, content, true);
        ButterKnife.bind(this);
        showbackbutton();
        showTittle(getResources().getString(R.string.myOrders));
        client=ApiClient.getGraphClient(OrderListing.this,true);
        data=new LocalData(OrderListing.this);
        edge=new ArrayList<Storefront.OrderEdge>();
        OrderList.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                if ((firstVisibleItem + visibleItemCount) != 0) {
                    if (((firstVisibleItem + visibleItemCount) == totalItemCount) && hasNextPage)
                    {
                        hasNextPage = false;
                        createOrderListRequest(data.getAccessToken(),cursor,"scroll");
                    }
                }
            }
        });
        OrderList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                TextView orderurl=view.findViewById(R.id.orderviewurl);
                TextView order_id=view.findViewById(R.id.order_id);
                Intent intent=new Intent(OrderListing.this,OrderWeblink.class);
                intent.putExtra("link",orderurl.getText().toString());
                intent.putExtra("order",order_id.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.magenative_slide_in,R.anim.magenative_slide_out);
            }
        });
        createOrderListRequest(data.getAccessToken(),cursor,"first");
    }
    private void createOrderListRequest(String accessToken, String cursor, String first)
    {
        try
        {
            Log.i("Cursor",""+cursor);
            QueryGraphCall call = client.queryGraph(Query.getOrderList(accessToken,cursor));
            Response.getGraphQLResponse(call,new AsyncResponse()
            {
                @Override
                public void finalOutput(@NonNull Object output,@NonNull boolean error )
                {
                    if(error)
                    {
                        GraphResponse<Storefront.QueryRoot> response  = ((GraphCallResult.Success<Storefront.QueryRoot>) output).getResponse();
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                processOrderListData(response,first);
                            }
                        });
                    }
                    else
                    {
                        Log.i("ResponseError",""+output.toString());
                        hasNextPage=false;
                    }
                }
            },OrderListing.this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void processOrderListData(GraphResponse<Storefront.QueryRoot> response, String first)
    {
        try
        {
            Storefront.OrderConnection connection  = response.getData().getCustomer().getOrders();
            hasNextPage=connection.getPageInfo().getHasNextPage();
            Log.i("hasNextPage", "" + hasNextPage);
            List<Storefront.OrderEdge> data = connection.getEdges();
            if(data.size()>0)
            {
                if(edge==null)
                {
                    edge=data;
                }
                else
                {
                    edge.addAll(data);
                }
                cursor=edge.get(edge.size()-1).getCursor();
                OrderListAdapter adapter=new OrderListAdapter(OrderListing.this,edge);
                int cp = OrderList.getFirstVisiblePosition();
                OrderList.setAdapter(adapter);
                OrderList.setSelection(cp);
                adapter.notifyDataSetChanged();
            }
            else
            {
                if (first.equals("first"))
                {
                    Toast.makeText(OrderListing.this, getResources().getString(R.string.noorder), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        return false;
    }
    @Override
    protected void onResume()
    {
        invalidateOptionsMenu();
        super.onResume();
    }
}
