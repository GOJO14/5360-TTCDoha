package com.goodwill.wholesale.storefrontqueries;
import android.content.Context;
import android.util.Log;

import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;
import com.goodwill.wholesale.checkoutsection.CheckoutLineItems;
import java.lang.ref.WeakReference;
public class MutationQuery
{
    static WeakReference<Context> contextWeakReference;
    public  static Storefront.CheckoutQueryDefinition getCheckoutDefinition( String cursor)
    {
        Storefront.CheckoutQuery.LineItemsArgumentsDefinition definition;
        if(cursor.equals("nocursor"))
        {
            definition=args -> args.first(20);
        }
        else
        {
            definition=args -> args.first(20).after(cursor);
        }

        return chek -> chek
                .webUrl()
                .lineItems(definition,edge->edge
                        .edges(lineedge->lineedge
                                .cursor()
                                .node(linenode->linenode
                                        .customAttributes(_queryBuilder -> _queryBuilder.key().value()) // discount volume options
                                        .title()
                                        .quantity()
                                        .variant(linevariant->linevariant
                                                .product(_queryBuilder -> _queryBuilder.title()) // discount volume options
                                                .availableForSale()
                                                .price()
                                                /*********************************discount volume options***********************************/
                                                .product(conn -> conn
                                                        .collections(arg->arg.
                                                                first(10),collection -> collection
                                                                .edges(collEdge -> collEdge
                                                                        .node(collNode -> collNode.title())
                                                                )
                                                        )
                                                )
                                                /*********************************discount volume options***********************************/
                                                .priceV2(p->p.currencyCode().amount())
                                                .compareAtPrice()
                                                .compareAtPriceV2(c->c.amount().amount())
                                                .image(img->img
                                                        .transformedSrc(trans->trans
                                                                .maxHeight(200)
                                                                .maxWidth(200)
                                                        )
                                                )
                                                .selectedOptions(select->select
                                                        .name()
                                                        .value()
                                                )
                                        )
                                )
                        )
                        .pageInfo(Storefront.PageInfoQuery::hasNextPage
                        )
                )
                .paymentDueV2(pd->pd.amount().currencyCode())
                .paymentDue()
                .subtotalPrice()
                .subtotalPriceV2(st->st.currencyCode().amount())
                .taxesIncluded()
                .taxExempt()
                .totalTax()
                .totalTaxV2(tt->tt.amount().currencyCode())
                .totalPrice()
                .totalPriceV2(tp->tp.currencyCode().amount());
    }
    public static   Storefront.MutationQuery createCheckout(Context context,String cursor,String discountcode, boolean couponcode,String checkout_id)
    {
        contextWeakReference=new WeakReference<Context>(context);
        CheckoutLineItems data=new CheckoutLineItems(contextWeakReference.get());
        Storefront.CheckoutCreateInput input = new Storefront.CheckoutCreateInput();
        input.setLineItems(data.getLineItems());
        Storefront.MutationQuery createcheckoutmutation = null;
        if(couponcode)
        {
            createcheckoutmutation= Storefront.mutation(root->root
                    .checkoutDiscountCodeApply(discountcode,new ID(checkout_id),
                            checkoutquery -> checkoutquery
                                    .checkout(getCheckoutDefinition(cursor))
                                    .userErrors(_queryBuilder -> _queryBuilder
                                            .message()
                                            .field()
                                    )
                    )
                    .checkoutDiscountCodeApplyV2(discountcode,new ID(checkout_id),checkout->checkout
                            .checkout(getCheckoutDefinition(cursor))
                            .checkoutUserErrors(checkerror->checkerror.message().field()))
            );
        }
        else
        {
            createcheckoutmutation = Storefront.mutation(root -> root
                    .checkoutCreate(input,
                            checkoutquery -> checkoutquery
                                    .checkout(getCheckoutDefinition(cursor))
                                    .userErrors(_queryBuilder -> _queryBuilder
                                            .message()
                                            .field()
                                    ).checkoutUserErrors(checkerror->checkerror.field().message())
                    )
            );

        }

       return createcheckoutmutation;
    }

    public static Storefront.MutationQuery createCustomer(String firstname,String lastname, String email,String password,String phone)
    {
        Storefront.CustomerCreateInput customer = new Storefront.CustomerCreateInput(email, password);
        customer.setFirstName(firstname);
        customer.setLastName(lastname);
        customer.setPhone(phone);

        return Storefront.mutation(root -> root
                .customerCreate(customer,
                        customerquery -> customerquery
                                .customer(customerdata -> customerdata
                                        .firstName()
                                        .lastName()
                                        .displayName()
                                        .email()
                                        .createdAt()
                                        .id()
                                )
                                .userErrors(_queryBuilder -> _queryBuilder
                                        .message()
                                        .field()
                                )
                        .customerUserErrors(cue->cue.field().message())
        ));
    }
    public static Storefront.CustomerAccessTokenCreatePayloadQueryDefinition getCustomerAccessToken()
    {
        return customerquery -> customerquery
                .customerAccessToken(customerdata -> customerdata
                        .accessToken()
                        .expiresAt())
                .userErrors(_queryBuilder -> _queryBuilder
                .message()
                .field())
                .customerUserErrors(cue->cue.message().field());
    }
    public static Storefront.MutationQuery createCustomerAccessToken( String email,String password)
    {
        Storefront.CustomerAccessTokenCreateInput customer = new Storefront.CustomerAccessTokenCreateInput(email, password);

        return Storefront.mutation(root -> root
                .customerAccessTokenCreate(customer,getCustomerAccessToken()
                ));
    }
    public static Storefront.MutationQuery recoverCustomer(String email)
    {


        return Storefront.mutation(root -> root
                .customerRecover(email,
                        recover->recover
                                .userErrors(Storefront.UserErrorQuery::message)
                        .customerUserErrors(cue->cue.field().message())
                ));
    }
    public static Storefront.MutationQuery renewToken(String token)
    {
        return Storefront.mutation(root -> root
                .customerAccessTokenRenew(token,
                        access->access
                                .customerAccessToken(r->r
                                        .accessToken()
                                        .expiresAt()
                                )
                                .userErrors(error->error
                                        .message()
                                        .field()
                                )
                )
        );
    }
    public static Storefront.MutationQuery updateCustomer(String token,String firstname,String lastname,String email,String password,String phone)
    {
        Storefront.CustomerUpdateInput update=new Storefront.CustomerUpdateInput();
        update.setEmail(email);
        update.setFirstName(firstname);
        update.setLastName(lastname);
        update.setPassword(password);
        update.setPhone(phone);
        return Storefront.mutation(root->root
                .customerUpdate(token,update,
                        customer->customer
                                .customer(c->c
                                         .firstName()
                                         .lastName()
                                         .email()
                                        .id()
                                         .phone())
                                 .customerAccessToken(access->access
                                         .expiresAt()
                                         .accessToken())
                                  .userErrors(error->error
                                          .message()
                                          .field())
                        .customerUserErrors(cue->cue.message().field())
                ));
    }
    public static Storefront.MutationQuery deleteCustomerAddress(String token,String address_id)
    {
        return Storefront.mutation(root->root
                .customerAddressDelete(new ID(address_id),token,
                customer->customer
                        .userErrors(Storefront.UserErrorQuery::message)
                .customerUserErrors(cue->cue.field().message())));
    }
    public static Storefront.MutationQuery addCustomerAddress(String operation,String address_id,String token, String firstname,String lastname,String company,String address1,String address2,String city,String country,String province,String zip,String phone)
    {
        Storefront.MailingAddressInput input=new Storefront.MailingAddressInput();
        input.setFirstName(firstname);
        input.setLastName(lastname);
        input.setCompany(company);
        input.setAddress1(address1);
        input.setAddress2(address2);
        input.setCity(city);
        input.setCountry(country);
        input.setProvince(province);
        input.setZip(zip);
        input.setPhone(phone);
        Storefront.MutationQuery mutation=null;
        if(operation.equals("update"))
        {
            mutation=Storefront.mutation(root->root
                    .customerAddressUpdate(token
                            ,new ID(address_id)
                            ,input
                            ,address->address
                                    .userErrors(Storefront.UserErrorQuery::message)
                    .customerUserErrors(cue->cue.message().field())));
        }
        if(operation.equals("add"))
        {
            mutation=Storefront.mutation(root->root
                    .customerAddressCreate(token
                            ,input
                            ,address->address
                                    .userErrors(Storefront.UserErrorQuery::message)
                    .customerUserErrors(cue->cue.field().message())));
        }
        return mutation;
    }

}
