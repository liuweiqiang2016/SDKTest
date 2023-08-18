package com.sensorsdata.automation.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sensorsdata.analytics.android.sdk.SensorsDataAutoTrackAppViewScreenUrl;
import com.sensorsdata.analytics.android.sdk.SensorsDataFragmentTitle;
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackEvent;
import com.sensorsdata.automation.R;

@SensorsDataFragmentTitle(title = "title_a")
@SensorsDataAutoTrackAppViewScreenUrl(url ="url_a" )
public class AnnotationFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceStateApp) {
        View view = inflater.inflate(R.layout.fragment_annotation, container, false);
        mHandler.sendEmptyMessageDelayed(1,100);
        return view;
    }
    @SensorsDataTrackEvent(eventName = "getInfo",properties = "{\"city\":\"hefei\"}")
    public void testAnnotationTrue(){

    }

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                //注解触发事件
                testAnnotationTrue();
            }
        }
    };

}

