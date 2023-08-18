package com.sensorsdata.automation.utils;

import static com.sensorsdata.automation.greendao.TestCaseDao.Properties.IsPass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.analytics.android.sdk.SensorsDataDynamicSuperProperties;
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackEventCallBack;
import com.sensorsdata.analytics.android.sdk.SensorsNetworkType;
import com.sensorsdata.analytics.android.sdk.core.business.exposure.SAExposureData;
import com.sensorsdata.analytics.android.sdk.deeplink.SensorsDataDeepLinkCallback;
import com.sensorsdata.automation.MyApplication;
import com.sensorsdata.automation.activity.ExposureActivity;
import com.sensorsdata.automation.activity.MainActivity;
import com.sensorsdata.automation.greendao.TestCase;
import com.sensorsdata.automation.greendao.TestCaseDao;
import com.sensorsdata.automation.model.DataModel;
import com.sensorsdata.automation.model.PropertyModel;
import com.sensorsdata.automation.model.PropertyType;

import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dalvik.system.DexFile;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Util {


    //用例集合的包目录
    public static String CASES_PACKAGENAME="com.sensorsdata.automation.cases";
    //应用 SP 名称
    private static String SP_NAME="app_sp";
    //分支名称
    private static String BRANCH_NAME="branch";
    //启动时间戳
    private static String START_MILLIS="millis";
    //测试报告名称
    private static String REPORT_NAME="测试报告.html";
    //企业微信机器人 key,接口文档见：https://developer.work.weixin.qq.com/document/path/91770
    private static String ROBOT_KEY="企业微信机器人key";

    public static String TAG="SA.S";
    private static String[] keys={"$app_name","$device_id","$model","$brand","$os_version","$app_version","$timezone_offset","$manufacturer","$screen_height","$os","$screen_width","$lib_version","$lib","$app_id","$wifi","$network_type","$is_first_day","dynamic","$event_session_id"};
    private static String[] start_keys={"$resume_from_background","$is_first_time","$screen_name","$title"};
    private static String[] end_keys={"event_duration","$screen_name","$title"};
    private static String[] view_keys={"$url","$screen_name","$title"};
    private static String[] click_keys={"$element_type","$element_id","$screen_name","$title"};
    private static String[] install_keys={"$ios_install_source"};
    private static String[] h5_keys={"$url","$latest_traffic_source_type","$latest_search_keyword","$latest_referrer"};
    private static String[] native_keys={"$latitude","$longitude","$geo_coordinate_system","$screen_orientation"};
    private static String[] ab_keys={"$abtest_experiment_id","$abtest_experiment_group_id"};
    private static String[] config_keys={"$app_remote_config"};
    private static String[] launch_keys={"$deeplink_url"};
    private static String[] launchResult_keys={"$deeplink_url","$event_duration"};
    private static String[] leave_keys={"$screen_name","$title","$url","event_duration"};
    private static String[] display_keys={"$sf_plan_id","$sf_plan_strategy_id","$sf_msg_id","$sf_plan_type","$sf_channel_service_name","$sf_channel_category","$sf_platform_tag","$sf_lib_version","$sf_succeed"};
    private static String[] popClick_keys={"$sf_plan_id","$sf_plan_strategy_id","$sf_msg_id","$sf_plan_type","$sf_channel_service_name","$sf_channel_category","$sf_platform_tag","$sf_lib_version","$sf_succeed","$sf_msg_element_action","$sf_msg_element_type","$sf_msg_action_id"};


    /**
     * 根据包名获取当前包下所有类的名称
     * @param context context
     * @param packageName 包名
     * @return 类名称
     */
    public static List<String> getClassName(Context context,String packageName){
        List<String>classNameList=new ArrayList<String >();
        try {
            DexFile df = new DexFile(context.getPackageCodePath());//通过DexFile查找当前的APK中可执行文件
            Enumeration<String> enumeration = df.entries();//获取df中的元素  这里包含了所有可执行的类名 该类名包含了包名+类名的方式
            while (enumeration.hasMoreElements()) {//遍历
                String className = (String) enumeration.nextElement();
                //带有匿名内部类的类，编译后会生成带有 $ 的.class，例如 AutoTrackCase$1,需要屏蔽该类型的 .class
                if (className.contains(packageName)&&!className.contains("$")) {//在当前所有可执行的类里面查找包含有该包名的所有类
                    Log.e(TAG, "className: "+className);
                    classNameList.add(className);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  classNameList;
    }

    /**
     * 通过反射机制，根据类名获取对应类的对象
     * @param className 类名
     * @return  对象
     */
    public static Object newInstance(String className){
        Class clz = null;
        Object obj=null;
        try {
            clz = Class.forName(className);
            obj= clz.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return obj;
    }

    //获取当前时间
    public static String getTime(){
        long currentTime=System.currentTimeMillis();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=new Date(currentTime);
        String time=formatter.format(date);
        return time;
    }

    /**
     * @return 获取预置属性模型
     */
    public static List<PropertyModel> getPropertyModels(){

        TestCaseDao mCaseDao= MyApplication.getDaoInstant().getTestCaseDao();
        TestCase testCase=new TestCase();
        testCase.setName("校验预置属性是否完整");
        testCase.setApi("getPresetProperties()");
        testCase.setExpect("预置属性完整");

        List<PropertyModel> list=new ArrayList<>();
        try{
            JSONObject jsonObject= JSON.parseObject(SensorsDataAPI.sharedInstance().getPresetProperties().toString());
            Log.e(TAG, "getPropertyModels: "+jsonObject.toString());
            list.add(new PropertyModel(PropertyType.STRING,"$app_version",jsonObject.getString("$app_version")));
            list.add(new PropertyModel(PropertyType.STRING,"$lib",jsonObject.getString("$lib")));
            list.add(new PropertyModel(PropertyType.STRING,"$lib_version",jsonObject.getString("$lib_version")));
            list.add(new PropertyModel(PropertyType.STRING,"$manufacturer",jsonObject.getString("$manufacturer")));
            list.add(new PropertyModel(PropertyType.STRING,"$model",jsonObject.getString("$model")));
            list.add(new PropertyModel(PropertyType.STRING,"$brand",jsonObject.getString("$brand")));
            list.add(new PropertyModel(PropertyType.STRING,"$os",jsonObject.getString("$os")));
            list.add(new PropertyModel(PropertyType.STRING,"$os_version",jsonObject.getString("$os_version")));
            list.add(new PropertyModel(PropertyType.NUMBER,"$screen_height",jsonObject.getIntValue("$screen_height")));
            list.add(new PropertyModel(PropertyType.NUMBER,"$screen_width",jsonObject.getIntValue("$screen_width")));
            list.add(new PropertyModel(PropertyType.BOOL,"$wifi",jsonObject.getBooleanValue("$wifi")));
            list.add(new PropertyModel(PropertyType.STRING,"$network_type",jsonObject.getString("$network_type")));
            list.add(new PropertyModel(PropertyType.STRING,"$app_id",jsonObject.getString("$app_id")));
            list.add(new PropertyModel(PropertyType.NUMBER,"$timezone_offset",jsonObject.getIntValue("$timezone_offset")));
            list.add(new PropertyModel(PropertyType.STRING,"$device_id",jsonObject.getString("$device_id")));
            list.add(new PropertyModel(PropertyType.STRING,"$app_name",jsonObject.getString("$app_name")));
            list.add(new PropertyModel(PropertyType.BOOL,"$is_first_day",jsonObject.getBooleanValue("$is_first_day")));
            list.add(new PropertyModel(PropertyType.STRING,"$carrier",jsonObject.getString("$carrier")));
        }catch (Exception e){
            Log.e(TAG, "getPropertyModels: "+e.getMessage() );
            testCase.setIsPass(false);
            testCase.setFailReason("敏感属性不完整，解析出现异常");
        }
        testCase.setIsPass(true);
        mCaseDao.insert(testCase);
        return list;
    }

    /**
     * @param rawJson 数据库中查询的原始数据
     * @return dataModel 封装为 dataModel 对象（仅适用明文数据）
     */
    public static DataModel dataToModel(org.json.JSONObject rawJson){
        DataModel dataModel=new DataModel();
        try {
            //原始 JSONObject 包含 _id,created_at,data,我们需要的数据均在 data 中;data 中存在校验位需要去除，否则无法转换为 JSONObject
            JSONObject jsonObject= JSON.parseObject(Util.parseData(rawJson.get("data").toString()));
            String type=jsonObject.getString("type");
            JSONObject lib=jsonObject.getJSONObject("lib");
            JSONObject properties= jsonObject.getJSONObject("properties");
            dataModel.setType(type);
            dataModel.setLib(lib);
            dataModel.setProperties(properties);
            //如果是事件类型数据，需要设置 eventName、identities
            if (type.contains("track")){
                String eventName=jsonObject.getString("event");
                JSONObject identities= jsonObject.getJSONObject("identities");
                dataModel.setEventName(eventName);
                dataModel.setIdentities(identities);
            }
            //如果是 profile_x 类型数据需要设置 identities
            if (type.contains("profile")){
                JSONObject identities=jsonObject.getJSONObject("identities");
                dataModel.setIdentities(identities);
            }
            //如果是 item_x 类型数据需要设置 item_type、item_id
            if (type.contains("item")){
                String item_type=jsonObject.getString("item_type");
                String item_id=jsonObject.getString("item_id");
                dataModel.setItemType(item_type);
                dataModel.setItemId(item_id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // JSON 解析失败，代表数据有问题，失败数据插入到 case 中，待处理
        }
        return dataModel;
    }


    /**
     * @param jsonArray 要校验的一系列原始数据
     */
    public static void assertJsonArray(JSONArray jsonArray){
        if (jsonArray!=null){
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    //原始数据转换为 model 类型
                    DataModel model= dataToModel(jsonArray.getJSONObject(i));
                    //校验事件类型的 model 通用属性是否完整
                    assertProperties(model);
                    //校验事件类型的 model 自定义属性是否正常
                    assertPropertyPlugin(model);
                    //校验敏感属性
                    assertLimitProperty(model);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //通用属性校验
    public static void assertProperties(DataModel dataModel){
        //如果是事件类型数据
        if (dataModel.getType().contains("track")){
            //构建数据库信息，根据校验情况决定是否插入数据
            TestCaseDao mCaseDao= MyApplication.getDaoInstant().getTestCaseDao();
            TestCase testCase=new TestCase();
            testCase.setName("事件通用属性校验");
            testCase.setDescribe("检验事件的属性是否出现异常");
            testCase.setExpect("事件属性准确");

            String eventName=dataModel.getEventName();
            JSONObject properties= dataModel.getProperties();
            for(String key:keys){
                //如果当前事件缺少预置属性key
                if (!properties.containsKey(key)&&!(key.equals("$is_first_day")&&(eventName.equals("$AppInstall")||eventName.equals("$SignUp")))){
                    //将当前事件写入到日志中
                    Log.e(TAG,"当前事件："+eventName+" 校验失败,缺少属性:"+key);
                    //丢失的数据入库存储
                    testCase.setActuality("事件:"+eventName+" 属性丢失,丢失属性为:"+key);
                    testCase.setFailReason("事件属性丢失");
                    testCase.setIsPass(false);
                    testCase.setRemark(properties.toString());
                    Log.e(TAG, "assertProperties: "+properties.toString() );
                    mCaseDao.insert(testCase);
                    return;
                }
            }
            //对特殊事件进一步校验
            String[] special_keys=null;
            switch (eventName){
                case "$AppStart":
                    special_keys=start_keys;
                    break;
                case "$AppViewScreen":
                    special_keys=view_keys;
                    break;
                case "$AppClick":
                    special_keys=click_keys;
                    break;
                case "$AppEnd":
                    special_keys=end_keys;
                    break;
                case "$AppInstall":
                    special_keys=install_keys;
                    break;
                case "$ABTestTrigger":
                    special_keys=ab_keys;
                    break;
                case "$AppRemoteConfigChanged":
                    special_keys=config_keys;
                    break;
                case "$AppDeeplinkLaunch":
                    special_keys=launch_keys;
                    break;
                case "$AppDeeplinkMatchedResult":
                    special_keys=launchResult_keys;
                    break;
                case "$AppPageLeave":
                    special_keys=leave_keys;
                    break;
                case "$PlanPopupDisplay":
                    special_keys=display_keys;
                    break;
                case "$PlanPopupClick":
                    special_keys=popClick_keys;
                    break;

            }
            //符合以上特殊事件，对上述事件进一步校验
            if (special_keys!=null){
                for(String key:special_keys){
                    //如果当前事件缺少预置属性key
                    if (!properties.containsKey(key)){
                        //将当前事件写入到日志中
                        Log.e(TAG,"当前事件："+eventName+" 校验失败,缺少属性:"+key);
                        //丢失的数据入库存储
                        testCase.setActuality("事件:"+eventName+" 属性丢失,丢失属性为:"+key);
                        testCase.setFailReason("事件属性丢失");
                        testCase.setIsPass(false);
                        mCaseDao.insert(testCase);
                        return;
                    }
                }
            }
            //如果是h5打通事件需要进一步校验
            if (properties.getString("$lib").equals("js")){
                //H5 特有属性校验
                for(String key:h5_keys){
                    //如果当前事件缺少预置属性key
                    if (!properties.containsKey(key)){
                        //将当前事件写入到日志中
                        Log.e(TAG,"当前事件："+eventName+" 校验失败,缺少属性:"+key);
                        //丢失的数据入库存储
                        testCase.setActuality("事件:"+eventName+" 属性丢失,丢失属性为:"+key);
                        testCase.setFailReason("事件属性丢失");
                        testCase.setIsPass(false);
                        mCaseDao.insert(testCase);
                        return;
                    }
                }
                //对 native 特有属性校验，如果 H5 事件包含，则异常
                for(String key:native_keys){
                    //如果当前事件缺少预置属性key
                    if (properties.containsKey(key)){
                        //将当前事件写入到日志中
                        Log.e(TAG,"当前事件："+eventName+" 校验失败,多余属性:"+key);
                        //丢失的数据入库存储
                        testCase.setActuality("事件:"+eventName+" 属性多余,多余属性为:"+key);
                        testCase.setFailReason("事件属性多余");
                        testCase.setIsPass(false);
                        mCaseDao.insert(testCase);
                        return;
                    }
                }

            }else{
                //$AppDataTrackingClose特殊处理,由于该事件可能无法携带经纬度信息
                if (!eventName.equals("$AppDataTrackingClose")){
                    //对经纬度信息进行校验
                    for(String key:native_keys){
                        //如果当前事件缺少经纬度及屏幕方向
                        if (!properties.containsKey(key)){
                            //将当前事件写入到日志中
                            Log.e(TAG,"当前事件："+eventName+" 校验失败,缺少属性:"+key);
                            //丢失的数据入库存储
                            testCase.setActuality("事件:"+eventName+" 属性丢失,丢失属性为:"+key);
                            testCase.setFailReason("事件属性丢失");
                            testCase.setIsPass(false);
                            mCaseDao.insert(testCase);
                            return;
                        }
                    }
                }
            }
            Log.d(TAG, "事件: "+eventName+" 属性校验通过!");
            testCase.setIsPass(true);
            testCase.setRemark("事件通用属性校验通过");
            mCaseDao.insert(testCase);
        }
    }

    /**
     * @param dataModel 事件类型数据
     */
    public static void assertPropertyPlugin(DataModel dataModel){
        //仅对事件类型数据校验
        if (!dataModel.getType().contains("track")){
            return;
        }
        TestCaseDao mCaseDao= MyApplication.getDaoInstant().getTestCaseDao();
        TestCase testCase=new TestCase();
        testCase.setName("自定义属性插件校验");
        testCase.setDescribe("自定义属性插件设置是否生效");
        testCase.setExpect("track 类型事件属性为:test_property，其他类型事件属性为:autoTest");
        JSONObject properties= dataModel.getProperties();
        String app_name=properties.getString("$app_name");
        testCase.setActuality("$app_name:"+app_name);
        //自定义属性插件，仅对 track 类型数据进行处理
        if (dataModel.getType().equals("track")){
            if (!app_name.equals("test_property")){
                testCase.setFailReason("自定义属性插件处理异常");
                testCase.setIsPass(false);
                mCaseDao.insert(testCase);
            }else {
                testCase.setIsPass(true);
                mCaseDao.insert(testCase);
            }
        }else {
            if (!app_name.equals("autoTest")){
                testCase.setFailReason("自定义属性插件处理异常");
                testCase.setIsPass(false);
                mCaseDao.insert(testCase);
            }else {
                testCase.setIsPass(true);
                mCaseDao.insert(testCase);
            }
        }
    }

    /**
     * @param dataModel 事件类型数据
     */
    public static void assertLimitProperty(DataModel dataModel){
        //仅对事件类型数据校验
        if (!dataModel.getType().contains("track")){
            return;
        }
        TestCaseDao mCaseDao= MyApplication.getDaoInstant().getTestCaseDao();
        TestCase testCase=new TestCase();
        testCase.setName("校验敏感属性限制");
        testCase.setDescribe("校验敏感属性限制是否生效");
        testCase.setExpect("敏感属性限制生效");
        String carrier=dataModel.getProperties().getString("$carrier");
        testCase.setActuality("$carrier:"+carrier);
        //验证敏感属性采集是否正常
        if (!carrier.equals("合肥电信")){
            testCase.setFailReason("敏感属性设置不生效");
            testCase.setIsPass(false);
            mCaseDao.insert(testCase);
        }else {
            testCase.setIsPass(true);
            mCaseDao.insert(testCase);
        }
    }

    /**
     * @param keyData events 表中的原始 data 数据
     * @return 去除原始 data 数据中的校验位
     */
    public static String parseData(String keyData) {
        try {
            if (TextUtils.isEmpty(keyData)) return "";
            int index = keyData.lastIndexOf("\t");
            if (index > -1) {
                String crc = keyData.substring(index).replaceFirst("\t", "");
                keyData = keyData.substring(0, index);
                if (TextUtils.isEmpty(keyData) || TextUtils.isEmpty(crc)
                        || !crc.equals(String.valueOf(keyData.hashCode()))) {
                    return "";
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return keyData;
    }

    /**
     * 更新分支名称
     *
     * @param context context
     * @param name 分支名称
     */
    public static void UpdateBranchName(Context context,String name){
        SharedPreferences sp=context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(BRANCH_NAME,name);
        editor.apply();
    }

    public static String getBranchURL(Context context){
        SharedPreferences sp=context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String name=sp.getString(BRANCH_NAME,"master");
        String URL="";
        if (name.equals("master")||name.equals("")){
            URL="https://gitlab.sensorsdata.cn/sensors-analytics/sdk/sa-sdk-android/-/tree/master";
        }else {
            URL="https://gitlab.sensorsdata.cn/sensors-analytics/sdk/sa-sdk-android/-/tree/"+name;
        }
        return URL;
    }

    /**
     * 设置 App 启动时间戳
     * @param context context
     */
    public static void setStartTime(Context context){
        SharedPreferences sp=context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putLong(START_MILLIS,System.currentTimeMillis());
        editor.apply();
    }
    /**
     * 获取 App 启动时间戳
     * @param context context
     */
    public static long getStartTime(Context context){
        SharedPreferences sp=context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getLong(START_MILLIS,-1);
    }

    public static void writeReportHtml(Context context){
        TestCaseDao mCaseDao= MyApplication.getDaoInstant().getTestCaseDao();
//        List<TestCase> list=mCaseDao.loadAll();
        List<TestCase> list=mCaseDao.queryBuilder().orderAsc(IsPass).list();
        if (list==null||list.size()==0){
            //如果测试用例 db 中无数据
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msgtype","text");
            JSONObject object = new JSONObject();
            //文本消息内容
            object.put("content","Android SDK 自动化测试执行失败，失败原因:测试用例 DB 为 null 或为空!");
            jsonObject.put("text",object);
            seedMsg(jsonObject);
            return;
        }
        try {
            File file = new File(Environment.getExternalStorageDirectory(),REPORT_NAME);
            //若文件存在，先删除再创建新的
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            //构造 html 文件，先构造头部的固定部分,为了美观，换行展示
            StringBuffer stringBuffer=new StringBuffer();
            //头部
            stringBuffer.append("<html>" +
                    "<head>" +
                    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
                    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                    "</head>" +
                    "<body>");
            //标题一:整体结论
            stringBuffer.append("<h2>Android SDK 自动化测试报告</h2>" +
                    "<h3>整体结论</h3>");
            //生成时间
            stringBuffer.append("<p>生成时间 : "+getTime()+"</p>");
            //对应分支地址
            String url=getBranchURL(context);
            stringBuffer.append("<p>分支地址 : <a href="+url+">"+url+"</a></p>");
            //整体结论
            stringBuffer.append("<p>"+getConclusion()+"</p>");
            //标题二:详细测试用例信息
            stringBuffer.append("<h3>详细信息</h3>");
            //构建表格，单独的 td 设置固定宽度，并在 table 中设置 style 为 break-all 强制换行，使固定宽度生效
            stringBuffer.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"10\" style=\"word-break:break-all\"");
            //表头
            stringBuffer.append(
                    "<tr>" +
                            "<th>编号</th>" +
                            "<th>执行时间</th>" +
                            "<th>用例名称</th>" +
                            "<th>接口信息</th>" +
                            "<th>描述信息</th>" +
                            "<th>预期结果</th>" +
                            "<th>实际结果</th>" +
                            "<th>是否通过</th>" +
                            "<th>失败原因</th>" +
                            "<th>备注信息</th>" +
                            "</tr>");
            //表格内容
            for (TestCase testCase:list){
                stringBuffer.append("<tr>" +
                        "<td>"+testCase.getId()+ "</td>" +
                        "<td>"+testCase.getTime()+"</td>" +
                        "<td style=\"width: 300px;\" >"+testCase.getName()+"</td>" +
                        "<td>"+testCase.getApi()+"</td>" +
                        "<td>"+testCase.getDescribe()+"</td>" +
                        "<td style=\"width: 200px;\" >"+testCase.getExpect()+"</td>" +
                        "<td style=\"width: 400px;\" >"+testCase.getActuality()+"</td>" +
                        "<td>"+testCase.getIsPass()+"</td>" +
                        "<td>"+testCase.getFailReason()+"</td>" +
                        "<td style=\"width: 400px;\" >"+testCase.getRemark()+"</td>" +
                        "</tr>");
            }
            stringBuffer.append("</table>");
            stringBuffer.append("</body></html>");
            String s=stringBuffer.toString();
            FileOutputStream outputStream = new FileOutputStream(file, true); //追加数据
            outputStream.write(s.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //导出文件到电脑，若文件已存在则直接覆盖
        // adb pull /sdcard/测试报告.html  /Users/weiqiangliu/Downloads

    }

    public static void seedMsg(JSONObject jsonObject){
        new Thread(){
            @Override
            public void run() {
                MediaType JSON = MediaType.get("application/json; charset=utf-8");
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
                Request request = new Request.Builder()
                        .url("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key="+ROBOT_KEY)
                        .post(body)
                        .build();
                try {
                    Log.d(TAG, "request: "+request);
                    Response response=client.newCall(request).execute();
                    Log.d(TAG, "response: "+response.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    /**
     * 发布自动化测试报告
     */
    public static void postReport(Context context){
        new Thread(){
            @Override
            public void run() {
                File file = new File(Environment.getExternalStorageDirectory(),REPORT_NAME);
                //若文件不存在，停止执行
                if (!file.exists()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("msgtype","text");
                        org.json.JSONObject object = new org.json.JSONObject();
                        //文本消息内容
                        object.put("content","Android SDK 自动化测试执行失败，失败原因:未找到报告文件!");
                        jsonObject.put("text",object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    seedMsg(jsonObject);
                    return;
                }
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                        .build();
                Request request = new Request.Builder()
                        .addHeader("Content-Type", "multipart/form-data; boundary")
                        .url("https://qyapi.weixin.qq.com/cgi-bin/webhook/upload_media?type=file&key="+ROBOT_KEY)
                        .post(requestBody)
                        .build();
                try {
                    Log.d(TAG, "request: "+request);
                    Response response=client.newCall(request).execute();
                    Log.d(TAG, "response: "+response);
                    // 请求结果处理
                    byte[] datas = response.body().bytes();
                    //得到 media_id
                    String respMsg = new String(datas);
                    Log.d(TAG, "respMsg: ==============================="+respMsg);
                    //发布测试完成通知消息
                    seedConclusion(context);
                    //发布文件消息
                    JSONObject result = JSONObject.parseObject(respMsg);
                    String media_id = result.getString("media_id");
                    JSONObject fileJson = new JSONObject();
                    fileJson.put("media_id", media_id);
                    JSONObject reqBody = new JSONObject();
                    reqBody.put("msgtype", "file");
                    reqBody.put("file", fileJson);
                    seedMsg(reqBody);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    /**
     * 清空测试用例 DB
     */
    public static void clearCaseDB(){
        TestCaseDao mCaseDao= MyApplication.getDaoInstant().getTestCaseDao();
        mCaseDao.deleteAll();
    }

    /**
     * 获取测试用例 DB 的数据总量
     * @return 测试用例 DB 的数据总量
     */
    public static long getCaseSize(){
        TestCaseDao mCaseDao= MyApplication.getDaoInstant().getTestCaseDao();
        return mCaseDao.count();
    }

    /** 获取测试用例通过的个数
     * @return 测试用例通过的个数
     */
    public static long getCaseSuccessSize(){
        TestCaseDao mCaseDao= MyApplication.getDaoInstant().getTestCaseDao();
        QueryBuilder<TestCase> qb=mCaseDao.queryBuilder().where(IsPass.eq("true"));
        if (qb==null){
            return 0;
        }else {
            return qb.count();
        }
    }

    /**
     * 获取测试整体结论信息
     * @return 整体结论信息
     */
    public static String getConclusion(){
        long total=getCaseSize(),s_count=getCaseSuccessSize();
        DecimalFormat df=new java.text.DecimalFormat("##.##%");//传入格式模板
        String str=df.format((float)s_count/(float)total);
        return "本次回归共计执行用例 "+total+ " 个，其中通过 "+s_count+" 个，未通过 "+(total-s_count)+" 个，通过率为："+str;
    }

    /**
     * 获取测试整体结论信息
     */
    public static void seedConclusion(Context context){
        long total=getCaseSize(),s_count=getCaseSuccessSize();
//        long total=100,s_count=99;
        DecimalFormat df=new java.text.DecimalFormat("##.##%");//传入格式模板
        String str=df.format((float)s_count/(float)total);
        String msg="###### Android SDK 自动化回归测试推送 \n";
        SharedPreferences sp=context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String name=sp.getString(BRANCH_NAME,"master");
        String URL="";
        if (name.equals("master")||name.equals("")){
            URL="https://gitlab.xxx.cn/-/tree/master";
        }else {
            URL="https://gitlab.xxx.cn/-/tree/"+name;
        }
        //如果成功数小于总数，视为未通过
        if (s_count<total){
            msg=msg+"测试结果:<font color=\"warning\">失败</font>\n" +
                    "用例总数:<font color=\"info\">"+total+"</font>\n" +
                    "通过数:<font color=\"info\">"+s_count+"</font>\n" +
                    "失败数:<font color=\"warning\">"+(total-s_count)+"</font>\n" +
                    "通过率:<font color=\"warning\">"+str+"</font>\n" +
                    "测试分支:<font color=\"info\">"+"["+name+"]("+URL+")</font>\n"+
                    "执行明细:<font color=\"info\">[pipelines](https://gitlab.xxx.cn/-/pipelines)</font>";
        }else {
            msg=msg+"测试结果:<font color=\"info\">成功</font>\n" +
                    "用例总数:<font color=\"info\">"+total+"</font>\n" +
                    "通过数:<font color=\"info\">"+s_count+"</font>\n" +
                    "失败数:<font color=\"warning\">"+(total-s_count)+"</font>\n" +
                    "通过率:<font color=\"info\">"+str+"</font>\n" +
                    "测试分支:<font color=\"info\">"+"["+name+"]("+URL+")</font>\n"+
                    "执行明细:<font color=\"info\">[pipelines](https://gitlab.xxx.cn/-/pipelines)</font>";
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msgtype","markdown");
        JSONObject object = new JSONObject();
        //文本消息内容
        object.put("content",msg);
        jsonObject.put("markdown",object);
        seedMsg(jsonObject);
    }

    /**
     * 将属性 model 转换为 SDK 可识别的 json 属性
     * 为什么要这么做？主要是为方便校验 JSONObject 类型的自定义属性
     * @param model 属性 model
     * @return 转换为 SDK 可识别的 JSONObject 属性
     */
    public static org.json.JSONObject makeJSONObject(PropertyModel model){
        org.json.JSONObject jsonObject=new org.json.JSONObject();
        try{
            jsonObject.put(model.getKey(), model.getValue());
            //存储无需区分类型，校验或许需要，待定
//            PropertyType type=model.getType();
//            switch (type){
//                case NUMBER:
//                    jsonObject.put(model.getKey(),(int)model.getValue());
//                    break;
//                case STRING:
//                    jsonObject.put(model.getKey(),model.getValue().toString());
//                    break;
//                case BOOL:
//                    jsonObject.put(model.getKey(),(boolean)model.getValue());
//                    break;
//                case LIST:
//                    jsonObject.put(model.getKey(), model.getValue());
//                    break;
//            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 将属性 model 转换为 SDK 可识别的 json 属性
     * 为什么要这么做？主要是为方便校验 JSONObject 类型的自定义属性
     * @param models 属性组 models
     * @return 转换为 SDK 可识别的 JSONObject 属性
     */
    public static org.json.JSONObject makeJSONObject(List<PropertyModel> models){
        org.json.JSONObject jsonObject=new org.json.JSONObject();
        try{
            for (PropertyModel model:models){
                jsonObject.put(model.getKey(), model.getValue());
            }


        }catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    /**
     * 校验单个自定义属性
     * @param propertyModel 单个自定义属性
     * @param dataModel SDK 数据构造的事件
     * @return 校验是否通过
     */
    public static boolean assertProperty(PropertyModel propertyModel,DataModel dataModel){
        //获取事件的所有数据
        JSONObject properties= dataModel.getProperties();
        String key=propertyModel.getKey();
        Object value=propertyModel.getValue();
        PropertyType type=propertyModel.getType();
        //判断事件属性中是否包含该自定义属性 key
        boolean isHaveKey=properties.containsKey(key);
        if (isHaveKey){
            switch (type){
                case NUMBER:
                    //比较自定义属性与事件属性的 int 类型
                    if ((int)value!=properties.getIntValue(key)){
                        return false;
                    }
                    break;
                case STRING:
                    //比较自定义属性与事件属性的 string 类型
                    if (!value.toString().equals(properties.getString(key))){
                        return false;
                    }
                    break;
                case BOOL:
                    //比较自定义属性与事件属性的 boolean 类型
                    if ((boolean)value!=properties.getBoolean(key)){
                        return false;
                    }
                    break;
                case LIST:
                    //事件数据中的字符串数组
                    com.alibaba.fastjson.JSONArray array1=properties.getJSONArray(key);
                    //将自定义属性 List<String> 转换为字符串数组
                    List<String> list=(List<String>)value;
                    com.alibaba.fastjson.JSONArray array2=  com.alibaba.fastjson.JSONArray.parseArray(JSON.toJSONString(list));
                    //若两个数组不相等，元素相同，元素位置不同也是不相等
                    if (!array1.equals(array2)){
                        return false;
                    }
                    break;
            }

        }else {
            //不包含，直接返回 false
            return false;
        }
        //key、value 均通过，返回 true
        return true;
    }

    /**
     * 校验单个自定义属性
     * @param propertyModels 多个自定义属性
     * @param dataModel SDK 数据构造的事件
     * @return 校验是否通过
     */
    public static boolean assertProperties(List<PropertyModel> propertyModels,DataModel dataModel){
        for (PropertyModel propertyModel:propertyModels){
            //如果其中一个自定义属性校验失败，则不再继续校验
            if (!assertProperty(propertyModel,dataModel)){
                return false;
            }
        }
        //所有自定义属性均验证通过，返回 true
        return true;
    }

    /**
     * @param ex 异常
     * @return 以 str 返回异常信息
     */
    public static String getExceptionInfo(Exception ex) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream pout = new PrintStream(out);
        ex.printStackTrace(pout);
        String ret = new String(out.toByteArray());
        pout.close();
        try {
            out.close();
        } catch (Exception e) {
        }
        return ret;
    }

    /**
     * @param throwable 异常
     * @return 以 str 返回异常信息
     */
    public static String getThrowableInfo(Throwable throwable) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream pout = new PrintStream(out);
        throwable.printStackTrace(pout);
        String ret = new String(out.toByteArray());
        pout.close();
        try {
            out.close();
        } catch (Exception e) {
        }
        return ret;
    }

    /**
     * 校验数据类型是否正常
     * @param expect 数据类型预期值
     * @param model 数据模型
     * @return 返回比较结果
     */
    public static boolean assertType(String expect,DataModel model){
        return expect.equals(model.getType());
    }

    /**
     * 校验事件名是否正常
     * @param expect 数据类型预期值
     * @param model 数据模型
     * @return 返回比较结果
     */
    public static boolean assertEventName(String expect,DataModel model){
        return expect.equals(model.getEventName());
    }

    /**
     * @param rawJson 原始数据
     * @param key 要查询的 key
     * @return 返回结果
     */
    public static String getValueFromData(org.json.JSONObject rawJson,String key){
        String value="";
        //原始 JSONObject 包含 _id,created_at,data,我们需要的数据均在 data 中;data 中存在校验位需要去除，否则无法转换为 JSONObject
        JSONObject jsonObject= null;
        try {
            jsonObject = JSON.parseObject(Util.parseData(rawJson.get("data").toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject!=null){
            value=jsonObject.getString(key);
        }
        return value;
    }

    /**
     * @param rawJson 原始数据
     * @return 返回结果
     */
    public static boolean isH5Data(org.json.JSONObject rawJson){
        //原始 JSONObject 包含 _id,created_at,data,我们需要的数据均在 data 中;data 中存在校验位需要去除，否则无法转换为 JSONObject
        JSONObject jsonObject= null;
        try {
            jsonObject = JSON.parseObject(Util.parseData(rawJson.get("data").toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        boolean hybrid_h5=jsonObject.containsKey("_hybrid_h5")&&jsonObject.getBooleanValue("_hybrid_h5");
        JSONObject properties=jsonObject.getJSONObject("properties");
        boolean checkProperties= properties.containsKey("$lib")&&
                properties.containsKey("$lib_version")&&
                properties.containsKey("$os_version")&&
                properties.containsKey("$model")&&
                properties.containsKey("$os")&&
                properties.containsKey("$brand")&&
                properties.containsKey("$app_version")&&
                properties.containsKey("$app_name")&&
                properties.containsKey("$app_id")&&
                properties.containsKey("$manufacturer")&&
                properties.containsKey("$wifi")&&
                properties.containsKey("$network_type");
        return hybrid_h5&&checkProperties;
    }

    /** 是否包含某个 key
     * @param rawJson 原始数据
     * @param key 要查询的 key
     * @return 返回结果
     */
    public static boolean hasValueFromData(org.json.JSONObject rawJson,String key){
        boolean value = false;
        //原始 JSONObject 包含 _id,created_at,data,我们需要的数据均在 data 中;data 中存在校验位需要去除，否则无法转换为 JSONObject
        try {
            JSONObject jsonObject = JSON.parseObject(Util.parseData(rawJson.get("data").toString()));
            value=jsonObject.containsKey(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 测试 SDK 所有 API 是否正常
     * @param mContext context 对象
     */
    public static void apiTest(Context mContext){
        SensorsDataAPI api=SensorsDataAPI.sharedInstance();;
        boolean noCrash=true;
        try {
            //执行用例
            api.addVisualizedAutoTrackActivity(MainActivity.class);
            List<Class<?>> list=new ArrayList<>();
            api.addHeatMapActivities(list);
            List<Class<?>> activitiesList=new ArrayList<>();
            api.addHeatMapActivity(MainActivity.class);
            api.addVisualizedAutoTrackActivities(activitiesList);
            api.clearGPSLocation();
            api.clearLastScreenUrl();
            api.clearReferrerWhenAppEnd();
            api.clearSuperProperties();
            api.clearTrackTimer();
            api.deleteAll();
            List<SensorsDataAPI.AutoTrackEventType> eventTypeList = new ArrayList<>();
            api.disableAutoTrack(SensorsDataAPI.AutoTrackEventType.APP_CLICK);
            api.disableAutoTrack(eventTypeList);
            api.enableAutoTrack(eventTypeList);
            api.enableAutoTrackFragments(activitiesList);
            api.enableNetworkRequest(true);
            api.enableTrackScreenOrientation(true);
            api.flush();
            api.flushSync();
            api.getAnonymousId();
            api.getCookie(false);
            api.getDistinctId();
            api.getFlushBulkSize();
            api.getFlushInterval();
            api.getIgnoredViewTypeList();
            api.getLastScreenTrackProperties();
            api.getLastScreenUrl();
            api.getLoginId();
            api.getMaxCacheSize();
            api.getPresetProperties();
            api.getScreenOrientation();
            api.getSessionIntervalTime();
            api.getSuperProperties();
            api.getClass();
            api.identify("abc");
            api.ignoreAutoTrackActivities(activitiesList);
            api.ignoreAutoTrackActivity(MainActivity.class);
            api.ignoreAutoTrackFragments(activitiesList);
            Button button=new Button(mContext);
            api.ignoreView(button,false);
            api.ignoreViewType(Button.class);
            api.isActivityAutoTrackAppClickIgnored(MainActivity.class);
            api.isActivityAutoTrackAppViewScreenIgnored(MainActivity.class);
            api.isAutoTrackEnabled();
            api.isAutoTrackEventTypeIgnored(SensorsDataAPI.AutoTrackEventType.APP_CLICK);
            api.isHeatMapActivity(MainActivity.class);
            api.isHeatMapEnabled();
            api.isNetworkRequestEnable();
            api.isTrackFragmentAppViewScreenEnabled();
            api.isVisualizedAutoTrackActivity(MainActivity.class);
            api.isVisualizedAutoTrackEnabled();
            api.itemDelete("a","a");
            api.itemSet("a","b",new org.json.JSONObject());
            api.login("abc");
            api.login("abc",new org.json.JSONObject());
            api.logout();
            api.profileAppend("a","b");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Set<String> set=new ArraySet<String>();
                set.add("bbb");
                api.profileAppend("a",set);
            }
            api.profileDelete();
            api.profileIncrement("a",123);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                api.profileIncrement(new ArrayMap<String,Number>());
            }
            api.profilePushId("a","a");
            api.profileSet(new org.json.JSONObject());
            api.profileSet("age",23);
            api.profileSetOnce(new org.json.JSONObject());
            api.profileSetOnce("bax",44);
            api.profileUnset("aa");
            api.profileUnsetPushId("aaa");
            api.registerDynamicSuperProperties(new SensorsDataDynamicSuperProperties() {
                @Override
                public org.json.JSONObject getDynamicSuperProperties() {
                    return null;
                }
            });
            api.registerSuperProperties(new org.json.JSONObject());
            api.removeTimer("aaa");
            api.resetAnonymousId();
            api.resumeAutoTrackActivities(activitiesList);
            api.resumeAutoTrackActivity(MainActivity.class);
            api.resumeIgnoredAutoTrackFragments(activitiesList);
            api.resumeTrackScreenOrientation();
            api.setFlushNetworkPolicy(SensorsNetworkType.TYPE_NONE);
            api.setSessionIntervalTime(12*10000);
            api.setCookie("aa",true);
            api.setServerUrl("http://newsdktest.datasink.sensorsdata.cn/sa?project=liuweiqiang&token=5a394d2405c147ca");
            api.setDeepLinkCallback(new SensorsDataDeepLinkCallback() {
                @Override
                public void onReceive(String params, boolean success, long appAwakePassedTime) {

                }
            });
            api.setGPSLocation(12.0,22.0);
            api.setFlushBulkSize(20);
            api.setFlushInterval(30);
            api.setMaxCacheSize(64*1024);
            api.setTrackEventCallBack(new SensorsDataTrackEventCallBack() {
                @Override
                public boolean onTrackEvent(String eventName, org.json.JSONObject eventProperties) {
                    return true;
                }
            });
//            api.setViewActivity(button, mContext);
//            api.setViewFragmentName(btn_fun,TestFunctionFragment.class.getName());
            api.setViewID(button,"aaa");
            api.setViewID(new Object(),"aaa");
            api.setViewProperties(button,new org.json.JSONObject());
            api.stopTrackThread();
            api.startTrackThread();
            api.stopTrackScreenOrientation();
            api.trackInstallation("app",new org.json.JSONObject());
            api.trackInstallation("bb");
            api.trackInstallation("app",new org.json.JSONObject(),true);
            api.track("aa");
            api.track("aa",new org.json.JSONObject());
            api.trackChannelEvent("abc",new org.json.JSONObject());
            api.trackTimerEnd("aaa");
            api.trackTimerEnd("abc",new org.json.JSONObject());
            api.trackTimerResume("aaa");
            api.trackTimerStart("abc");
            api.trackViewAppClick(button,new org.json.JSONObject());
            api.trackViewScreen(MainActivity.class);
            api.trackViewScreen("url",new org.json.JSONObject());
            api.trackTimerPause("aa");
            api.trackAppInstall();
            api.trackFragmentAppViewScreen();
            api.unregisterSuperProperty("abc");
            api.enableDeepLinkInstallSource(true);
            api.trackDeepLinkLaunch("test");
            api.enableLog(true);
            api.requestDeferredDeepLink(null);
            api.setExposureIdentifier(button,"aaa");
            SAExposureData exposureData=new SAExposureData("aa");
            api.addExposureView(button,exposureData);
            api.removeExposureView(button);

        }catch (Throwable throwable){
            noCrash=false;
            //若用例出现崩溃，存储到测试用例 DB 信息
            TestCaseDao mCaseDao= MyApplication.getDaoInstant().getTestCaseDao();
            TestCase testCase=new TestCase();
            testCase.setName("全量接口调用");
            testCase.setDescribe("校验调用全部 SDK 接口是否出现崩溃");
            testCase.setExpect("SDK 正常运行");
            testCase.setActuality("SDK 出现崩溃");
            testCase.setIsPass(false);
            testCase.setFailReason("SDK 出现崩溃");
            testCase.setRemark("崩溃信息："+ Util.getThrowableInfo(throwable));
            mCaseDao.insert(testCase);
        }
        if (noCrash){
            TestCaseDao mCaseDao= MyApplication.getDaoInstant().getTestCaseDao();
            TestCase testCase=new TestCase();
            testCase.setName("全量接口调用");
            testCase.setDescribe("校验调用全部 SDK 接口是否出现崩溃");
            testCase.setExpect("SDK 正常运行");
            testCase.setActuality("SDK 正常运行");
            testCase.setIsPass(true);
            testCase.setRemark("SDK 全量接口调用正常，没有崩溃出现");
            mCaseDao.insert(testCase);
        }
    }

}
