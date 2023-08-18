package com.sensorsdata.automation.greendao;

import com.sensorsdata.automation.utils.Util;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class TestCase {

    @Id(autoincrement = true)
    private Long id;//用例id
    private String time=Util.getTime();//执行时间
    private String name="";//用例名称
    private String api="";//接口信息
    private String describe="";//描述信息
    private String expect="";//预期结果
    private String actuality="";//实际结果
    private boolean isPass;//是否通过 true 通过，false 不通过
    private String failReason="";//失败原因
    private String remark="";//备注信息
    @Generated(hash = 370520968)
    public TestCase(Long id, String time, String name, String api, String describe,
            String expect, String actuality, boolean isPass, String failReason,
            String remark) {
        this.id = id;
        this.time = time;
        this.name = name;
        this.api = api;
        this.describe = describe;
        this.expect = expect;
        this.actuality = actuality;
        this.isPass = isPass;
        this.failReason = failReason;
        this.remark = remark;
    }
    @Generated(hash = 2047508717)
    public TestCase() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getName() {
        return this.name;
    }
    public TestCase setName(String name) {
        this.name = name;
        return this;
    }
    public String getApi() {
        return this.api;
    }
    public TestCase setApi(String api) {
        this.api = api;
        return this;
    }
    public String getDescribe() {
        return this.describe;
    }
    public TestCase setDescribe(String describe) {
        this.describe = describe;
        return this;
    }
    public String getExpect() {
        return this.expect;
    }
    public TestCase setExpect(String expect) {
        this.expect = expect;
        return this;
    }
    public String getActuality() {
        return this.actuality;
    }
    public TestCase setActuality(String actuality) {
        this.actuality = actuality;
        return this;
    }
    public boolean getIsPass() {
        return this.isPass;
    }
    public TestCase setIsPass(boolean isPass) {
        this.isPass = isPass;
        return this;
    }
    public String getFailReason() {
        return this.failReason;
    }
    public TestCase setFailReason(String failReason) {
        this.failReason = failReason;
        return this;
    }
    public String getRemark() {
        return this.remark;
    }
    public TestCase setRemark(String remark) {
        this.remark = remark;
        return this;
    }
}
