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
package com.goodwill.wholesale.addresssection;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.shopify.buy3.GraphCallResult;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.MutationGraphCall;
import com.shopify.buy3.Storefront;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.maincontainer.MainActivity;
import com.goodwill.wholesale.requestsection.ApiClient;
import com.goodwill.wholesale.storagesection.LocalData;
import com.goodwill.wholesale.storefrontqueries.MutationQuery;
import com.goodwill.wholesale.storefrontresponse.AsyncResponse;
import com.goodwill.wholesale.storefrontresponse.Response;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
public class AddAddress extends MainActivity
{
    @Nullable @BindView(R.id.firstname) EditText firstname;
    @Nullable @BindView(R.id.lastname) EditText lastname;
    @Nullable @BindView(R.id.company) EditText company;
    @Nullable@BindView(R.id.address1) EditText address1;
    @Nullable @BindView(R.id.address2) EditText address2;
    @Nullable @BindView(R.id.city) EditText city;
    @Nullable @BindView(R.id.country) Spinner country;
    @Nullable@BindView(R.id.provinces) Spinner provinces;
    @Nullable @BindView(R.id.statetext) EditText statetext;
    @Nullable@BindView(R.id.zip) EditText zip;
    @Nullable @BindView(R.id.phone) EditText phone;
    @Nullable @BindView(R.id.submit) Button submit;
    boolean state=true;
    GraphClient client;
    LocalData data;
    boolean update=false;
    Storefront.MailingAddress address=null;
    ArrayList<String> countries=null;
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ViewGroup content = (ViewGroup) findViewById(R.id.MageNative_frame_container);
        getLayoutInflater().inflate(R.layout.magenative_add_address, content, true);
        ButterKnife.bind(AddAddress.this);
        showbackbutton();
        showTittle(getIntent().getStringExtra("name"));
        if(getIntent().getSerializableExtra("object")!=null)
        {
            address= (Storefront.MailingAddress) getIntent().getSerializableExtra("object");
            update=true;
            firstname.setText(address.getFirstName());
            lastname.setText(address.getLastName());
            company.setText(address.getCompany());
            address1.setText(address.getAddress1());
            address2.setText(address.getAddress2());
            city.setText(address.getCity());
            zip.setText(address.getZip());
            phone.setText(address.getPhone());
        }
        client= ApiClient.getGraphClient(AddAddress.this,true);
        data=new LocalData(AddAddress.this);
        countries=getCountryList();
        if(update)
        {
            countries.remove(address.getCountry());
            countries.add(0,address.getCountry());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.text,countries);
        country.setAdapter(dataAdapter);
        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                if(getStateList(country.getSelectedItem().toString())!=null)
                {
                   state=true;
                   provinces.setVisibility(View.VISIBLE);
                   statetext.setVisibility(View.GONE);
                   ArrayList<String> states= getStateList(country.getSelectedItem().toString());
                   if(update)
                   {
                       states.remove(address.getProvince());
                       states.add(address.getProvince());
                   }
                   ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AddAddress.this, R.layout.text,states);
                   provinces.setAdapter(dataAdapter);
                }
                else
                {
                   state=false;
                   if(update)
                    {
                        statetext.setText(address.getProvince());
                    }
                   provinces.setVisibility(View.GONE);
                   statetext.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(firstname.getText().toString().isEmpty())
                {
                    firstname.setError(getResources().getString(R.string.empty));
                    firstname.requestFocus();
                }
                else
                {
                    if(lastname.getText().toString().isEmpty())
                    {
                        lastname.setError(getResources().getString(R.string.empty));
                        lastname.requestFocus();
                    }
                    else
                    {
                        if(address1.getText().toString().isEmpty())
                        {
                            address1.setError(getResources().getString(R.string.empty));
                            address1.requestFocus();
                        }
                        else
                        {
                            if(address2.getText().toString().isEmpty())
                            {
                                address2.setError(getResources().getString(R.string.empty));
                                address2.requestFocus();
                            }
                            else
                            {
                                if(city.getText().toString().isEmpty())
                                {
                                    city.setError(getResources().getString(R.string.empty));
                                    city.requestFocus();
                                }
                                else
                                {
                                    if(zip.getText().toString().isEmpty())
                                    {
                                        zip.setError(getResources().getString(R.string.empty));
                                        zip.requestFocus();
                                    }
                                    else
                                    {
                                        if(phone.getText().toString().isEmpty())
                                        {
                                            phone.setError(getResources().getString(R.string.empty));
                                            phone.requestFocus();
                                        }
                                        else
                                        {
                                            if(!(state))
                                            {
                                                statetext.setError(getResources().getString(R.string.empty));
                                                statetext.requestFocus();
                                            }
                                            else
                                            {
                                                createAddress();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void createAddress()
    {
        try
        {
            String statedata;
            if(state)
            {
                statedata=provinces.getSelectedItem().toString();
            }
            else
            {
                statedata=statetext.getText().toString();
            }
            MutationGraphCall call=null;
            if(update)
            {
                call= client.mutateGraph(MutationQuery
                        .addCustomerAddress("update",address.getId().toString(),data.getAccessToken(),
                                firstname.getText().toString(),
                                lastname.getText().toString(),
                                company.getText().toString(),
                                address1.getText().toString(),
                                address2.getText().toString(),
                                city.getText().toString(),
                                country.getSelectedItem().toString(),
                                statedata,
                                zip.getText().toString(),
                                phone.getText().toString()));
            }
            else
            {
                call= client.mutateGraph(MutationQuery
                        .addCustomerAddress("add","noaddres_id",data.getAccessToken(),
                                firstname.getText().toString(),
                                lastname.getText().toString(),
                                company.getText().toString(),
                                address1.getText().toString(),
                                address2.getText().toString(),
                                city.getText().toString(),
                                country.getSelectedItem().toString(),
                                statedata,
                                zip.getText().toString(),
                                phone.getText().toString()));
            }
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
                                List<Storefront.UserError> errors;
                                if(update)
                                {
                                    errors = response.getData().getCustomerAddressUpdate().getUserErrors();
                                }
                                else
                                {
                                    errors = response.getData().getCustomerAddressCreate().getUserErrors();
                                }
                                if(errors.size() > 0)
                                {
                                    Iterator iterator = errors.iterator();
                                    String err = "";
                                    while (iterator.hasNext())
                                    {
                                        Storefront.UserError error = (Storefront.UserError) iterator.next();
                                        err += error.getMessage();
                                    }
                                    Toast.makeText(AddAddress.this,err,Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    if(update)
                                    {
                                        Toast.makeText(AddAddress.this,getResources().getString(R.string.succesfullyupdated),Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(AddAddress.this,getResources().getString(R.string.succesfullyadded),Toast.LENGTH_LONG).show();
                                    }
                                    setResult(2);
                                    finish();
                                }
                            }
                        });
                    }
                    else
                    {
                        Log.i("ResponseError",""+output.toString());
                    }
                }
            },AddAddress.this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu)
    {
        return false;
    }
}

