package com.goodwill.wholesale.storefrontqueries;
import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;
import timber.log.Timber;
public  class Query
{
    public static Storefront.QueryRootQuery getCollections(String cursor)
    {
        Storefront.QueryRootQuery.CollectionsArgumentsDefinition definition;
        if(cursor.equals("nocursor"))
        {
            definition=args -> args.first(50);
        }
        else
        {
            definition=args -> args.first(50).after(cursor);
        }

        return Storefront.query(root->root
                .collections(definition, collect->collect.edges(edge->edge.cursor().node(node->node.title().image(Storefront.ImageQuery::originalSrc))).pageInfo(Storefront.PageInfoQuery::hasNextPage))
        );
    }
    private static Storefront.ProductConnectionQueryDefinition getProductDefinition()
    {
        return productdata -> productdata
                .edges(edges -> edges
                        .cursor()
                        .node(node -> node
                                .title()
                                .tags()
                                .handle()
                                .images(img->img.first(10),imag->imag.
                                        edges(imgedge->imgedge
                                                .node(imgnode->imgnode
                                                        .originalSrc()
                                                        .transformedSrc( /*t->t
                                                                .maxWidth(600)
                                                                .maxHeight(600)*/
                                                        )
                                                )
                                        )
                                )
                                .availableForSale()
                                .descriptionHtml()
                                .description()
                                .variants(args -> args
                                        .first(120), variant -> variant
                                        .edges(variantEdgeQuery -> variantEdgeQuery
                                                .node(productVariantQuery -> productVariantQuery
                                                        .priceV2(price -> price.amount().currencyCode())
                                                        .price()
                                                        .selectedOptions(select->select.name().value())
                                                        .compareAtPriceV2(compare->compare.amount().currencyCode())
                                                        .compareAtPrice()
                                                        .image(Storefront.ImageQuery::originalSrc)
                                                        .availableForSale()
                                                )
                                        )
                                )
                                .onlineStoreUrl()
                                .options(op->op.
                                        name()
                                        .values()
                                ))
                )
                .pageInfo(Storefront.PageInfoQuery::hasNextPage
                );
    }
    public static Storefront.QueryRootQuery getProducts(String cat_id, String cursor,Storefront.ProductCollectionSortKeys sortby_key,boolean direction,int number)
    {
        Storefront.CollectionQuery.ProductsArgumentsDefinition definition;
        if(cursor.equals("nocursor"))
        {
            definition=args -> args.first(number).sortKey(sortby_key).reverse(direction);
        }
        else
        {
            definition=args -> args.first(number).after(cursor).sortKey(sortby_key).reverse(direction);
        }
        Storefront.QueryRootQuery productsquery=null;
        if(cat_id.contains("*#*"))
        {
            Timber.tag("Inhandle").i("1");

            productsquery=Storefront.query(root->root
                    .collectionByHandle(cat_id.replace("*#*","").trim(),collect->collect
                            .products(definition,getProductDefinition())))
                    .shop(shop->shop.collectionByHandle(cat_id.replace("*#*","").trim(),collect->collect
                            .products(definition,getProductDefinition())));
        }
        else
        {
            productsquery = Storefront.query(root ->root
                    .node(new ID(cat_id),
                            rootnode -> rootnode.
                                    onCollection(oncollection -> oncollection
                                            .image(image->image
                                                    .originalSrc()
                                                    .transformedSrc(/*tr->tr
                                                            .maxHeight(300)
                                                            .maxWidth(700)*/
                                                    )
                                            )
                                            .products(definition, getProductDefinition()
                                            )
                                    )
                    )
            );
        }
        return productsquery;
    }
    public static Storefront.QueryRootQuery getShopDetails()
    {

        return Storefront.query(root->root
                .shop(shop->shop
                        .moneyFormat()
                        .paymentSettings(pay->pay
                                .currencyCode()
                        .enabledPresentmentCurrencies())
                        .privacyPolicy(privacy->privacy
                                .body()
                                .title()
                                .url())
                        .refundPolicy(refund->refund
                                .body()
                                .url()
                                .title())
                        .termsOfService(term->term
                                .title()
                                .url()
                                .body())
                )
        );
    }
    public  static Storefront.QueryRootQuery getSingleProduct(String product_id)
    {
        Storefront.QueryRootQuery productquery=null;
        if(product_id.contains("*#*"))
        {
            productquery=Storefront.query(root->root
                    .productByHandle(product_id.replace("*#*","").trim(),getProductQuery()));
        }
        else
        {
            productquery = Storefront.query(root ->root
                    .node(new ID(product_id),
                            rootnode -> rootnode.
                                    onProduct(getProductQuery()
                                    )));
        }
        return productquery;
    }

    private static Storefront.ProductQueryDefinition getProductQuery()
    {

        return product->product
                .title()
                .tags()
                .images(img->img.
                        first(10),imag->imag.
                        edges(imgedge->imgedge.
                                node(imgnode->imgnode.originalSrc()
                                        .transformedSrc(tr->tr.maxWidth(600).maxHeight(600))
                                )
                        )
                )
                .availableForSale()
                .descriptionHtml()
                .description()
                .variants(args -> args
                        .first(120), variant -> variant
                        .edges(variantEdgeQuery -> variantEdgeQuery
                                .node(productVariantQuery -> productVariantQuery
                                        .price()
                                        .priceV2(p->p.amount().currencyCode())
                                        .selectedOptions(select->select.name().value())
                                        .compareAtPrice()
                                        .compareAtPriceV2(c->c.amount().currencyCode())
                                        .image(Storefront.ImageQuery::originalSrc)
                                        .availableForSale()
                                )
                        )
                )
                .onlineStoreUrl()
                .options(op->op.
                        name()
                        .values()
                );
    }

    public static Storefront.QueryRootQuery getCustomerDetails(String customeraccestoken)
    {

        return Storefront.query(root->root
                .customer(customeraccestoken,
                        customerQuery->customerQuery
                                .firstName()
                                .lastName()
                                .id()
                )
        );
    }
    public static Storefront.QueryRootQuery getAutoSearchProducts(String cursor, String keyword, Storefront.ProductSortKeys sortby_key,boolean direction)
    {
        Storefront.QueryRootQuery.ProductsArgumentsDefinition definition;
        if(cursor.equals("nocursor"))
        {
            definition=args -> args.query(keyword).first(25).sortKey(sortby_key).reverse(direction);
        }
        else
        {
            definition=args -> args.query(keyword).first(25).after(cursor).sortKey(sortby_key).reverse(direction);
        }
        Storefront.ShopQuery.ProductsArgumentsDefinition shoppro;
        if(cursor.equals("nocursor"))
        {
            shoppro=args -> args.query(keyword).first(25).sortKey(sortby_key).reverse(direction);
        }
        else
        {
            shoppro=args -> args.query(keyword).first(25).after(cursor).sortKey(sortby_key).reverse(direction);
        }
        return Storefront.query(root->root
                .shop(shop->shop
                        .products(shoppro,getProductDefinition()
                        )
                )
                .products(definition,getProductDefinition())
        );
    }
    public static Storefront.QueryRootQuery getAddressList(String accesstoken,String cursor)
    {
        Storefront.CustomerQuery.AddressesArgumentsDefinition definitions;
        if(cursor.equals("nocursor"))
            definitions = args -> args.first(10);
        else
            definitions = args -> args.first(10).after(cursor);

        return Storefront.query(root->root
                .customer(accesstoken,customer->customer
                        .addresses(definitions, address->address
                                .edges(edge->edge
                                        .cursor()
                                        .node(node->node
                                                .firstName()
                                                .lastName()
                                                .company()
                                                .address1()
                                                .address2()
                                                .city()
                                                .country()
                                                .province()
                                                .phone()
                                                .zip()
                                                .formattedArea()
                                        )
                                )
                                .pageInfo(Storefront.PageInfoQuery::hasNextPage))));
    }
    public static Storefront.QueryRootQuery getOrderList(String accesstoken,String cursor)
    {
        Storefront.CustomerQuery.OrdersArgumentsDefinition definition;
        if(cursor.equals("nocursor"))
        {
            definition=args->args.first(10);
        }
        else
        {
            definition=args->args.first(10).after(cursor);
        }

        return Storefront.query(root->root
                .customer(accesstoken,customer->customer
                        .orders(definition, order->order
                                .edges(edge->edge
                                        .cursor()
                                        .node(ordernode->ordernode
                                                .customerUrl()
                                                .statusUrl()
                                                .processedAt()
                                                .orderNumber()
                                                .lineItems(arg->arg.first(150),item->item
                                                        .edges(itemedge->itemedge
                                                                .node(Storefront.OrderLineItemQuery::title
                                                                )
                                                        )
                                                )
                                                .totalPriceV2(tp->tp.amount().currencyCode())
                                                .totalPrice()
                                        )
                                )
                                .pageInfo(Storefront.PageInfoQuery::hasNextPage
                                )
                        )
                )
        );
    }
    public static Storefront.QueryRootQuery getAllProducts( String cursor,Storefront.ProductSortKeys sortby_key,boolean direction,int number)
    {
        Storefront.ShopQuery.ProductsArgumentsDefinition definition;
        if(cursor.equals("nocursor"))
        {
            definition=args -> args.first(number).sortKey(sortby_key).reverse(direction);
        }
        else
        {
            definition=args -> args.first(number).after(cursor).sortKey(sortby_key).reverse(direction);
        }
        Storefront.QueryRootQuery.ProductsArgumentsDefinition shoppro;
        if(cursor.equals("nocursor"))
        {
            shoppro=args -> args.first(number).sortKey(sortby_key).reverse(direction);
        }
        else
        {
            shoppro=args -> args.first(number).after(cursor).sortKey(sortby_key).reverse(direction);
        }
        return Storefront.query(root ->root
                .shop(rootnode -> rootnode
                                .products(definition, getProductDefinition()))
                .products(shoppro,getProductDefinition())

        );
    }
}
