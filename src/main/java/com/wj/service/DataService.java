package com.wj.service;

import com.wj.dao.*;
import com.wj.domain.*;
import com.wj.util.ExportEcxcel;
import com.wj.util.ReadExcel;
import com.wj.util.SampleDataHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.apache.commons.math3.fitting.GaussianCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Repository
public class DataService {
    @Autowired
    private SampleDataDao sampleDataDao;
    @Autowired
    private UploadSDExcelLogDao uploadSDExcelLogDao;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private DGroupDao dGroupDao;
    @Autowired
    private CheckItemDao checkItemDao;
    @Autowired
    private IndicatorsDao indicatorsDao;

    public SampleDataHandler sampleDataHandler;

    /*
        * 上传成功excel,并记录日志
        *
        * */
    @Transactional
    public int uploadExcelSuccess(int c_id,MultipartFile file, User user) throws IOException, ParseException {
        if(file.isEmpty()){
            return 0;
        }

        ReadExcel readExcel=new ReadExcel();
        SampleDataHandler sampleDataHandler = new SampleDataHandler();
        UploadSDExcelLog uploadSDExcelLog=new UploadSDExcelLog();

        String fileName=file.getOriginalFilename();//获取文件名
        System.out.println("获得文件："+file.getOriginalFilename());

        int uploadUserId=user.getUserId();//获取上传者的ID

        Date entyrTime=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                .parse(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                        .format(new Date()));//数据样本录入时间

//        try{
            List<List<SampleData>> sampleDatas = readExcel.readXls(file,entyrTime);//读入表格
            int datanum= sampleDatas.size();//样本数据组数量
            do_ExcelUpload_Log(entyrTime,uploadUserId,fileName,datanum);//将上传日志相关信息传入数据库
            uploadSDExcelLog=sampleDataDao.selectUploadExcelID(entyrTime);//目的是从数据库中将与entryTime相同的日志取出，使用他的id



            for(List<SampleData> sd : sampleDatas) {
                D_Group d_group = new D_Group();
                d_group.setC_id(c_id);
                d_group.setCreate_time(entyrTime.toString());
                d_group.setObtain_time(sd.get(0).getObtain_time());
                dGroupDao.InsertInto_DGroupInfo(d_group);

                for (SampleData d : sd) {
                    SampleData data = new SampleData();
                    data.setG_id(dGroupDao.get_NewestG_id());
                    data.setObtain_time(d.getObtain_time());
                    data.setValue(sampleDataHandler.get_stanardData(d.getValue()));
                    data.setS_status(2);
                    sampleDataDao.insertSampleData(data);
                }
            }


        return 1;
    }



    /*
    * 录入文件的日志
    *
    * */
    @Transactional
    public void do_ExcelUpload_Log(Date uploadTime,int uploadUserId
            ,String excelName,int dataNum){
        String remark="";
        UploadSDExcelLog uploadSDExcelLog=new UploadSDExcelLog();
        uploadSDExcelLog.setExcel_name(excelName);
        uploadSDExcelLog.setData_num(dataNum);
        uploadSDExcelLog.setUpload_time(uploadTime);
        uploadSDExcelLog.setUpload_user_id(uploadUserId);
        uploadSDExcelLog.setRemarks(remark);

        uploadSDExcelLogDao.insertUploadSDExcel(uploadSDExcelLog);

    }



    //-----------Project-------------------
    /*
    * 新增项目信息，同时更新其他正在使用的项目为未使用，即状态有1变为2
    * */
    @Transactional
    public void creat_newProject(int u_id,String project_name,String Remarks) throws ParseException {
        Project project = new Project();
        Date entyrTime=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                .parse(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                        .format(new Date()));//数据样本录入时间
        /*先将所有已经存在的所有正在使用的项目变为未使用*/
        projectDao.Update_AllProjects_1_to_2();
        /*添加新的项目信息*/
        project.setU_id(u_id);
        project.setP_name(project_name);
        project.setCreate_time(entyrTime.toString());
        project.setP_status(1);//创建时更新状态
        project.setCurrent_num(0);//当前数据数量为0
        project.setRemarks(Remarks);
        projectDao.Insert_Project_info(project);
    }

    /*
    * 拉取用户下所有项目信息列表
    * */
    public List<Project> get_All_ProjectInfoList(int u_id){
        List<Project> projects = new ArrayList<Project>(projectDao.Query_AllProjects_Info(u_id));

        return projects;
    }

    /*删除某用户某项目，实际为该状态
    */
    @Transactional
    public void deleteProject(int p_id){
        System.out.println("删除的项目id》》》"+p_id);
        projectDao.Update_Project_Status(p_id,0);
        projectDao.Update_NewestProjectsTo1();
    }

    //-------------CheckItem------------

    /*
    * 新增项目信息，同时更新其他正在使用的项目为未使用，即状态有1变为2
    * */
    @Transactional
    public void creat_newCheckItem(int p_id,String c_name,String Remarks) throws ParseException {
        CheckItem checkItem = new CheckItem();
        /*先将所有已经存在的所有正在使用的项目变为未使用*/
        checkItemDao.Update_AllCheckItem_1_to_2();
        /*添加新的项目信息*/
        checkItem.setC_name(c_name);
        checkItem.setP_id(p_id);
        checkItem.setC_status(1);
        checkItem.setCurrent_num(0);
        checkItem.setRemarks(Remarks);
        checkItemDao.InsertCheckItem(checkItem);
    }

    /*
    * 点击*/

    /*
    * 拉取用户下所有项目信息列表
    * */
    public List<CheckItem> get_All_CheckItemInfoList(int p_id){
        List<CheckItem> checkItems = new ArrayList<CheckItem>(checkItemDao.Query_AllCheckItems_Info(p_id));
        return checkItems;
    }

    /*删除某用户某项目，实际为该状态
    */

    public void deleteCheckItem(int c_id,int p_id){
        System.out.println("删除的项目id》》》"+c_id);

        checkItemDao.Update_CheckItem_Status(c_id,0);//状态为0即为删除
        checkItemDao.Update_NestCheckitem2_to1(p_id);
    }


    /*获取最新checkItem*/
    public CheckItem getNestCheckItem(int p_id){
        return checkItemDao.get_nest_CheckItem(p_id);
    }

//----------data---------------
    /*获取该checkeditem 中所有值，并以组分好*/
    public List<List<SampleData>> getAllDataInCheckItem(int c_id){
        System.out.println(c_id);
        List<List<SampleData>> list_2=null;
        List<SampleData> sampleDataList = checkItemDao.get_AllDataInCheckItem(c_id);
        List<D_Group> g_idList=checkItemDao.get_Group_In_CheckItem(c_id);
        list_2=new ArrayList<List<SampleData>>();
        if(sampleDataList.isEmpty()||g_idList.isEmpty() )
            return null;
        else {
            int i = 0;
            for(D_Group c : g_idList){
                List<SampleData> tempSDList = new ArrayList<SampleData>();
                for (SampleData sd : sampleDataList){

                    if(sd.getG_id() == c.getG_id()){
                        tempSDList.add(sd);
                    }
                    else {
                        continue;
                    }
                }
                list_2.add(tempSDList);
                ++i;
            }

            return list_2;
        }

    }
    /*获取抽检属性名称*/
    public CheckItem get_CheckItem(int c_id){
        CheckItem checkItem=new CheckItem();
        checkItem=checkItemDao.get_CheckItem(c_id);
        return checkItem;
    }
    @Transactional
    public void clickAtCheckItem(int c_id){
        checkItemDao.Update_AllCheckItem_1_to_2();//先把所有是1的重制为2
        checkItemDao.Update_CheckItem_Status(c_id,1);//再把需要的设置为1
    }
    @Transactional
    public void clickAtProject(int p_id){
        projectDao.Update_AllProjects_1_to_2();//先重制项目状态
        projectDao.Update_Project_Status(p_id,1);//将点击项目状态更新为1
        checkItemDao.Update_AllCheckItem_1_to_2();//又把所有是1的子项目重制为2
        checkItemDao.Update_NestCheckitem2_to1(p_id);//将子项目中最新的一条更新为1
    }

    /*更新或添加Indicators信息*/
    public void uploadOrAddIndicatorsInfo(int c_id,Indicators indicators){
        List<Indicators>IList=new ArrayList<Indicators>();
        IList=indicatorsDao.getIndicatorInfo(c_id);
        if(IList.size()==0){
            indicatorsDao.insertIndicatorInfo(indicators);
        }
        else {
            indicatorsDao.updateIndicatorInfo(indicators);
        }
    }
    /*获取Indicators信息*/
    public Indicators getIndicators(int c_id){

        Indicators indicators=new Indicators();
        Indicators tempDicators=new Indicators();
        if(indicatorsDao.getIndicatorInfo(c_id).size()==0){
            tempDicators.setUSL(0);
            tempDicators.setLSL(0);
            tempDicators.setTargetValue(0);
            return  tempDicators;

        }

        else {
            indicators=indicatorsDao.getIndicatorInfo(c_id).get(0);
            return  indicators;
        }
    }

    /*获取某检查项目下所有数据，返回为list*/
    public List<SampleData> getALLSampleDataByCID(int c_id){

        if(sampleDataDao.get_SampleDataListInDB(c_id).size()==0)
            return null;
        else
            return sampleDataDao.get_SampleDataListInDB(c_id);
    }

    /*获取某检查项目下所有组数据平均值，返回为list*/
    public List<Double> getAVGofValueInGID(int c_id){
        if(sampleDataDao.get_AVGofValueByG_id(c_id).size()==0)
            return null;
        else
            return sampleDataDao.get_AVGofValueByG_id(c_id);
    }

    /*获取某检查项目下所有组数据抽检时间，返回为list*/
    public List<String> getObtainTimeInGID(int c_id){
        if(sampleDataDao.get_ObtainTimeofValueByG_id(c_id).size()==0)
            return null;
        else
            return sampleDataDao.get_ObtainTimeofValueByG_id(c_id);
    }

    /*获取样本运行图的x坐标*/
    public List<Integer> getXOfSampleDataRunTable(List<SampleData> sampleData){
        List<Integer> Xlist=new ArrayList<Integer>();
        if (sampleData==null)
            return null;
        else
        {
            int j=1;
            Xlist.add(j);
            for(int i=0;i<sampleData.size()-1;++i){
                if(sampleData.get(i+1).getG_id()!=sampleData.get(i).getG_id())
                    ++j;
                Xlist.add(j);
            }
        }
        return Xlist;
    }
    /*获取样本运行图中极差控制图中X轴坐标*/
    public List<Integer> get_XRlist(List<List<SampleData>> lists){
        if(lists==null)
            return null;
        else {
            List<Integer> xRlist=new ArrayList<Integer>();
            for(int i=0;i<lists.size();++i){
                xRlist.add(i+1);
            }
            return xRlist;
        }

    }

    /*获取样本运行图的y坐标*/
    public List<Double> getYOfSampleDataRunTable(List<SampleData> sampleData){
        List<Double> SVlaueList = new ArrayList<Double>();
        if(sampleData==null)
            return null;
        else{
            for (SampleData sd :sampleData){
                SVlaueList.add(sd.getValue());
            }
            return SVlaueList;
        }


    }

    //获取数组中最大值
    public double getMaxInList(List<Double> list){
        double max;
        if(list==null)
            max= 0;

        else{
            max=list.get(0);
            for(Double l :list){
                if(l.doubleValue()>max)
                    max=l.doubleValue();
            }
            System.out.println("该数列最大值：------>"+max);
        }

        return max;
    }

    //获取数组中最 小值
    public double getMinInList(List<Double> list){
        double min;
        if (list==null)
            min=0;
        else {
            min=list.get(0);
            for(Double l :list){
                if(l.doubleValue()<min)
                    min=l.doubleValue();
            }
            System.out.println("该数列最小值：------>"+min);
        }

        return min;
    }

    //获取样本数据SampleData数组中最大值
    public double getMaxInListForSampleData(List<SampleData> list){
        double max;
        max=list.get(0).getValue();
        for(SampleData l :list){
            if(l.getValue()>max)
                max=l.getValue();
        }
        System.out.println("该该Sampledata数列最大值数列最大值：------>"+max);
        return max;
    }
    //获取样本数据SampleData数组中最大值
    public double getMinInListForSampleData(List<SampleData> list){
        double min;
        min=list.get(0).getValue();
        for(SampleData l :list){
            if(l.getValue()<min)
                min=l.getValue();
        }
        System.out.println("该Sampledata数列最大值：------>"+min);

        return min;
    }

    //将列表改变一下,
    //将 【1，2】，【3，4】，【5，6】  改成【1，2】，【1，2，3，4】，【1，2，3，4，5，6】

    public List<List<SampleData>> changList(List<List<SampleData>> lists) {
        List<SampleData> a=new ArrayList<SampleData>();
        List<List<SampleData>> t=new ArrayList<List<SampleData>>();
        for (List<SampleData> l:lists){
            List<SampleData> temp=new ArrayList<SampleData>();
            for (SampleData sampleData:l){
                a.add(sampleData);
                System.out.print("-->"+sampleData.getValue());
            }
            for(SampleData u:a){
                temp.add(u);
                System.out.println("@->"+u.getValue());
            }
            t.add(temp);
            System.out.println();
        }

        for (List<SampleData> sampleDataList:t){
            for(SampleData sampleData:sampleDataList){
                System.out.print(sampleData.getValue()+"  ");
            }
            System.out.println();
        }
        return t;
    }

    //获取CPk列表
    public List<Double> getCPkList(List<List<SampleData>> lists,Double USL,Double LSL){
        SampleDataHandler sampleDataHandler = new SampleDataHandler();
        List<Double> CPkList =new ArrayList<Double>();
        int i=0;

        for(List<SampleData> sd : lists){

            List<Double> tempList =new ArrayList<Double>();
            for(SampleData s:sd){
                tempList.add(s.getValue());
                System.out.println("将数据导入temp--->"+s.getValue());
            }
            System.out.println("temp--->"+tempList.size()+"---"+USL+"---"+LSL);
            CPkList.add(sampleDataHandler.get_CPK(tempList,USL,LSL,getRList(lists).get(i)));
            ++i;
        }
//        for(Double i : CPkList){
//            System.out.println("CPK-->"+i);
//        }
        return CPkList;
    }

    //获取组内CPK属性（需要数据达到稳定，所以需要取最后一组数据）
    public List<Double> getCPULK(List<List<SampleData>> lists,Double USL,Double LSL) {
        SampleDataHandler sampleDataHandler = new SampleDataHandler();
        List<Double> CPULK =new ArrayList<Double>();
        List<Double> E_CPULK=new ArrayList<Double>();
        E_CPULK.add(-99999.0);
        E_CPULK.add(-99999.0);
        E_CPULK.add(-99999.0);
        if (lists==null)
            return E_CPULK;
        else {
            int index=lists.size()-1;
            List<Double> tempList =new ArrayList<Double>();
            for(SampleData s:lists.get(index)){
                tempList.add(s.getValue());
            }
            System.out.println("temp--->"+tempList.size()+"---"+USL+"---"+LSL);

            CPULK.add(sampleDataHandler.get_Cpl(tempList,LSL,getRList(lists).get(index)));
            CPULK.add(sampleDataHandler.get_Cpu(tempList,USL,getRList(lists).get(index)));
            CPULK.add(sampleDataHandler.get_CPK(tempList,USL,LSL,getRList(lists).get(index)));
            return CPULK;
        }

    }

    /*
    * 获取极差列表*/
    public List<Double>getRList(List<List<SampleData>> lists){
        SampleDataHandler sampleDataHandler=new SampleDataHandler();
        List<Double>Rlist=new ArrayList<Double>();
        if(lists==null)
            return null;
        else {
            for(List<SampleData> sampleDataList:lists){
                Rlist.add(sampleDataHandler.get_R_S(sampleDataList));
            }
            return Rlist;
        }

    }


    /*获取极差平均值估计标准差*/
    public Double getSDBy_RBar_D2(List<Double> Rlist){
        SampleDataHandler sampleDataHandler=new SampleDataHandler();
        if(Rlist==null)
            return null;
        else
            return sampleDataHandler.get_SDByRBar_d2(Rlist);
    }



    /*将所有数据状态进行更新
    * 1：已经输入，已处理，合格 2：已经输入，未处理 0：已经输入 ，已经处理，不合格 4：已经输入，已删除*/
    public void updateSampleStatus(List<List<SampleData>> lists,double USL,double LSL){
        for(List<SampleData> sampleDataList :lists){
            for (SampleData sd:sampleDataList){
                double value=sd.getValue();
                System.out.println("-->"+value+"--"+USL+" "+LSL);
                if(value>=LSL&&value<=USL){
                    System.out.println(sd.getId()+"  @->1");
                    sampleDataDao.upataeSD_Status(sd.getId(),1);
                }
                else {
                    System.out.println(sd.getId()+"  @->0");
                    sampleDataDao.upataeSD_Status(sd.getId(),0);
                }
            }
        }
    }

    /*计算合格率列表*/
    public List<Double> getPassRateList(List<List<SampleData>>lists,double USL ,double LSL){
        SampleDataHandler sampleDataHandler =new SampleDataHandler();
        List<Double> psaaRateList=new ArrayList<Double>();
        for(List<SampleData> sd:lists){
            psaaRateList.add(sampleDataHandler.get_PassRate(sd,USL,LSL));
        }
        return psaaRateList;

    }

    /* 计算所有Sample的平均值*/
    public double getAVGIntotal(List<SampleData>sampleData){

        SampleDataHandler sampleDataHandler = new SampleDataHandler();
        List<Double>temp =new ArrayList<Double>();
        for(SampleData sd: sampleData){
            temp.add(sd.getValue());
        }
        return sampleDataHandler.get_average(temp);
    }

    /*计算所有的标准差*/
    public double getStandardDevicationInTotal(List<Double> list){
        SampleDataHandler sampleDataHandler = new SampleDataHandler();
        Double  StandardDevication=sampleDataHandler.get_standard_Deviation(list);
        return StandardDevication;
    }
    /*计算Ca*/
    public double getCa(List<Double> list,Double USL,Double LSL){
        SampleDataHandler sampleDataHandler = new SampleDataHandler();
        Double Ca=sampleDataHandler.get_Ca(sampleDataHandler.get_average(list),USL,LSL);
        return Ca;
    }

    /*计算Ppl，Ppu，Ppk*/
    public List<Double> get_PPULK(List<Double> list,double USL,double LSL){
        SampleDataHandler sampleDataHandler=new SampleDataHandler();
        List<Double> temp=new ArrayList<Double>();
        temp.add(sampleDataHandler.get_Ppl(list,LSL));
        temp.add(sampleDataHandler.get_Ppu(list,USL));
        temp.add(sampleDataHandler.get_PPK(list,USL,LSL));
        return temp;
    }



    /* 获取所有数的中间值*/
    public double getMinddleValue(List<Double> list){
        SampleDataHandler sampleDataHandler = new SampleDataHandler();
        List<Double> temp = sampleDataHandler.bubbleSort(list);
        Double middleValue=temp.get((int)Math.floor(temp.size()/2));
        return middleValue;
    }
    /*获取正态分布Y轴数据*/
    public List<Double> getXOfNormalDistributionChar(List<SampleData> list){
//        double max=middleValue_total_AddThreeSD>=getMaxInListForSampleData(list)
//                ?middleValue_total_AddThreeSD:getMaxInListForSampleData(list);
//        double min=middleValue_total_DecreaseThreeSD<=getMinInListForSampleData(list)
//                ?middleValue_total_DecreaseThreeSD:getMinInListForSampleData(list);
        List<Double> temp=new ArrayList<Double>();
//        int i=0;
//        for(SampleData sampleData:list){
//            weightedObservedPoints.add(i,sampleData.getValue());
//            ++i;
//        }
        WeightedObservedPoints obs = new WeightedObservedPoints();
        obs.add(0, 25);
        obs.add(1, 68);
        obs.add(2, 144);
        obs.add(3, 220);
        obs.add(4, 335);
        obs.add(5, 199);
        obs.add(6, 52);
        obs.add(7, 14);
        obs.add(8, 5);
        obs.add(9, 2);
        obs.add(0, 25);
        obs.add(1, 68);
        obs.add(2, 144);
        obs.add(3, 220);
        obs.add(4, 335);
        obs.add(5, 199);
        obs.add(6, 52);
        obs.add(7, 14);
        obs.add(8, 5);
        obs.add(9, 2);
        double[] parameters = GaussianCurveFitter.create().fit(obs.toList());

        for (double i : parameters) {

            System.out.println(parameters.length+"  "+i);
            temp.add(i);
        }
        return temp;




    }


    /*获取PPM
    * 0:<LSL
    * 1:>USL
    * 2:PPM*/
    public List<Double> getPPM(List<Double> list,Double USL,Double LSL){
        SampleDataHandler sampleDataHandler=new SampleDataHandler();
        int Less_LSL=0;
        int size = list.size();
        int More_USL=0;
        int total=0;
        Double PPM_LSL=0.0;
        Double PPM_USL=0.0;
        Double PPM=0.0;
        List<Double> ppmList=new ArrayList<Double>();
        for(Double l:list){
            if(l<LSL){
                ++Less_LSL;
            }
            else if(l>USL)
            {
                ++More_USL;
            }
        }
        System.out.println("PPM-----小于LSL："+Less_LSL+"   大于USL："+More_USL+"  ---Size:"+size);
        PPM_LSL=(Less_LSL*(1000000.00000/size));
        PPM_USL=(More_USL*(1000000.00000/size));
        PPM=PPM_LSL+PPM_USL;

        ppmList.add(sampleDataHandler.get_stanardData(PPM_LSL));
        ppmList.add(sampleDataHandler.get_stanardData(PPM_USL));
        ppmList.add(sampleDataHandler.get_stanardData(PPM));
        return ppmList;

    }
    /*list转二位数组*/
    public String[][] LtoA(List<List<String>> result){
        String[][] z = new String[result.size()][];

        for(int i=0;i<z.length;i++){
            int j=0;
            for(String s:result.get(i)){
                z[i][j] =s;
                ++j;
            }
        }
        return z;
    }


    /* 导出工序能力报表*/

    public void exporeExcelOfProcessCapability(int p_id) throws Exception {
        ExportEcxcel exportEcxcel=new ExportEcxcel();
        String sheetName = "工序能力报表";
        String titleName = "工序能力报表";
        String fileName = "工序能力报表";
        int columnNumber = 16;
        int[] columnWidth = { 30, 10, 10,10,10,10,10,10,15,15,15,15,15,15,15,15 };
        String[] columnName = { "检查项目", "Cpk", "Cp","Cpl","Cpu","Ppk","Pp","Ppl","Ppu","平均值"
                ,"极差值","标准差（整体）","标准差（组内）","最大值","最小值","记录数" };



        List<Integer> c_idList=new ArrayList<Integer>();
        List<CheckItem> checkItemList=new ArrayList<CheckItem>();
        checkItemList=checkItemDao.getCheckItemListByPID(p_id);
        for(CheckItem checkItem:checkItemList){
            System.out.println("c_id---->"+checkItem.getC_id());
            c_idList.add(checkItem.getC_id());
        }
        SampleDataHandler sampleDataHandler=new SampleDataHandler();
        int l=c_idList.size();
        String[][] dataList=new String[l][16];
        int i=0;
        String p_name=projectDao.get_projectByP_id(p_id).getP_name();
        for(Integer c_id:c_idList){
            List<SampleData> SListBySample=getALLSampleDataByCID(c_id);
            List<List<SampleData>> dataInCheckItemByGroup = getAllDataInCheckItem(c_id);
            List<Double> SVlaueList=getYOfSampleDataRunTable(SListBySample);

            double USL=getIndicators(c_id).getUSL();
            double LSL=getIndicators(c_id).getLSL();
            double U=getIndicators(c_id).getTargetValue();
            String itemName=checkItemDao.get_CheckItem(c_id).getC_name();
            int num=dataInCheckItemByGroup==null?-9999:SListBySample.size();//样本数量
            int g_num=dataInCheckItemByGroup==null?-9999:dataInCheckItemByGroup.size();
            double standardDeviation=dataInCheckItemByGroup==null?-9999:getStandardDevicationInTotal(SVlaueList);//标准差  整体
            double SD=dataInCheckItemByGroup==null?-9999:getRList(dataInCheckItemByGroup).get(g_num-1);//标准差 组内

            double avg_total=dataInCheckItemByGroup==null?-9999:getAVGIntotal(SListBySample);//整体平均值
            double max_total=dataInCheckItemByGroup==null?-9999:getMaxInListForSampleData(SListBySample);//整体最大值
            double min_total=dataInCheckItemByGroup==null?-9999:getMinInListForSampleData(SListBySample);//整体最小值
            double R=dataInCheckItemByGroup==null?-9999:(max_total-min_total);//整体极差
            double Cpl=dataInCheckItemByGroup==null?-9999:getCPULK(dataInCheckItemByGroup,USL,LSL).get(0);//Cpl
            double Cpu=dataInCheckItemByGroup==null?-9999:getCPULK(dataInCheckItemByGroup,USL,LSL).get(1);//Cpu
            double Cpk=dataInCheckItemByGroup==null?-9999:getCPULK(dataInCheckItemByGroup,USL,LSL).get(2);//Cpk
            double CP=dataInCheckItemByGroup==null?-9999:sampleDataHandler.get_CP(SVlaueList,USL,LSL,SD);//CP
            double Ppl=dataInCheckItemByGroup==null?-9999:get_PPULK(SVlaueList,USL,LSL).get(0);//Ppl
            double Ppu=dataInCheckItemByGroup==null?-9999:get_PPULK(SVlaueList,USL,LSL).get(1);//Ppu
            double Ppk=dataInCheckItemByGroup==null?-9999:get_PPULK(SVlaueList,USL,LSL).get(2);//Ppk
            double PP=dataInCheckItemByGroup==null?-9999:sampleDataHandler.get_PP(SVlaueList,USL,LSL);//PP


            // "检查项目", "Cpk", "Cp","Cpl","Cpu","Ppk","Pp","Ppl","Ppu","平均值"
               // ,"极差值","标准差（整体）","标准差（组内）","最大值","最小值","记录数"


            dataList[i][0]=p_name+"/"+itemName;
            dataList[i][1]=String.valueOf(Cpk);
            dataList[i][2]=String.valueOf(CP);
            dataList[i][3]=String.valueOf(Cpl);
            dataList[i][4]=String.valueOf(Cpu);
            dataList[i][5]=String.valueOf(Ppk);
            dataList[i][6]=String.valueOf(PP);
            dataList[i][7]=String.valueOf(Ppl);
            dataList[i][8]=String.valueOf(Ppu);
            dataList[i][9]=String.valueOf(avg_total);
            dataList[i][10]=String.valueOf(R);
            dataList[i][11]=String.valueOf(standardDeviation);
            dataList[i][12]=String.valueOf(SD);
            dataList[i][13]=String.valueOf(max_total);
            dataList[i][14]=String.valueOf(min_total);
            dataList[i][15]=String.valueOf(num);
            ++i;


        }
        try {
            exportEcxcel.ExportNoResponse(sheetName, titleName, fileName,
                    columnNumber, columnWidth, columnName, dataList);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /*导出良品率*/
    public void exporeGoodProductRate(int p_id){
        ExportEcxcel exportEcxcel=new ExportEcxcel();
        String sheetName = "良品率报表";
        String titleName = "良品率报表";
        String fileName = "良品率报表";
        int columnNumber = 4;
        int[] columnWidth = { 30, 15, 15,10 };
        String[] columnName = { "检查项目", "单工序抽检数", "单工序良品数","单工序良品率"};


        SampleDataHandler sampleDataHandler=new SampleDataHandler();
        List<Integer> c_idList=new ArrayList<Integer>();
        List<CheckItem> checkItemList=new ArrayList<CheckItem>();
        checkItemList=checkItemDao.getCheckItemListByPID(p_id);
        for(CheckItem checkItem:checkItemList){
            System.out.println("c_id---->"+checkItem.getC_id());
            c_idList.add(checkItem.getC_id());
        }
        int l=c_idList.size();
        String[][] dataList=new String[l][16];
        int i=0;
        for(int c_id:c_idList) {
            List<List<SampleData>> sampleDataList=new ArrayList<List<SampleData>>();
            sampleDataList=getAllDataInCheckItem(c_id);
            if(getIndicators(c_id).getUSL()!=0&&getIndicators(c_id).getUSL()!=0&&sampleDataList!=null){

                updateSampleStatus(sampleDataList,getIndicators(c_id).getUSL()
                        ,getIndicators(c_id).getLSL());
                System.out.println("样本数据状态已经更新");
            }


            if (checkItemDao.get_CheckItem(c_id)==null)
                continue;
            String p_name = projectDao.get_projectByP_id(p_id).getP_name();
            String itemName = checkItemDao.get_CheckItem(c_id).getC_name();
            int passCount=sampleDataList==null?-9999:checkItemDao.getDataCountsBystatus(c_id,1);
            int allCount=sampleDataList==null?-9999:checkItemDao.get_AllDataInCheckItem(c_id).size();
            double passRate=sampleDataList==null?-9999:sampleDataHandler.get_stanardData((double)passCount/allCount);


            dataList[i][0]=p_name+"/"+itemName;
            dataList[i][1]=String.valueOf(passCount);
            dataList[i][2]=String.valueOf(allCount);
            dataList[i][3]=String.valueOf(passRate);
            ++i;
        }

        try {
            exportEcxcel.ExportNoResponse(sheetName, titleName, fileName,
                    columnNumber, columnWidth, columnName, dataList);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /* 更新表中数据*/
    public void updateDataIntable(List<SampleData> list){
        System.out.println("参数传入样本id："+list);
        for(SampleData sampleData:list){
            System.out.println("参数传入样本id："+sampleData.getId()+"  值："+sampleData.getValue());

            sampleDataDao.updateSD_Value(sampleData.getId(),sampleData.getValue());
        }
    }

}
