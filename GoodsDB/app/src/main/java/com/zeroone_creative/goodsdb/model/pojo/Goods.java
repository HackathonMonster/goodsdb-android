package com.zeroone_creative.goodsdb.model.pojo;

import java.util.List;

/**
 * Created by shunhosaka on 2014/12/07.
 */
public class Goods {
    public int id;
    public String name;
    public String created_at;
    public String updated_at;
    public List<Tag> tags;
    public List<Picture> pictures;
}
