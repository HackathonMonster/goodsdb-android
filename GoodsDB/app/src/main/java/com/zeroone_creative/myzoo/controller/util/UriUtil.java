package com.zeroone_creative.myzoo.controller.util;

import android.net.Uri;

/**
 * Created by shunhosaka on 2014/12/06.
 */
public class UriUtil {

    static private String baseUrl = "myzoo.azurewebsites.net";

    static private Uri.Builder getBaseUri(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.encodedAuthority(baseUrl);
        return builder;
    }

    static public String getRegistUri() {
        Uri.Builder builder = getBaseUri();
        //叩く先のAPI
        builder.path("/login");
        return builder.build().toString();
    }

    static public String getCreateUri() {
        Uri.Builder builder = getBaseUri();
        //叩く先のAPI
        builder.path("/gallery");
        return builder.build().toString();
    }

    static public String getGalleryUri(String id) {
        Uri.Builder builder = getBaseUri();
        //叩く先のAPI
        builder.path("/gallery");
        //builder.appendPath("/");
        builder.appendPath(id);
        return builder.build().toString();
    }


}
