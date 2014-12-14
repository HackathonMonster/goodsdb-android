package com.zeroone_creative.goodsdb.controller.util;

import android.net.Uri;

import java.util.List;

/**
 * Created by shunhosaka on 2014/12/06.
 */
public class UriUtil {

    static private String baseUrl = "138.91.20.246";

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

    static public String getSearchUri(List<String> tags, String type) {
        Uri.Builder builder = getBaseUri();
        //叩く先のAPI
        builder.path("/items/search");
        for(String tag : tags) {
            builder.appendQueryParameter("tags[]", tag);
        }
        builder.appendQueryParameter("type", type);
        return builder.build().toString();
    }

    static public String putGoodsUri(String id) {
        Uri.Builder builder = getBaseUri();
        //叩く先のAPI
        builder.path("/items");
        builder.appendEncodedPath(id);
        return builder.build().toString();
    }

}
