package com.wj.util;

import com.wj.domain.SampleData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SampleDataHandler {
    /*
   * 数据筛选
   * 如果某项数据数据为null则跳过
   * @param SampleDate sd 样本数据
   * */
    public List<Double> dataList_OnlyDigital(List<Double> dataList){
        List<Double> after_dataLiat=new ArrayList<Double>();
        for(Double d : dataList){
            if(d.isNaN()) {            //如果为非数字，则跳过
                continue;
            }
            else {
                after_dataLiat.add(d);
            }
        }
        return after_dataLiat;
    }

    /*
    *计算平方和
    * @param List
    * @return Double res
    * */
    public Double get_SquareSum(List<Double> dataList){
        int len=dataList.size();
        double sqrsum = 0.0000;
        for (int i = 0; i <len; i++) {
            sqrsum = sqrsum + dataList.get(i)*dataList.get(i);
        }
        return sqrsum;
    }

    /*
    * 计算平均值
    * 数据来源 SampleDate
    * @param List<Double> dataList 样本数据list
    * @return  Double average
    * */
    public double get_average(List<Double> dataList){
        Double sum = 0.0000;
        for(Double d : dataList) {
            sum += d;
        }
        int Denominator=dataList.size();
        return sum/Denominator;
    }

    /*
    * 计算方差
    * 数据来源 SampleDate
    * @param List<Double> dataList 样本数据list
    * @return  Double variance
    * */
    public Double get_variance(List<Double> dataList){
        int count = dataList.size();
        Double sqrsum = get_SquareSum(dataList);
        Double average = get_average(dataList);
        double result=0.0000;
        result = (sqrsum - count * average * average) / count;
        return result;

    }

    /*计算标准差
    * 数据来源 SampleDate
    * @param List<Double> dataList 样本数据list
    * @return  Double standard_Deviation
    * */
    public Double get_standard_Deviation(List<Double> dataList){
        Double variance = get_variance(dataList);
        Double result;
        result = Math.sqrt(Math.abs(variance));
        return result;
    }

    /*格式化数据
    * 输出位数统一为5位
    * @param Double data
    * @return Double res
    * */
    public Double get_stanardData(Double data){
        BigDecimal bg = new BigDecimal(data);
        double res = bg.setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
        return res;
    }

    /*
    * 计算Ca值（制程准确度）
    * 依据公式 Ca=(X-U)/(T/2) （X为所有取样数据的平均值；
    * 规格公差T＝规格上限－规格下限；规格中心值U＝（规格上限+规格下限）/2
    * ）*/
    public Double get_Ca(Double X,Double USL,Double LSL){
        Double U=1.00000*(USL+LSL)/2;
        Double T=1.00000*(USL-LSL);
        Double Ca=(X-U)/(T/2);
        return Ca;
    }



    /*
    * 计算Cp值（制程精密度）
    * 依据公式 Cp =T/6σ
    * */
    public Double get_Cp(Double variance , Double USL ,Double LSL){
        return 1.00000*(USL-LSL)/(6*variance);
    }



}
