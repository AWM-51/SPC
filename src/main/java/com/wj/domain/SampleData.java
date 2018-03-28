package com.wj.domain;

import java.util.Date;

public class SampleData {
    private int id;
    private int g_id;
    private String obtain_time;
    private Double value;
    private int s_status;//1：已经输入，已处理，合格 2：已经输入，未处理 0：已经输入 ，已经处理，不合格 4：已经输入，已删除
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getG_id() {
        return g_id;
    }

    public void setG_id(int g_id) {
        this.g_id = g_id;
    }

    public String getObtain_time() {
        return obtain_time;
    }

    public void setObtain_time(String obtain_time) {
        this.obtain_time = obtain_time;
    }


    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public int getS_status() {
        return s_status;
    }

    public void setS_status(int s_status) {
        this.s_status = s_status;
    }


}
