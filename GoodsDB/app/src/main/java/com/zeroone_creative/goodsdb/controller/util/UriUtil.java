package com.zeroone_creative.goodsdb.controller.util;

import android.net.Uri;

/**
 * Created by shunhosaka on 2014/12/06.
 */
public class UriUtil {

    static private String baseUrl = "";

    static private Uri.Builder getBaseUri(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.encodedAuthority(baseUrl);
        return builder;
    }

    static public String getGoodsUri() {
        Uri.Builder builder = getBaseUri();
        //叩く先のAPI
        builder.path("/items");
        return builder.build().toString();
    }

    static public String postGoodsUri() {
        Uri.Builder builder = getBaseUri();
        //叩く先のAPI
        builder.path("/items");
        return builder.build().toString();
    }

    static public String postLoginUri() {
        Uri.Builder builder = getBaseUri();
        //叩く先のAPI
        builder.path("/login");
        return builder.build().toString();
    }

}
