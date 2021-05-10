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
package com.goodwill.wholesale.adaptersection;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shopify.buy3.Storefront;
import com.goodwill.wholesale.R;
import com.goodwill.wholesale.addresssection.AddAddress;
import com.goodwill.wholesale.addresssection.AddressList;
import com.goodwill.wholesale.storagesection.LocalData;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class addressListAdapter extends BaseAdapter {
    @Nullable
    private static LayoutInflater Ced_inflater = null;
    private final List<Storefront.MailingAddressEdge> Ced_value;
    private final WeakReference<Activity> Ced_activity;
    @Nullable @BindView(R.id.address_id) TextView address_id;
    @Nullable @BindView(R.id.firstname) TextView firstname;
    @Nullable @BindView(R.id.lastname) TextView lastname;
    @Nullable @BindView(R.id.company) TextView company;
    @Nullable @BindView(R.id.address1) TextView address1;
    @Nullable @BindView(R.id.address2) TextView address2;
    @Nullable @BindView(R.id.city) TextView city;
    @Nullable @BindView(R.id.state) TextView state;
    @Nullable @BindView(R.id.country) TextView country;
    @Nullable @BindView(R.id.pincode) TextView pincode;
    @Nullable @BindView(R.id.phone) TextView phone;
    LocalData data=null;
    public addressListAdapter(Activity a, List<Storefront.MailingAddressEdge> d)
    {
        Ced_activity =new WeakReference<Activity>(a);
        Ced_value = d;
        Ced_inflater = (LayoutInflater) Ced_activity.get().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        data=new LocalData(Ced_activity.get());
    }

    public int getCount() {
        return Ced_value.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    @Nullable
    @Override
    public View getView(final int position, @Nullable View convertView, ViewGroup parent) {
        try {
            View vi;
            if (convertView == null) {
                vi = Objects.requireNonNull(Ced_inflater).inflate(R.layout.address_item, parent, false);
            } else {
                vi = convertView;
            }
            ButterKnife.bind(this, vi);
            final TextView address_id=vi.findViewById(R.id.address_id);
            address_id.setText(Ced_value.get(position).getNode().getId().toString());
            firstname.setText(Ced_value.get(position).getNode().getFirstName());
            lastname.setText(Ced_value.get(position).getNode().getLastName());
            company.setText(Ced_value.get(position).getNode().getCompany());
            address1.setText(Ced_value.get(position).getNode().getAddress1());
            address2.setText(Ced_value.get(position).getNode().getAddress2());
            city.setText(Ced_value.get(position).getNode().getCity());
            state.setText(Ced_value.get(position).getNode().getProvince());
            country.setText(Ced_value.get(position).getNode().getCountry());
            pincode.setText("-"+Ced_value.get(position).getNode().getZip());
            phone.setText(Ced_value.get(position).getNode().getPhone());
            ImageView delete=vi.findViewById(R.id.delete);
            ImageView edit=vi.findViewById(R.id.edit);
            delete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                  AddressList adddress=  (AddressList)Ced_activity.get();
                  adddress.deleteAddress(address_id.getText().toString(),data.getAccessToken());
                }
            });
            edit.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Storefront.MailingAddress address=Ced_value.get(position).getNode();
                    Intent intent=new Intent(Ced_activity.get(), AddAddress.class);
                    intent.putExtra("object", (Serializable) address);
                    intent.putExtra("name", Ced_activity.get().getResources().getString(R.string.updateaddress));
                    Ced_activity.get().startActivityForResult(intent,2);
                }
            });

            return vi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}