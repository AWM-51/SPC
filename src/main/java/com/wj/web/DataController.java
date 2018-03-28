package com.wj.web;

import com.sun.org.apache.xpath.internal.operations.Mod;
import com.wj.domain.*;
import com.wj.service.DataService;
import com.wj.util.SampleDataHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DataController {
    @Autowired
    private DataService dataService;

    @RequestMapping(value = "/entryExcel.html")
    public ModelAndView uploadExcel(HttpServletRequest request,HttpServletResponse response
            ,MultipartFile  file,int c_id,int p_id) throws IOException, ParseException {
        User user= (User) request.getSession().getAttribute("user");
        String info=dataService.uploadExcelSuccess(c_id,file,user)==1?"upload success":"upload fail";

        List<List<SampleData>> sampleDataList = dataService.getAllDataInCheckItem(c_id);
        CheckItem checkItem1 = dataService.get_CheckItem(c_id);
        double USL=dataService.getIndicators(c_id).getUSL();
        double LSL=dataService.getIndicators(c_id).getLSL();
        List<CheckItem> checkItems=dataService.get_All_CheckItemInfoList(p_id);
        request.setAttribute("USL",USL);
        request.setAttribute("LSL",LSL);
        request.setAttribute("showedCheckItem",checkItem1);
        request.setAttribute("checkItems",checkItems);
        request.setAttribute("sampleDataList",sampleDataList);
        request.setAttribute("selected_p_id",p_id);
        request.setAttribute("selected_c_id",c_id);

        return new ModelAndView("checkItem","info",info);
    }
    @Transactional
    @RequestMapping(value = "/addNewProject.html")
    public ModelAndView addNewProject(int u_id,String p_name,String remarks,HttpServletRequest request,HttpServletResponse response) throws ParseException {
        dataService.creat_newProject(u_id,p_name,remarks);

        List<Project> projects = new ArrayList<Project>();
        projects=dataService.get_All_ProjectInfoList(u_id);//获取该用户的所有项目，用于刷新
        System.out.println("添加的项目属于》》》》u_id="+u_id);

        if (!projects.isEmpty())
            request.getSession().setAttribute("projects",projects);
        return new ModelAndView("main");
    }

    @RequestMapping(value = "/deleteProject.html")
    public ModelAndView deleteProject(HttpServletRequest request,DataCommand dataCommand, BindingResult bindingResult){

        if( bindingResult.hasErrors())
        {
            System.out.println( "There are errors! {}"+bindingResult);
            return new ModelAndView("main");
        }
        System.out.println("删除的项目名称为》》》》"+dataCommand.getU_id());
        dataService.deleteProject(dataCommand.getProject().getP_id());//删除传入的project

        List<Project> projects = new ArrayList<Project>();
        projects=dataService.get_All_ProjectInfoList(dataCommand.getU_id());//获取该用户的所有项目，用于刷新
        if (!projects.isEmpty())
            request.getSession().setAttribute("projects",projects);
        return new ModelAndView("main");
    }

    @RequestMapping(value = "/addNewCheckItem.html")
    public ModelAndView addNewCheckItem(HttpServletRequest request,int p_id,String c_name,String c_remarks){
        try {

            dataService.creat_newCheckItem(p_id,c_name,c_remarks);
            List<CheckItem>checkItems =new ArrayList<CheckItem>();
            checkItems = dataService.get_All_CheckItemInfoList(p_id);
            System.out.println("添加的检查项目属于》》》》p_id="+p_id);
            if(!checkItems.isEmpty())
                request.setAttribute("checkItems",checkItems);
            request.setAttribute("selected_p_id",p_id);
            request.setAttribute("c_id",dataService.getNestCheckItem(p_id).getC_id());
            return new ModelAndView("checkItem");
        } catch (ParseException e) {
            e.printStackTrace();
            return new ModelAndView("checkItem");//发生错误也返回main页
        }
    }

    @RequestMapping(value = "/showCheckItem.html")
    public ModelAndView showCheckItem(HttpServletRequest request , int p_id){
        dataService.clickAtProject(p_id);//点击事件
        List<CheckItem> checkItems = new ArrayList<CheckItem>();
        checkItems=dataService.get_All_CheckItemInfoList(p_id);
        int selected_c_id=dataService.getNestCheckItem(p_id).getC_id();
        if (checkItems.isEmpty()){
            request.setAttribute("is_null",1);
            request.setAttribute("selected_p_id",p_id);
            return new ModelAndView("checkItem");
        }
        Indicators indicatorsInDB=dataService.getIndicators(selected_c_id);
        request.setAttribute("indicatorsInDB",indicatorsInDB);
        request.setAttribute("is_null",0);
        request.setAttribute("selected_c_id",selected_c_id);
        request.setAttribute("checkItems",checkItems);
        request.setAttribute("selected_p_id",p_id);
        return new ModelAndView("checkItem");
    }

    @RequestMapping (value = "/deleteChtekItem.html")
    public ModelAndView deleteCheckItem(HttpServletRequest request ,int c_id,int p_id){
        dataService.deleteCheckItem(c_id,p_id);
        List<CheckItem> checkItems = new ArrayList<CheckItem>();
        checkItems=dataService.get_All_CheckItemInfoList(p_id);
        request.setAttribute("checkItems",checkItems);
        request.setAttribute("selected_p_id",p_id);
        return new ModelAndView("checkItem");
    }


    @RequestMapping(value = "/uploadIndicatiorsInfo.html")
    public ModelAndView uploadIndicatiorsInfo(HttpServletRequest request,int selected_p_id, int selected_c_id, double USL, double LSL){
        Indicators indicators = new Indicators();
        Double U = (USL+LSL)*0.5000;
        indicators.setUSL(USL);
        indicators.setLSL(LSL);
        indicators.setTargetValue(U);
        indicators.setC_id(selected_c_id);
//        System.out.println(USL+"---"+LSL+"----"+selected_c_id);
        dataService.uploadOrAddIndicatorsInfo(selected_c_id,indicators);//更新信息

        List<CheckItem> checkItems = new ArrayList<CheckItem>();
        checkItems=dataService.get_All_CheckItemInfoList(selected_p_id);
        if (checkItems.isEmpty()){
            request.setAttribute("is_null",1);
            request.setAttribute("selected_p_id",selected_p_id);
            return new ModelAndView("checkItem");
        }

        List<List<SampleData>> sampleDataList = dataService.getAllDataInCheckItem(selected_c_id);

        CheckItem checkItem = dataService.get_CheckItem(selected_c_id);
        Indicators indicatorsInDB=dataService.getIndicators(selected_c_id);
        request.setAttribute("indicatorsInDB",indicatorsInDB);
        request.setAttribute("showedCheckItem",checkItem);
        request.setAttribute("sampleDataList",sampleDataList);

        request.setAttribute("is_null",0);
        request.setAttribute("selected_c_id",selected_c_id);
        request.setAttribute("checkItems",checkItems);
        request.setAttribute("selected_p_id",selected_p_id);
        return new ModelAndView("checkItem");
    }



    @RequestMapping (value = "/showDataTable.html")
    public ModelAndView showDataTable(HttpServletRequest request ,int c_id,int p_id){
        dataService.clickAtCheckItem(c_id);//点击转换状态
        List<List<SampleData>> sampleDataList = dataService.getAllDataInCheckItem(c_id);

        CheckItem checkItem = dataService.get_CheckItem(c_id);
        List<CheckItem> checkItems=dataService.get_All_CheckItemInfoList(p_id);
        Indicators indicatorsInDB=dataService.getIndicators(c_id);
        request.setAttribute("indicatorsInDB",indicatorsInDB);
        request.setAttribute("showedCheckItem",checkItem);
        request.setAttribute("checkItems",checkItems);
        request.setAttribute("sampleDataList",sampleDataList);
        request.setAttribute("selected_p_id",p_id);
        request.setAttribute("selected_c_id",c_id);
        return new ModelAndView("checkItem");
    }

    @RequestMapping(value = "/gotoSampleDataRuningTable.html")
    public ModelAndView gotoSampleDataRuningTable(HttpServletRequest request,int c_id,int selected_p_id){
        List<Double> SVlaueList = new ArrayList<Double>();
        List<Integer> xList = new ArrayList<Integer>();
//        List<Integer> StatusList = new ArrayList<Integer>();
        List<SampleData> SList = new ArrayList<SampleData>();
        SList=dataService.getALLSampleDataByCID(c_id);
        SVlaueList=dataService.getYOfSampleDataRunTable(SList);//获取Y轴值
        xList=dataService.getXOfSampleDataRunTable(SList);//获取X轴值
        double USL=dataService.getIndicators(c_id).getUSL();
        double LSL=dataService.getIndicators(c_id).getLSL();
        double U=dataService.getIndicators(c_id).getTargetValue();
        double maxInSampleData=dataService.getMaxInList(SVlaueList);
        SampleDataHandler sampleDataHandler =new SampleDataHandler();
        double cpk=sampleDataHandler.get_CPK(SVlaueList,USL,LSL);


        request.setAttribute("SVlaueList",SVlaueList);
        request.setAttribute("xList",xList);
        request.setAttribute("max",maxInSampleData);
        request.setAttribute("USL",USL);
        request.setAttribute("LSL",LSL);
        request.setAttribute("U",U);
        request.setAttribute("cpk",cpk);
        request.setAttribute("selected_p_id",selected_p_id);
        return new ModelAndView("tables");
    }

    @RequestMapping(value="/gotoAvgRuningTable.html")
    public ModelAndView gotoAvgRuningTable(HttpServletRequest request,int c_id,int selected_p_id){
        List<Double> AVGValueList=new ArrayList<Double>();
        List<String> xList=new ArrayList<String>();
        AVGValueList=dataService.getAVGofValueInGID(c_id);
        xList=dataService.getObtainTimeInGID(c_id);
        Double max=dataService.getMaxInList(AVGValueList);
        double USL=dataService.getIndicators(c_id).getUSL();
        double LSL=dataService.getIndicators(c_id).getLSL();
        double U=dataService.getIndicators(c_id).getTargetValue();



        request.setAttribute("AVGValueList",AVGValueList);
        request.setAttribute("xList",xList);
        request.setAttribute("max",max);
        request.setAttribute("USL",USL);
        request.setAttribute("LSL",LSL);
        request.setAttribute("U",U);
        request.setAttribute("selected_p_id",selected_p_id);
        return new ModelAndView("avgRunningTable");
    }

    @RequestMapping(value="/gotoCPKRunningTable.html")
    public ModelAndView gotoCPKRunningTable(HttpServletRequest request,int c_id , int selected_p_id){
        List<String> xList=new ArrayList<String>();
        List<List<SampleData>> dataInCheckItemByGroup = dataService.getAllDataInCheckItem(c_id);
        double USL=dataService.getIndicators(c_id).getUSL();
        double LSL=dataService.getIndicators(c_id).getLSL();
        List<Double> CPkList = dataService.getCPkList(dataInCheckItemByGroup,USL,LSL);
        xList=dataService.getObtainTimeInGID(c_id);
        Double max=dataService.getMaxInList(CPkList);
        double U=dataService.getIndicators(c_id).getTargetValue();

        request.setAttribute("CPkList",CPkList);
        request.setAttribute("xList",xList);
        request.setAttribute("max",max);
        request.setAttribute("USL",USL);
        request.setAttribute("LSL",LSL);
        request.setAttribute("U",U);
        request.setAttribute("selected_p_id",selected_p_id);
        return new ModelAndView("CPKRunningTable");
    }

    @RequestMapping(value = "/gotoPassRateRunningTable.html")
    public ModelAndView gotoPassRateRunningTable(HttpServletRequest request ,int c_id,int selected_p_id){
        List<String> xList=new ArrayList<String>();
        List<List<SampleData>> dataInCheckItemByGroup = dataService.getAllDataInCheckItem(c_id);
        double USL=dataService.getIndicators(c_id).getUSL();
        double LSL=dataService.getIndicators(c_id).getLSL();
        xList=dataService.getObtainTimeInGID(c_id);

        List<Double> passRateList = dataService.getPassRateList(dataInCheckItemByGroup,USL,LSL);
        request.setAttribute("passRateList",passRateList);
        request.setAttribute("xList",xList);
        request.setAttribute("selected_p_id",selected_p_id);
        return new ModelAndView("passRateRunningTable");
    }

    @RequestMapping(value = "gotoAnalysisTable.html")
    public ModelAndView gotoAnalysisTable(HttpServletRequest request ,int c_id,int selected_p_id){
        return new ModelAndView("");
    }

}
