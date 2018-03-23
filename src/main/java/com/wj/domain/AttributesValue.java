package com.wj.domain;

public class AttributesValue {
    private int id;
    private int type_id;//什么类型的状态值 1：project  2：checkitem 3：group  4：sampledata
    private int f_id;//与tepe_id对应选取值
    private Double averageValue;
    private Double variance;
    private Double standardDeviation;
    private Double MAX;
    private Double MIN;

    private Double Ca;
    private Double Cp;
    private Double CPK;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType_id() {
        return type_id;
    }

    public void setType_id(int type_id) {
        this.type_id = type_id;
    }

    public int getF_id() {
        return f_id;
    }

    public void setF_id(int f_id) {
        this.f_id = f_id;
    }

    public Double getAverageValue() {
        return averageValue;
    }

    public void setAverageValue(Double averageValue) {
        this.averageValue = averageValue;
    }

    public Double getVariance() {
        return variance;
    }

    public void setVariance(Double variance) {
        this.variance = variance;
    }

    public Double getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(Double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public Double getMAX() {
        return MAX;
    }

    public void setMAX(Double MAX) {
        this.MAX = MAX;
    }

    public Double getMIN() {
        return MIN;
    }

    public void setMIN(Double MIN) {
        this.MIN = MIN;
    }

    public Double getCa() {
        return Ca;
    }

    public void setCa(Double ca) {
        Ca = ca;
    }

    public Double getCp() {
        return Cp;
    }

    public void setCp(Double cp) {
        Cp = cp;
    }

    public Double getCPK() {
        return CPK;
    }

    public void setCPK(Double CPK) {
        this.CPK = CPK;
    }




}
