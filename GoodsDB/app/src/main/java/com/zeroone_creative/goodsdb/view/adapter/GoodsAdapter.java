package com.zeroone_creative.goodsdb.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zeroone_creative.goodsdb.R;
import com.zeroone_creative.goodsdb.model.pojo.Goods;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shunhosaka on 2014/12/04.
 */
public class GoodsAdapter extends BaseAdapter {

    private List<Goods> mContent = new ArrayList<Goods>();
    private LayoutInflater mInflater;
    private Context mContext;

    public GoodsAdapter(Context context, ArrayList<Goods> content) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = context;
        this.mContent = content;
    }

    @Override
    public int getCount() {
        return mContent.size();
    }

    @Override
    public Goods getItem(int position) {
        return mContent.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null) {
            convertView = mInflater.inflate(R.layout.item_goods, null);
            holder = new ViewHolder();
            holder.nameTextView = (TextView) convertView.findViewById(R.id.item_goods_textview_name);
            holder.imageView = (ImageView) convertView.findViewById(R.id.item_goods_imageview);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        Goods goods = mContent.get(position);
        if(goods.name == null || goods.name.equals("")) {
            holder.nameTextView.setText("no title");
        } else {
            holder.nameTextView.setText(goods.name);
        }
        if(goods.pictures.size() > 0) {
            Picasso.with(mContext).load(goods.pictures.get(0).imageUrl).error(R.drawable.img_detail_noimg).resize(300,300).centerCrop().into(holder.imageView);
        }
        return convertView;
    }

    private class ViewHolder{
        TextView nameTextView;
        ImageView imageView;
    }

    public void updateContent(List<Goods> content) {
        this.mContent = content;
        this.notifyDataSetChanged();

    }

}
