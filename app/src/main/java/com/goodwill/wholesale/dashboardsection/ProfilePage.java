package com.goodwill.wholesale.dashboardsection;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shopify.buy3.GraphCallResult;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.MutationGraphCall;
import com.shopify.buy3.Storefront;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.loginandregistrationsection.Register;
import com.goodwill.wholesale.maincontainer.MainActivity;
import com.goodwill.wholesale.requestsection.ApiClient;
import com.goodwill.wholesale.storagesection.LocalData;
import com.goodwill.wholesale.storefrontqueries.MutationQuery;
import com.goodwill.wholesale.storefrontresponse.AsyncResponse;
import com.goodwill.wholesale.storefrontresponse.Response;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfilePage extends MainActivity
{
    @Nullable @BindView(R.id.MageNative_createaccounttext) TextView MageNative_createaccounttext;
    @Nullable @BindView(R.id.firstname) EditText firstname;
    @Nullable @BindView(R.id.lastname) EditText lastname;
    @Nullable @BindView(R.id.email) EditText email;
    @Nullable @BindView(R.id.password) EditText password;
    @Nullable @BindView(R.id.confirmpassword) EditText confirmpassword;
    @Nullable @BindView(R.id.MageNative_register) Button MageNative_register;
    @Nullable @BindView(R.id.phone) EditText phone;
    LocalData data=null;
    GraphClient client=null;
    String passworddata="";
    private String blockCharacterSet = "@#$_&-+()/*':;!?,.~`|. ~#^|$%&*!0123456789";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ViewGroup content = findViewById(R.id.MageNative_frame_container);
        getLayoutInflater().inflate(R.layout.magenative_updateprofile, content, true);
        ButterKnife.bind(ProfilePage.this);
        MageNative_createaccounttext.setVisibility(View.GONE);
        showbackbutton();
        showTittle(getResources().getString(R.string.updateprofile));
        data=new LocalData(ProfilePage.this);
        client= ApiClient.getGraphClient(ProfilePage.this,true);
        Log.d("phone","profilepage");
        Log.d("phone","name-"+data.getFirstName());
        Log.d("phone","last-"+data.getLastName());
        Log.d("phone","phone-"+data.getphone());
        firstname.setFilters(new InputFilter[] { filter });
        lastname.setFilters(new InputFilter[] { filter });
        MageNative_register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                updateProfile();
            }
        });
        if(data.getPassword()!=null)
        {
            password.setText(data.getPassword());
        }
    }

    private void updateProfile()
    {
        try
        {
            if(firstname.getText().toString().isEmpty() && lastname.getText().toString().isEmpty() && email.getText().toString().isEmpty()&&password.getText().toString().isEmpty()&&confirmpassword.getText().toString().isEmpty()&&phone.getText().toString().isEmpty())
            {
                firstname.setError(getResources().getString(R.string.empty));
                firstname.requestFocus();
                lastname.setError(getResources().getString(R.string.empty));
                email.setError(getResources().getString(R.string.empty));
                password.setError(getResources().getString(R.string.empty));
                confirmpassword.setError(getResources().getString(R.string.empty));
                phone.setError(getResources().getString(R.string.validnumber));
            }
            else
            {
                if (firstname.getText().toString().isEmpty() )
                {
                    firstname.setError(getResources().getString(R.string.empty));
                    firstname.requestFocus();
                }
                else
                {
                    if (lastname.getText().toString().isEmpty())
                    {
                        lastname.setError(getResources().getString(R.string.empty));
                        lastname.requestFocus();
                    }
                    else
                    {
                        if (email.getText().toString().isEmpty())
                        {
                            email.setError(getResources().getString(R.string.empty));
                            email.requestFocus();
                        }
                        else
                        {
                            if(password.getText().toString().isEmpty())
                            {
                                if(data.getPassword()!=null)
                                {
                                    passworddata=data.getPassword();
                                }
                                else
                                {
                                    password.setError(getResources().getString(R.string.empty));
                                    password.requestFocus();
                                }
                            }
                            else
                            {
                                passworddata=password.getText().toString();
                                if(confirmpassword.getText().toString().isEmpty())
                                {
                                    confirmpassword.setError(getResources().getString(R.string.empty));
                                    confirmpassword.requestFocus();
                                }
                                else
                                {
                                    if(passworddata.equals(confirmpassword.getText().toString()))
                                    {
                                        if (phone.getText().toString().isEmpty()){
                                            phone.setError(getResources().getString(R.string.validnumber));
                                            phone.requestFocus();
                                        }else {
                                            update();
                                        }

                                    }
                                    else
                                    {
                                        confirmpassword.setError(getResources().getString(R.string.passwordnotmatch));
                                        confirmpassword.requestFocus();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void update()
    {
        try
        {
            MutationGraphCall call = client.mutateGraph(MutationQuery.updateCustomer(data.getAccessToken(),
                    firstname.getText().toString(),
                    lastname.getText().toString(),
                    email.getText().toString(),passworddata,phone.getText().toString()));
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
                                List<Storefront.UserError> errors = response.getData().getCustomerUpdate().getUserErrors();
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
                                    Toast.makeText(ProfilePage.this,err,Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    Log.d("phone","- processprofile 1");
                                    processProfileData(response);
                                }
                            }
                        });
                    }
                    else
                    {
                        Log.i("ResponseError",""+output.toString());
                    }
                }
            },ProfilePage.this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void processProfileData(GraphResponse<Storefront.Mutation> response)
    {
        try
        {
            Log.d("phone","- processprofile");
            Storefront.Customer customer= response.getData().getCustomerUpdate().getCustomer();
            Storefront.CustomerAccessToken token=response.getData().getCustomerUpdate().getCustomerAccessToken();
            data.saveFirstName(customer.getFirstName());
            data.saveCustomerId(customer.getId().toString());
            data.saveLastName(customer.getLastName());
            Log.d("phone","name- "+data.getFirstName());
            Log.d("phone","phone- "+data.getphone());
            data.savephone(customer.getPhone());

            data.createSession(customer.getEmail(),passworddata);
            data.saveAccesstokenWithExpiry(token.getAccessToken(),token.getExpiresAt().toLocalDateTime().toString());
            Toast.makeText(ProfilePage.this,getResources().getString(R.string.profileupdated),Toast.LENGTH_LONG).show();
            finish();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        firstname.setText(data.getFirstName());
        lastname.setText(data.getLastName());
        email.setText(data.getEmail());
        Log.d("phone",data.getphone());
        phone.setText(data.getphone());
    }
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        return false;
    }
    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };
}
