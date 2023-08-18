package com.sensorsdata.automation.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sensorsdata.automation.R;
import com.sensorsdata.automation.model.PropertyModel;
import com.sensorsdata.automation.utils.Util;


import java.util.List;

public class InfoAdapter extends BaseAdapter {

    private List<PropertyModel> list;
    private Context mContext;

    public InfoAdapter(Context context) {
        this.mContext =context;
        this.list = Util.getPropertyModels();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder = null;
        if (convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);
            holder = new ViewHolder();
            holder.tv_key = convertView.findViewById(R.id.tv_key);
            holder.tv_value = convertView.findViewById(R.id.tv_value);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.tv_key.setText(list.get(position).getKey());
        holder.tv_value.setText(list.get(position).getValue().toString());
        return convertView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return null;
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    static class ViewHolder {
        TextView tv_key;
        TextView tv_value;
    }
}
