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
package com.goodwill.wholesale.addresssection;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shopify.buy3.GraphCallResult;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.MutationGraphCall;
import com.shopify.buy3.QueryGraphCall;
import com.shopify.buy3.Storefront;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.adaptersection.addressListAdapter;
import com.goodwill.wholesale.maincontainer.MainActivity;
import com.goodwill.wholesale.requestsection.ApiClient;
import com.goodwill.wholesale.storagesection.LocalData;
import com.goodwill.wholesale.storefrontqueries.MutationQuery;
import com.goodwill.wholesale.storefrontqueries.Query;
import com.goodwill.wholesale.storefrontresponse.AsyncResponse;
import com.goodwill.wholesale.storefrontresponse.Response;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
public class AddressList extends MainActivity
{
    @Nullable @BindView(R.id.address_list) ListView address_list;
    @Nullable @BindView(R.id.addaddress) TextView addaddress;
    @Nullable @BindView(R.id.addaddress2) TextView addaddress2;
    GraphClient client;
    String cursor="nocursor";
    boolean hasNextPage=false;
    LocalData data;
    List<Storefront.MailingAddressEdge> edge=null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ViewGroup content = findViewById(R.id.MageNative_frame_container);
        getLayoutInflater().inflate(R.layout.address_list, content, true);
        ButterKnife.bind(AddressList.this);
        showbackbutton();
        showTittle(getResources().getString(R.string.MyAddress));
        address_list.setDividerHeight(0);
        client= ApiClient.getGraphClient(AddressList.this,true);
        data=new LocalData(AddressList.this);
        edge=new ArrayList<Storefront.MailingAddressEdge>();
        address_list.setOnScrollListener(new AbsListView.OnScrollListener()
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
                        createAddressListRequest(data.getAccessToken(),cursor,"scroll");
                    }
                }
            }
        });
        addaddress.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent addaddress=new Intent(AddressList.this, AddAddress.class);
                addaddress.putExtra("name",getResources().getString(R.string.addaddress));
                startActivityForResult(addaddress,2);
                overridePendingTransition(R.anim.magenative_slide_in,R.anim.magenative_slide_out);
            }
        });
        addaddress2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent addaddress=new Intent(AddressList.this, AddAddress.class);
                addaddress.putExtra("name",getResources().getString(R.string.addaddress));
                startActivityForResult(addaddress,2);
                overridePendingTransition(R.anim.magenative_slide_in,R.anim.magenative_slide_out);
            }
        });
        createAddressListRequest(data.getAccessToken(),cursor,"first");
    }

    public void deleteAddress(String s, String accessToken)
    {
        try
        {

            MutationGraphCall call = client.mutateGraph(MutationQuery.deleteCustomerAddress(data.getAccessToken(),s));
            Response.getMutationGraphQLResponse(call,new AsyncResponse()
            {
                @Override
                public void finalOutput(@NonNull Object output,@NonNull boolean error)
                {
                    if(error)
                    {
                        GraphResponse<Storefront.Mutation> response  = ((GraphCallResult.Success<Storefront.Mutation>) output).getResponse();
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                List<Storefront.UserError> errors = response.getData().getCustomerAddressDelete().getUserErrors();
                                if(errors.size() > 0)
                                {
                                    boolean iserror=false;
                                    Iterator iterator = errors.iterator();
                                    String err = "";
                                    while (iterator.hasNext())
                                    {
                                        Storefront.UserError error = (Storefront.UserError) iterator.next();
                                        err += error.getMessage();
                                    }
                                    Toast.makeText(AddressList.this,err,Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    Toast.makeText(AddressList.this,getResources().getString(R.string.succesfullydelete),Toast.LENGTH_LONG).show();
                                    refreshList();
                                }
                            }
                        });
                    }
                    else
                    {
                        Log.i("ResponseError",""+output.toString());
                    }
                }
            },AddressList.this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void refreshList()
    {
        try {
            String cursor="nocursor";
            hasNextPage=false;
            client= ApiClient.getGraphClient(AddressList.this,false);
            edge.clear();
            createAddressListRequest(data.getAccessToken(),cursor,"first");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void createAddressListRequest(String accessToken, String cursor, String first)
    {
        try
        {
            Log.i("Cursor",""+cursor);
            QueryGraphCall call = client.queryGraph(Query.getAddressList(accessToken,cursor));
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
                                processAddressListData(response,first);
                            }
                        });
                    }
                    else
                    {
                        Log.i("ResponseError",""+output.toString());
                        hasNextPage=false;
                    }
                }
            },AddressList.this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void processAddressListData(GraphResponse<Storefront.QueryRoot> response, String origin)
    {
        try
        {
            try
            {
                Storefront.MailingAddressConnection connection  = response.getData().getCustomer().getAddresses();
                hasNextPage=connection.getPageInfo().getHasNextPage();
                Log.i("hasNextPage", "" + hasNextPage);
                List<Storefront.MailingAddressEdge> data = connection.getEdges();
                if(data.size()>0)
                {
                    addaddress.setVisibility(View.VISIBLE);
                    address_list.setVisibility(View.VISIBLE);
                    addaddress2.setVisibility(View.GONE);
                    if(edge==null)
                    {
                        edge=data;
                    }
                    else
                    {
                        edge.addAll(data);
                    }
                    cursor=edge.get(edge.size()-1).getCursor();
                    addressListAdapter addressadapter=new addressListAdapter(AddressList.this,edge);
                    int cp = address_list.getFirstVisiblePosition();
                    address_list.setAdapter(addressadapter);
                    address_list.setSelection(cp);
                    addressadapter.notifyDataSetChanged();
                }
                else
                {
                    if (origin.equals("first"))
                    {
                        Toast.makeText(AddressList.this, getResources().getString(R.string.noaddress), Toast.LENGTH_LONG).show();
                        addaddress.setVisibility(View.GONE);
                        address_list.setVisibility(View.GONE);
                        addaddress2.setVisibility(View.VISIBLE);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
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
    protected void onResume() {
        super.onResume();
    }
    @Override
    public void onBackPressed() {
        invalidateOptionsMenu();
        super.onBackPressed();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==2)
        {
            refreshList();
        }
    }
}
