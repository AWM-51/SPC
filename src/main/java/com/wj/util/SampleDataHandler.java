package com.wj.util;

import com.wj.domain.SampleData;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SampleDataHandler {
    //统计学数组

    public static final Double[] D2={1.0,1.0,1.128,1.169,2.059,2.326,2.543,2.704,2.847,2.970,3.078,3.173,3.258,3.336,3.407
            ,3.472,3.532,3.588,3.640,3.689,3.735,3.778,3.819,3.858,3.895,3.931};
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
        return get_stanardData(sum/Denominator);
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
        return get_stanardData(result);
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
        return get_stanardData(Ca);
    }



    /*
    * 计算Cp值（制程精密度）
    * 依据公式 Cp =T/6σ
    * */
    public Double get_Cp(Double standardDeviation , Double USL ,Double LSL){
        return 1.00000*(USL-LSL)/(6*standardDeviation);
    }

    /*
    * 计算过程中每个数据的CPK
    * 公式 CPK=Cp*（1-|Ca|）
    * */
    public Double get_CP(List<Double> dataList, Double USL ,Double LSL,Double SD){
        Double Ca=get_Ca(get_average(dataList),USL,LSL);
        Double Cp=get_Cp((SD/2.326),USL,LSL);//(SD/2.326)通过极差估计标准差
        System.out.println("--- 标准差----"+get_standard_Deviation(dataList));
        Double Cpk=Cp*(1-Math.abs(Ca));
        System.out.println("估计值标准差="+(SD/2.326)+"  Ca="+Ca+"   Cp="+Cp+"   CPK="+Cpk);
        return get_stanardData(Cpk);
    }
    public  Double get_CPK(List<Double> dataList, Double USL ,Double LSL,Double SD){
        System.out.println("SD----->"+SD);
        Double Cpl=get_Cpl(dataList,LSL,SD);
        Double Cpu=get_Cpu(dataList,USL,SD);

        return Cpl<Cpu?Cpl:Cpu;
    }
    /*Cpu*/
    public Double get_Cpu(List<Double> dataList, Double USL,Double SD){
        return get_stanardData((USL-get_average(dataList))/(3.0000*(SD/2.326)));
    }

    /*Cpl*/
    public Double get_Cpl(List<Double> dataList, Double LSL,Double SD){
        return get_stanardData((get_average(dataList)-LSL)/(3.0000*(SD/2.326)));
    }


    /*计算最大值*/
    public double getMaxInList(List<Double> l){
        double max;

        max=l.get(0);
        for(Double a :l){
            if(a.doubleValue()>max)
                max=a.doubleValue();
        }
        return max;
    }
    /*计算最小值*/
    public double getMinInList(List<Double> l){
        double min;

        min=l.get(0);
        for(Double a :l){
            if(a.doubleValue()<min)
                min=a.doubleValue();
        }
        return min;
    }
    //获取样本数据SampleData数组中最大值
    public double getMaxInListForSampleData(List<SampleData> list){
        double max1;
        max1=list.get(0).getValue();
        for(SampleData l :list){
            if(l.getValue()>max1)
                max1=l.getValue();
        }
        return max1;
    }
    //获取样本数据SampleData数组中最大值
    public double getMinInListForSampleData(List<SampleData> list){
        double min;
        min=list.get(0).getValue();
        for(SampleData l :list){
            if(l.getValue()<min)
                min=l.getValue();
        }
        return min;
    }
    /*计算极差*/
    public Double get_R(List<Double> list){
        return get_stanardData(getMaxInList(list)-getMinInList(list));
    }
    /*计算极差 参数为样本数据对象*/
    public Double get_R_S(List<SampleData> list){
//        System.out.println("计算极差："+getMaxInListForSampleData(list)+"-"+getMinInListForSampleData(list));
        if(getMaxInListForSampleData(list)-getMinInListForSampleData(list)==0){
            return 0.00000001;
        }
        else {
            return get_stanardData(getMaxInListForSampleData(list)-getMinInListForSampleData(list));
        }

    }

    /* 通过RBar/d2 来估计极差*/
    public double get_SDByRBar_d2(List<Double> R_List){
        int num=5;
        Double avg=get_average(R_List);
        return get_stanardData(avg/D2[num]);
    }

    /*计算组内CPK 需要等到样本数据稳定后，其区别在于西格玛采用估计值，即S=Rbar/d2，在过程稳定的情况下，与统计学公式计算出来的标准差误差不大*/
    public Double get_R_CPK(){return 1.0;}
    /*
    * 计算PPU
    * Cpu=(USL-Average)/3σ
    * 标准差采用整体标准差
    * */
    public Double get_Ppu(List<Double> dataList, Double USL){
        return get_stanardData((USL-get_average(dataList))/(3.0000*get_standard_Deviation(dataList)));
    }


    /*计算PPL
    * Cpl=(Average-LSL)/3σ */
    public Double get_Ppl(List<Double> dataList, Double LSL){
        return get_stanardData((get_average(dataList)-LSL)/(3.0000*get_standard_Deviation(dataList)));
    }
    /*计算的Ppk
    * Ppk=min{ppu,pul}*/
    public Double get_PPK(List<Double> dataList, Double USL ,Double LSL){
        Double cpu=get_Ppu(dataList,USL);
        Double cpl=get_Ppl(dataList,LSL);

        return cpu>cpl?cpl:cpu;
    }

    public Double get_PP(List<Double> dataList, Double USL ,Double LSL){
        Double Pa=get_Ca(get_average(dataList),USL,LSL);
        Double Pp=get_Cp(get_standard_Deviation(dataList),USL,LSL);
        Double PP=Pp*(1-Math.abs(Pa));
        return get_stanardData(PP);
    }


    /*计算Ppl
    * 与Cpk差别在于标准差采用整体标准差
    * */

    public Double get_PassRate(List<SampleData> list,Double USL,Double LSL){
        for(SampleData sampleData : list){
            System.out.println("数据："+sampleData.getValue());
        }
        System.out.println("USL:"+USL+"   LSL:"+LSL);
        DecimalFormat df=new DecimalFormat("0.0000");

        int size = list.size();
        int pass=0;
        for(SampleData d : list){
            double value=d.getValue();
            if(value>=LSL&&value<=USL){
                ++pass;
            }
        }
        System.out.println("pass个数："+pass);
        System.out.println("总个数："+size);
        System.out.println("合格率:"+df.format((double) pass/size));
        return get_stanardData(Double.valueOf(df.format((double) pass/size)));
    }

//    public static void main(String[] args) {
//        SampleDataHandler sampleDataHandler=new SampleDataHandler();
//        List<Double> list=new ArrayList<Double>();
//        list.add(9.2999);
//        list.add(8.9821);
//        list.add(9.0121);
//        list.add(9.1122);
//        list.add(8.8999);
//
////        list.add(9.2009);
////        list.add(8.0021);
////        list.add(9.0991);
////        list.add(9.1002);
////        list.add(8.7299);
//        Double s=sampleDataHandler.get_variance(list);
//        System.out.println("avg"+sampleDataHandler.get_average(list)+"v"+sampleDataHandler.get_stanardData(s));
//    }

    /*冒泡排序*/
    public List<Double> bubbleSort(List<Double> list){
        int n=list.size();
        int j , k;
        int flag = n ;//flag来记录最后交换的位置，也就是排序的尾边界

        while (flag > 0){//排序未结束标志
            k = flag; //k 来记录遍历的尾边界
            flag = 0;

            for(j=1; j<k; j++){
                if(list.get(j-1) > list.get(j)){//前面的数字大于后面的数字就交换
                    //交换a[j-1]和a[j]
                    double temp;
                    temp = list.get(j-1);
                    list.set(j - 1, list.get(j));
                    list.set(j, temp);

                    //表示交换过数据;
                    flag = j;//记录最新的尾边界.
                }
            }
        }
        return list;
    }






}
