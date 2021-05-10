package com.goodwill.wholesale.loginandregistrationsection;
import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
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
import java.util.Iterator;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
@SuppressWarnings("ALL")
public class Register extends MainActivity
{
    @Nullable @BindView(R.id.firstname) EditText firstname;
    @Nullable @BindView(R.id.lastname) EditText lastname;
    @Nullable @BindView(R.id.email) EditText email;
    @Nullable @BindView(R.id.password) EditText password;
    @Nullable @BindView(R.id.confirmpassword) EditText confirmpassword;
    @Nullable @BindView(R.id.phone) EditText phone;
    @Nullable @BindView(R.id.MageNative_register) Button MageNative_register;
    GraphClient client=null;
    LocalData data=null;
    String origin="normal";
    String weburl="normal";
    private String blockCharacterSet = "@#$_&-+()/*':;!?,.~`|. ~#^|$%&*!0123456789";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ViewGroup content = findViewById(R.id.MageNative_frame_container);
        getLayoutInflater().inflate(R.layout.magenative_registration, content, true);
        ButterKnife.bind(Register.this);
        showbackbutton();
        showTittle(getResources().getString(R.string.CreateAccount));
        client= ApiClient.getGraphClient(Register.this,true);
        data=new LocalData(Register.this);
        origin=getIntent().getStringExtra("checkout");
        weburl=getIntent().getStringExtra("link");
        firstname.setFilters(new InputFilter[] { filter });
        lastname.setFilters(new InputFilter[] { filter });
        MageNative_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        if(email.getText().toString().isEmpty())
                        {
                            email.setError(getResources().getString(R.string.empty));
                            email.requestFocus();
                        }
                        else
                        {
                            if(!(isValidEmail(email.getText().toString())))
                            {
                                email.setError(getResources().getString(R.string.invalidemail));
                                email.requestFocus();
                            }
                            else
                            {
                                if(phone.getText().toString().isEmpty())
                                {
                                    phone.setError(getResources().getString(R.string.validnumber));
                                    phone.requestFocus();
                                }
                                else
                                {
                                    if(password.getText().toString().isEmpty())
                                    {
                                        password.setError(getResources().getString(R.string.empty));
                                        password.requestFocus();
                                    }
                                    else
                                    {
                                        if(confirmpassword.getText().toString().isEmpty())
                                        {
                                            confirmpassword.setError(getResources().getString(R.string.empty));
                                            confirmpassword.requestFocus();
                                        }
                                        else
                                        {
                                            if(password.getText().toString().equals(confirmpassword.getText().toString()))
                                            {
                                                createAccount(firstname.getText().toString(),lastname.getText().toString(),email.getText().toString(),password.getText().toString(),phone.getText().toString());

                                            }
                                            else
                                            {
                                                password.setError(getResources().getString(R.string.passwordnotmatch));
                                                confirmpassword.setError(getResources().getString(R.string.passwordnotmatch));
                                                password.requestFocus();
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

    private void createAccount(String firstname, String lastname, String email, String password,String phone)
    {
        try
        {
            MutationGraphCall call = client.mutateGraph(MutationQuery.createCustomer(firstname,lastname,email,password,phone));
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
                                List<Storefront.UserError> errors = response.getData().getCustomerCreate().getUserErrors();
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
                                    Log.d("processRegisterData-","1");
                                    Toast.makeText(Register.this,err,Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    Log.d("processRegisterData-","2");
                                    processRegisterData(response);
                                }
                            }
                        });
                    }
                    else
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(Register.this,""+output.toString(),Toast.LENGTH_LONG).show();
                                Log.i("ResponseError",""+output.toString());
                            }
                        });
                    }
                }
            },Register.this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void processRegisterData(GraphResponse<Storefront.Mutation> response)
    {
        try
        {
            Log.d("processRegisterData-","3");
            Storefront.Customer customer=response.getData().getCustomerCreate().getCustomer();
            data.saveFirstName(customer.getFirstName());
            data.saveLastName(customer.getLastName());
            data.savephone(customer.getPhone());
            data.saveCustomerId(customer.getId().toString());
            LoginUser(email.getText().toString(),password.getText().toString(),origin,weburl);
            finish();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    @Override
    protected void onResume() {
       super.onResume();
    }
    private static boolean isValidEmail(String target)
    {
        boolean valid = false;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (target.matches(emailPattern)) {
            valid = true;
        }
        return valid;
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
