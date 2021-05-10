package com.goodwill.wholesale.maincontainer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goodwill.wholesale.R;
import com.goodwill.wholesale.checkoutsection.CartListing;
import com.goodwill.wholesale.dashboardsection.AccountDashboard;
import com.goodwill.wholesale.datasection.Data;
import com.goodwill.wholesale.homesection.HomePage;
import com.goodwill.wholesale.loginandregistrationsection.Login;
import com.goodwill.wholesale.productlistingsection.AllProductListing;
import com.goodwill.wholesale.productlistingsection.ProductListing;
import com.goodwill.wholesale.productviewsection.ProductView;
import com.goodwill.wholesale.storagesection.LocalData;
import com.goodwill.wholesale.wishlistsection.WishListing;

import org.json.JSONArray;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
/**
 * Created by cedcoss on 27/4/18.
 */
@SuppressWarnings("ALL")
public class FragmentDrawer extends Fragment
{
    @Nullable
    static ActionBarDrawerToggle mDrawerToggle;
    @Nullable
    private DrawerLayout mDrawerLayout;
    @Nullable
    private View containerView;
    @Nullable
    private int accessresult;
    @Nullable
    @BindView(R.id.appversion) TextView appversion;
    @Nullable
    Unbinder unbinder;
    @Nullable
    @BindView(R.id.section2) RelativeLayout section2;
    @Nullable
    @BindView(R.id.sidemenus) LinearLayout menus;
    @Nullable
    @BindView(R.id.section3) RelativeLayout section3;
    @Nullable
    @BindView(R.id.extramenus) LinearLayout extramenus;
    @Nullable
    @BindView(R.id.copyright) TextView copyright;
    @Nullable
    @BindView(R.id.section1) RelativeLayout section1;
    @BindView(R.id.myaccount_section) RelativeLayout myaccount_section;
    @BindView(R.id.language_section) RelativeLayout language_section;
    @BindView(R.id.home_section) RelativeLayout home_section;
    @BindView(R.id.bag_section) RelativeLayout bag_section;
    @BindView(R.id.wish_section) RelativeLayout wish_section;
    @BindView(R.id.invitesection) RelativeLayout invitesection;
    @BindView(R.id.whatsappsection) RelativeLayout whatsappsection;
    @BindView(R.id.MageNative_signin) TextView MageNative_signin;
    @BindView(R.id.privacy) TextView privacy;
    @BindView(R.id.refund) TextView refund;
    @BindView(R.id.terms) TextView terms;
    LocalData localData=null;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View layout=null;
        try
        {
            layout= inflater.inflate(R.layout.fragment_drawer, container, false);
            unbinder = ButterKnife.bind(this, layout);
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String version = pInfo.versionName;
            int versioncode = pInfo.versionCode;
            String app_version = "App Version " + version + "(" + versioncode + ")";
            appversion.setText(app_version);
            copyright.setText(getResources().getString(R.string.copyright)+" "+getResources().getString(R.string.app_name));
            localData=new LocalData(getActivity());
            section1.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(MageNative_signin.getText().toString().equals(getResources().getString(R.string.signin_drawer)))
                    {
                        Intent intent=new Intent(getActivity(), Login.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                        close();
                    }
                }
            });
            myaccount_section.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(localData.isLogin())
                    {
                        Intent intent=new Intent(getActivity(), AccountDashboard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                        close();
                    }
                    else
                    {
                        Intent intent=new Intent(getActivity(), Login.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                        close();
                    }
                }
            });
            language_section.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    ((MainActivity) getActivity()).showlangpop(Data.getLangauages());
                    close();
                }
            });
            home_section.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getActivity(), HomePage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                    close();
                }
            });
            bag_section.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getActivity(), CartListing.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                    close();
                }
            });
            wish_section.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getActivity(), WishListing.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                    close();
                }
            });
            terms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    String parts[]=localData.getTerms().split("#");
                    Intent intent=new Intent(getActivity(), MainContainerWeblink.class);
                    intent.putExtra("name",parts[0]);
                    intent.putExtra("link", parts[1]);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                    close();
                }
            });
            privacy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    String parts[]=localData.getPrivacyPolicy().split("#");
                    Intent intent=new Intent(getActivity(), MainContainerWeblink.class);
                    intent.putExtra("name",parts[0]);
                    intent.putExtra("link", parts[1]);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                    close();
                }
            });
            refund.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    String parts[]=localData.getRefundPolicy().split("#");
                    Intent intent=new Intent(getActivity(), MainContainerWeblink.class);
                    intent.putExtra("name",parts[0]);
                    intent.putExtra("link", parts[1]);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                    close();
                }
            });
            invitesection.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + appPackageName);
                    startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.shareproduct)));
                }
            });

            whatsappsection.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                 String phone = "+97433004420";
                    try{
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+phone));
                        startActivity(intent);
                    }
                    catch(Exception e)
                    {
                        Toast.makeText(getActivity(),"Error/n"+ e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

            });
            createMenus();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return layout;
    }

    private void createMenus()
    {
        try
        {
            if(localData.getMenus()!=null)
            {
                JSONObject object=new JSONObject(localData.getMenus());
                if(object.getBoolean("success"))
                {
                    if(object.has("data"))
                    {
                        JSONArray array=object.getJSONArray("data");
                        if(array.length()>0)
                        {
                            generateMenus(array,menus,extramenus);
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
    void generateMenus(JSONArray data,LinearLayout categoriessection,LinearLayout extramenu)
    {
        try
        {
            JSONObject object=null;
            View catsection=null;
            String typedata="notype";
            TextView catname=null;
            ImageView expand_collapse=null;
            TextView id=null;
            TextView handle=null;
            TextView type=null;
            TextView url=null;
            LinearLayout submenus=null;
            LinearLayout extra_menus=null;
            for (int i = 0; i < data.length(); i++)
            {
               catsection = View.inflate(getActivity(),R.layout.magenative_maincategorysection,null);
               catname=catsection.findViewById(R.id.catname);
               expand_collapse=catsection.findViewById(R.id.expand_collapse);
               id=catsection.findViewById(R.id.id);
               handle=catsection.findViewById(R.id.handle);
               type=catsection.findViewById(R.id.type);
               url=catsection.findViewById(R.id.url);
               submenus=catsection.findViewById(R.id.submenus);
               extra_menus=catsection.findViewById(R.id.extra_menus);
               object=data.getJSONObject(i);
               if(object.has("type"))
               {
                   typedata=object.getString("type");
                   type.setText(typedata);
               }
               if(object.has("title"))
               {
                   String upperString = object.getString("title").substring(0,1).toUpperCase() + object.getString("title").substring(1).toLowerCase();
                   catname.setText(upperString);
               }
               if(object.has("id"))
               {
                    id.setText(object.getString("id"));
               }
               if(object.has("handle"))
               {
                    handle.setText(object.getString("handle"));
               }
               if(object.has("url"))
               {
                   url.setText(object.getString("url"));
               }
               if(object.has("menus"))
               {
                   generateMenus(object.getJSONArray("menus"),submenus,extra_menus);
                   if(submenus.getChildCount()>0)
                   {
                       categoriessection.addView(catsection);
                   }
                   if(extra_menus.getChildCount()>0)
                   {
                       extramenu.addView(catsection);
                   }
               }
               else
               {
                   if(typedata.equals("collection")||typedata.equals("product")||typedata.equals("collection-all")||typedata.equals("product-all"))
                   {
                       expand_collapse.setVisibility(View.GONE);
                       categoriessection.addView(catsection);
                   }
                   else
                   {
                       expand_collapse.setVisibility(View.GONE);
                       extramenu.addView(catsection);
                   }
               }
               LinearLayout finalSubmenus = submenus;
               TextView finalCatname = catname;
               final boolean[] open = {false};
               final boolean[] extraopen = {false};
               LinearLayout finalExtra_menus = extra_menus;
               TextView finalId = id;
               TextView finalType = type;
                TextView finalUrl = url;
                TextView finalHandle = handle;
                catsection.setOnClickListener(new View.OnClickListener()
               {
                   @Override
                   public void onClick(View view)
                   {
                      if(finalSubmenus.getChildCount()>0)
                      {
                        if(open[0])
                        {
                            open[0] =false;
                            finalSubmenus.setVisibility(View.GONE);
                        }
                        else
                        {
                            open[0]=true;
                            finalSubmenus.setVisibility(View.VISIBLE);
                        }
                      }
                      else
                      {
                          if(finalExtra_menus.getChildCount()>0)
                          {
                              if(extraopen[0])
                              {
                                  extraopen[0] =false;
                                  finalExtra_menus.setVisibility(View.GONE);
                              }
                              else
                              {
                                  extraopen[0]=true;
                                  finalExtra_menus.setVisibility(View.VISIBLE);
                              }
                          }
                          else
                          {
                              if(finalType.getText().toString().equals("collection"))
                              {
                                  try
                                  {   String s1;
                                      if(finalId.getText().toString().isEmpty())
                                      {
                                          s1= finalHandle.getText().toString()+"*#*";
                                      }
                                      else
                                      {
                                          s1 = "gid://shopify/Collection/" + finalId.getText().toString();
                                          byte[] data = Base64.encode(s1.getBytes(), Base64.DEFAULT);
                                          s1 = new String(data, "UTF-8").trim();
                                      }
                                      Intent intent=new Intent(getActivity(), ProductListing.class);
                                      intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                      intent.putExtra("cat_id",s1 );
                                      intent.putExtra("cat_name",finalCatname.getText().toString());
                                      startActivity(intent);
                                      getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                                      close();
                                  }
                                  catch (Exception e)
                                  {
                                      e.printStackTrace();
                                  }
                              }
                              else
                              {
                                  if(finalType.getText().toString().equals("product"))
                                  {
                                      try
                                      {
                                          String s1;
                                          if(finalId.getText().toString().isEmpty())
                                          {
                                              s1= finalHandle.getText().toString()+"*#*";
                                          }
                                          else
                                          {
                                              s1 = "gid://shopify/Product/" + finalId.getText().toString();
                                              byte[] data = Base64.encode(s1.getBytes(), Base64.DEFAULT);
                                              s1 = new String(data, "UTF-8").trim();
                                          }
                                          Intent intent=new Intent(getActivity(), ProductView.class);
                                          intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                          intent.putExtra("id",s1 );
                                          startActivity(intent);
                                          getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                                          close();
                                      }
                                      catch (Exception e)
                                      {
                                          e.printStackTrace();
                                      }
                                  }
                                  else
                                  {
                                      if(finalType.getText().toString().equals("product-all"))
                                      {
                                          try
                                          {
                                              Intent intent=new Intent(getActivity(), AllProductListing.class);
                                              intent.putExtra("cat_name",finalCatname.getText().toString());
                                              intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                              startActivity(intent);
                                              getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                                              close();
                                          }
                                          catch (Exception e)
                                          {
                                              e.printStackTrace();
                                          }
                                      }
                                      else
                                      {
                                          if(finalType.getText().toString().equals("collection-all"))
                                          {
                                              try
                                              {
                                                  Intent intent=new Intent(getActivity(), HomePage.class);
                                                  intent.putExtra("collection-all",finalCatname.getText().toString());
                                                  intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                  startActivity(intent);
                                                  getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                                                  close();
                                              }
                                              catch (Exception e)
                                              {
                                                  e.printStackTrace();
                                              }
                                          }
                                          else
                                          {
                                              if(finalType.getText().toString().equals("page")||finalType.getText().toString().equals("blog"))
                                              {
                                                  try
                                                  {
                                                      Intent intent=new Intent(getActivity(), MainContainerWeblink.class);
                                                      intent.putExtra("name",finalCatname.getText().toString());
                                                      intent.putExtra("link", finalUrl.getText().toString());
                                                      intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                      startActivity(intent);
                                                      getActivity().overridePendingTransition(R.anim.magenative_slide_in, R.anim.magenative_slide_out);
                                                      close();
                                                  }
                                                  catch (Exception e)
                                                  {
                                                      e.printStackTrace();
                                                  }
                                              }
                                              else
                                              {
                                                  Toast.makeText(getActivity(), finalCatname.getText().toString(),Toast.LENGTH_LONG).show();
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
            if(menus.getChildCount()>0)
            {
                section2.setVisibility(View.VISIBLE);
            }
            if(extramenus.getChildCount()>0)
            {
                section3.setVisibility(View.VISIBLE);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }
    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar)
    {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                mDrawerToggle.syncState();
            }
        });
    }

    public void open()
    {
        mDrawerLayout.openDrawer(containerView);
    }

    private void close() {
        mDrawerLayout.closeDrawer(containerView);
    }

    @Override
    public void onResume()
    {
        if(localData.getFirstName()!=null)
        {
            String username="Hey! " +localData.getFirstName();
            MageNative_signin.setText(username);
        }
        super.onResume();
    }

    public interface FragmentDrawerListener
    {
        void onDrawerItemSelected(View view, int position);
    }
}
