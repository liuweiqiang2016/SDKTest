package com.sensorsdata.automation.cases;


import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.automation.BaseCase;
import com.sensorsdata.automation.greendao.TestCase;
import com.sensorsdata.automation.model.DataModel;
import com.sensorsdata.automation.utils.Util;

import org.json.JSONException;

public class ItemCase extends BaseCase {

    @Override
    public void beforeTest() {
        super.beforeTest();
    }

    String itemType="i_type",itemId="i_id";

    @Override
    public void Test() {
        super.Test();
        SensorsDataAPI.sharedInstance().itemSet(itemType,itemId,Util.makeJSONObject(list));
        SensorsDataAPI.sharedInstance().itemDelete(itemType,itemId);
    }

    @Override
    public void afterTest() {
        super.afterTest();
        if (jsonArray!=null){
            try {

                DataModel model0=Util.dataToModel(jsonArray.getJSONObject(0));
                //校验数据类型及 itemType、itemId
                mCaseDao.insert(new TestCase()
                        .setName("校验数据类型及 itemType、itemId")
                        .setApi("itemSet(itemType,itemId,properties)")
                        .setExpect("数据类型及及 itemType、itemId 正确")
                        //.setActuality("type:"+model0.getType()+",itemId:"+model0.getItemId()+",itemType:"+model0.getItemType())
                        .setIsPass(Util.assertType("item_set",model0)&&itemId.equals(model0.getItemId())&&itemType.equals(model0.getItemType())));

                //校验 itemSet 自定义属性
                mCaseDao.insert(new TestCase()
                        .setName("校验 itemSet 自定义属性")
                        .setApi("itemSet(itemType,itemId,properties)")
                        .setExpect("自定义属性与传入参数一致")
                        .setIsPass(Util.assertProperties(list,model0)&&model0.getProperties().size()==5)
                );

                //第二条数据
                DataModel model1=Util.dataToModel(jsonArray.getJSONObject(1));
                //校验数据类型及 itemType、itemId
                mCaseDao.insert(new TestCase()
                        .setName("校验数据类型及 itemType、itemId")
                        .setApi("itemDelete(itemType,itemId)")
                        //.setActuality("type:"+model1.getType()+",itemId:"+model1.getItemId()+",itemType:"+model1.getItemType())
                        .setExpect("数据类型及及 itemType、itemId 正确")
                        .setIsPass(Util.assertType("item_delete",model1)&&itemId.equals(model1.getItemId())&&itemType.equals(model1.getItemType())));

                //校验 itemDelete 自定义属性
                mCaseDao.insert(new TestCase()
                        .setName("校验 itemSet 自定义属性")
                        .setApi("itemDelete(itemType,itemId)")
                        .setExpect("{}")
                        .setIsPass(model1.getProperties().isEmpty())
                );

            } catch (JSONException e) {
                e.printStackTrace();
                mCaseDao.insert(new TestCase()
                        .setName("数据及属性正常获取")
                        .setDescribe("SDK 入库的数据或属性可以正常获取")
                        .setExpect("数据及属性正常获取")
                        .setActuality("数据及属性未能获取")
                        .setFailReason("JSON 解析异常")
                        .setIsPass(false)
                        .setRemark("崩溃信息："+Util.getExceptionInfo(e)));

            }
        }
    }
}
