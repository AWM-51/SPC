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


    /*
    * 上传成功excel,并记录日志
    *
    * */
    @Transactional
    public int uploadExcelSuccess(Project project,MultipartFile file, User user) throws IOException, ParseException {
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

        try{
            List<List<SampleData>> sampleDatas = readExcel.readXls(file,entyrTime);//读入表格
            int datanum= sampleDatas.size();//样本数据组数量
            do_ExcelUpload_Log(entyrTime,uploadUserId,fileName,datanum);//将上传日志相关信息传入数据库
            uploadSDExcelLog=sampleDataDao.selectUploadExcelID(entyrTime);//目的是从数据库中将与entryTime相同的日志取出，使用他的id

            for(List<SampleData> sd : sampleDatas){



//                sd.setAverage_value(sampleDataHandler.get_stanardData(sampleDataHandler.get_average(sd)));//在SampleData插入数据库前就将平均值算出。
//                sd.setVariance(sampleDataHandler.get_stanardData(sampleDataHandler.get_variance(sd)));//在SampleData插入数据库前就将方差算出。
//                sd.setStandard_Deviation(sampleDataHandler.get_stanardData(sampleDataHandler.get_standard_Deviation(sd)));//在SampleData插入数据库前就将标准差算出。
//
//                sampleDataDao.insertSampleData(sd,uploadSDExcelLog.getUploadSDExcel_log_id());//插入数据至数据库
            }
        }catch (NullPointerException ex){
            System.out.println(ex);

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
    * 拉取用户下所有项目信息列表
    * */
    public List<CheckItem> get_All_CheckItemInfoList(int p_id){
        List<CheckItem> checkItems = new ArrayList<CheckItem>(checkItemDao.Query_AllCheckItems_Info(p_id));

        return checkItems;
    }

    /*删除某用户某项目，实际为该状态
    */

    public void deleteCheckItem(int c_id){
        System.out.println("删除的项目id》》》"+c_id);

        projectDao.Update_Project_Status(c_id,0);
        projectDao.Update_NewestProjectsTo1();
    }




}
