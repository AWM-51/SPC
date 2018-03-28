package com.wj.service;

import com.wj.dao.*;
import com.wj.domain.*;
import com.wj.util.ReadExcel;
import com.wj.util.SampleDataHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

        checkItemDao.Update_CheckItem_Status(c_id,0);
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
        List<SampleData> sampleDataList = checkItemDao.Get_AllDataInCheckItem(c_id);
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
//        List<Indicators>IList=new ArrayList<Indicators>();
//        IList=indicatorsDao.getIndicatorInfo(c_id);
//        if(IList.size()==0)
//            return null;
//        else
//            return  IList.get(0);
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
        if (sampleData.size()==0)
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

    /*获取样本运行图的y坐标*/
    public List<Double> getYOfSampleDataRunTable(List<SampleData> sampleData){
        List<Double> SVlaueList = new ArrayList<Double>();
        for (SampleData sd :sampleData){
            SVlaueList.add(sd.getValue());
        }
        return SVlaueList;
    }

    //获取数组中最大值
    public double getMaxInList(List<Double> list){
        double max;
        max=list.get(0);
        for(Double l :list){
            if(l.doubleValue()>max)
                max=l.doubleValue();
        }
        System.out.println("该数列最大值：------>"+max);
        return max;
    }

    //获取CPk列表
    public List<Double> getCPkList(List<List<SampleData>> lists,Double USL,Double LSL){
        SampleDataHandler sampleDataHandler = new SampleDataHandler();
        List<Double> CPkList =new ArrayList<Double>();

        for(List<SampleData> sd : lists){
            List<Double> tempList =new ArrayList<Double>();
            for(SampleData s:sd){
                tempList.add(s.getValue());
                System.out.println("将数据导入temp--->"+s.getValue());
            }
            System.out.println("temp--->"+tempList.size()+"---"+USL+"---"+LSL);
            CPkList.add(sampleDataHandler.get_CPK(tempList,USL,LSL));
        }
//        for(Double i : CPkList){
//            System.out.println("CPK-->"+i);
//        }
        return CPkList;
    }
    /*将所有数据状态进行更新
    * 1：已经输入，已处理，合格 2：已经输入，未处理 0：已经输入 ，已经处理，不合格 4：已经输入，已删除*/
    public void updateSampleStatus(List<List<SampleData>> lists,double USL,double LSL){
        for(List<SampleData> sampleDataList :lists){
            for (SampleData sd:sampleDataList){
                double value=sd.getValue();
                if(value>=LSL&&value<=USL){
                    sampleDataDao.upataeSD_Status(sd.getId(),1);
                }
                else {
                    sampleDataDao.upataeSD_Status(sd.getId(),0);
                }
            }
        }
    }

    /*计算兵备道合格率列表*/
    public List<Double> getPassRateList(List<List<SampleData>>lists,double USL ,double LSL){
        SampleDataHandler sampleDataHandler =new SampleDataHandler();
        List<Double> psaaRateList=new ArrayList<Double>();
        for(List<SampleData> sd:lists){
            psaaRateList.add(sampleDataHandler.get_PassRate(sd,USL,LSL));
        }
        return psaaRateList;

    }


}
