package com.sensorsdata.automation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.analytics.android.sdk.core.business.exposure.SAExposureConfig;
import com.sensorsdata.analytics.android.sdk.core.business.exposure.SAExposureData;
import com.sensorsdata.automation.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SimpleAdapter extends BaseAdapter {

    private List<String> list;
    private Context mContext;

    public SimpleAdapter(Context context, List<String> list) {
        this.mContext =context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder = null;
        if (convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.simple_item,parent,false);
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.item_tv);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.textView.setText(list.get(position));


        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("item_pos",2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        //设置曝光 view 唯一标记位
        SensorsDataAPI.sharedInstance().setExposureIdentifier(holder.textView,String.valueOf(position));
         //对指定 item 添加曝光监听
        if (position==2){
            SAExposureConfig exposureConfig=new SAExposureConfig(1.0f,1,true);
            exposureConfig.setDelayTime(0);
            SAExposureData exposureData=new SAExposureData("exposure_item",jsonObject,String.valueOf(position),exposureConfig);
            SensorsDataAPI.sharedInstance().addExposureView(holder.textView,exposureData);
        }

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
        TextView textView;

    }
}
