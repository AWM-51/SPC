package com.wj.domain;

/*
* 产品指标
* 规格上限USL
* 规格下限LSL
* */
public class Indicators {
    private int id;
    private int c_id;//外键 到 checked_item表
    private double targetValue;//目标值
    private double USL;//规格上限
    private double LSL;//规格下限



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getC_id() {
        return c_id;
    }

    public void setC_id(int c_id) {
        this.c_id = c_id;
    }

    public double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(double targetValue) {
        this.targetValue = targetValue;
    }

    public double getUSL() {
        return USL;
    }

    public void setUSL(double USL) {
        this.USL = USL;
    }

    public double getLSL() {
        return LSL;
    }

    public void setLSL(double LSL) {
        this.LSL = LSL;
    }



}
