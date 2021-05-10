package com.goodwill.wholesale.loginandregistrationsection;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Login extends MainActivity {
    static GraphClient apiclient;
    String origin = "normal";
    String weburl = "normal";
    @Nullable
    @BindView(R.id.MageNative_signupwithustext)
    TextView MageNative_signupwithustext;
    @Nullable
    @BindView(R.id.MageNative_forgotPassword)
    TextView MageNative_forgotPassword;
    @Nullable
    @BindView(R.id.MageNative_usr_password)
    EditText MageNative_usr_password;
    @Nullable
    @BindView(R.id.MageNative_user_name)
    EditText MageNative_user_name;
    @Nullable
    @BindView(R.id.MageNative_Login)
    Button MageNative_Login;
    @Nullable
    @BindView(R.id.scrollable)
    ScrollView scrollable;
    LocalData localData = null;

    private static boolean isValidEmail(String target) {
        boolean valid = false;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (target.matches(emailPattern)) {
            valid = true;
        }
        return valid;
    }

    public static Login getInstance() {
        return new Login();
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup content = findViewById(R.id.MageNative_frame_container);
        getLayoutInflater().inflate(R.layout.magenative_login, content, true);
        showbackbutton();
        showTittle(getResources().getString(R.string.login));
        ButterKnife.bind(Login.this);
        localData = new LocalData(Login.this);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Glide.with(Login.this)
                            .asBitmap()
                            .load(localData.getLoginback())
                            .into(new SimpleTarget<Bitmap>(width, height) {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    Drawable drawable = new BitmapDrawable(getResources(), resource);
                                  //  scrollable.setBackground(drawable);
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (getIntent().getStringExtra("link") != null) {
            origin = getIntent().getStringExtra("checkout");
            weburl = getIntent().getStringExtra("link");
        }
        apiclient = ApiClient.getGraphClient(Login.this, true);
        MageNative_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MageNative_user_name.getText().toString().isEmpty()) {
                    MageNative_user_name.setError(getResources().getString(R.string.empty));
                    MageNative_user_name.requestFocus();
                } else {
                    if (!(isValidEmail(MageNative_user_name.getText().toString()))) {
                        MageNative_user_name.setError(getResources().getString(R.string.invalidemail));
                        MageNative_user_name.requestFocus();
                    } else {
                        if (MageNative_usr_password.getText().toString().isEmpty()) {
                            MageNative_usr_password.setError(getResources().getString(R.string.empty));
                            MageNative_usr_password.requestFocus();
                        } else {
                            LoginUser(MageNative_user_name.getText().toString(), MageNative_usr_password.getText().toString(), origin, weburl);

                        }
                    }
                }
            }
        });
        MageNative_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPasswordPopUp();
            }
        });
        MageNative_signupwithustext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                intent.putExtra("checkout", origin);
                intent.putExtra("link", weburl);
                startActivity(intent);
                overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
            }
        });
    }

    private void forgotPasswordPopUp() {
        try {
            final Dialog listDialog = new Dialog(Login.this, R.style.PauseDialog);
            ((ViewGroup) ((ViewGroup) Objects.requireNonNull(listDialog.getWindow()).getDecorView()).getChildAt(0)).getChildAt(1).setBackgroundColor(Login.this.getResources().getColor(R.color.AppTheme));
            listDialog.setTitle(Login.this.getResources().getString(R.string.forgotpass));
            LayoutInflater li = (LayoutInflater) Login.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View v = Objects.requireNonNull(li).inflate(R.layout.enter_email, null, false);
            final EditText email = v.findViewById(R.id.email);
            TextView send = v.findViewById(R.id.conti);
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (email.getText().toString().isEmpty()) {
                        email.setError(Login.this.getResources().getString(R.string.empty));
                        email.requestFocus();
                    } else {
                        if (isValidEmail(email.getText().toString())) {
                            listDialog.cancel();
                            requestForgotPass(email.getText().toString());
                        } else {
                            email.setError(Login.this.getResources().getString(R.string.invalidemail));
                            email.requestFocus();
                        }
                    }
                }
            });
            listDialog.setContentView(v);
            listDialog.setCancelable(true);
            listDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestForgotPass(String s) {
        try {
            MutationGraphCall call = apiclient.mutateGraph(MutationQuery.recoverCustomer(s));
            Response.getMutationGraphQLResponse(call, new AsyncResponse() {
                @Override
                public void finalOutput(@NonNull Object output, @NonNull boolean error) {
                    if (error) {
                        GraphResponse<Storefront.Mutation> response = ((GraphCallResult.Success<Storefront.Mutation>) output).getResponse();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<Storefront.UserError> errors = response.getData().getCustomerRecover().getUserErrors();
                                if (errors.size() > 0) {
                                    Iterator iterator = errors.iterator();
                                    String err = "";
                                    while (iterator.hasNext()) {
                                        Storefront.UserError error = (Storefront.UserError) iterator.next();
                                        err += error.getMessage();
                                    }
                                    Toast.makeText(Login.this, err, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(Login.this, getResources().getString(R.string.pleasecheckyourmail), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Log.i("ResponseError", "" + output.toString());
                    }
                }
            }, Login.this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        return false;
    }

}
