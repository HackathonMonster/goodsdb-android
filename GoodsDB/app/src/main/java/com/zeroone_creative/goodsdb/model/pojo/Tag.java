package com.zeroone_creative.goodsdb.model.pojo;

/**
 * Created by shunhosaka on 2014/12/14.
 */
public class Tag {
    public String id;
    public String name;
    public String created_at;
    public String updated_at;

    private static String[] systemTags = {"public", "fun", "drop", "sale", "buy", "want"};

    public static boolean isSystem(String tag) {
        for(int i=0; i<systemTags.length; i++) {
            if(tag.contains(systemTags[i])) {
                return true;
            }
        }
        return false;
    }
}
