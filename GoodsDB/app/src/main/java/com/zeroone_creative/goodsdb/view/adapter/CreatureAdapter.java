package com.zeroone_creative.goodsdb.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.zeroone_creative.goodsdb.R;
import com.zeroone_creative.goodsdb.model.pojo.Creature;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shunhosaka on 2014/12/04.
 */
public class CreatureAdapter extends BaseAdapter {

    private List<Creature> mContent = new ArrayList<Creature>();
    private LayoutInflater mInflater;
    private Context mContext;

    public CreatureAdapter(Context context, ArrayList<Creature> content) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = context;
        this.mContent = content;
    }

    @Override
    public int getCount() {
        return mContent.size();
    }

    @Override
    public Creature getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.item_creatureadapter, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.item_creature_imageview);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        Creature creature = mContent.get(position);
        Picasso.with(mContext).load(creature.image).into(holder.imageView);

        return convertView;
    }

    private class ViewHolder{
        ImageView imageView;
    }

    public void updateContent(List<Creature> content) {
        this.mContent = content;
        this.notifyDataSetChanged();
    }

}
