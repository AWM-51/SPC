package com.wj.web;

import com.sun.org.apache.xpath.internal.operations.Mod;
import com.wj.domain.*;
import com.wj.service.DataService;
import com.wj.util.CurveFitting;
import com.wj.util.SampleDataHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.plaf.SliderUI;
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
        if(USL!=0&&LSL!=0){
            dataService.updateSampleStatus(sampleDataList,USL,LSL);//上传的时候更新下状态
        }
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
            int selected_c_id=dataService.getNestCheckItem(p_id).getC_id();
            System.out.println("添加的检查项目属于》》》》p_id="+p_id);
            if(!checkItems.isEmpty())
                request.setAttribute("checkItems",checkItems);
            request.setAttribute("selected_p_id",p_id);
            request.setAttribute("selected_c_id",selected_c_id);
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

        if (checkItems.isEmpty()){
            request.setAttribute("is_null",1);
            request.setAttribute("selected_p_id",p_id);
            return new ModelAndView("checkItem");
        }
        int selected_c_id=dataService.getNestCheckItem(p_id).getC_id();
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
        USL=USL>LSL?USL:LSL;
        LSL=LSL<USL?LSL:USL;//后端矫正输入错误
        Double U = (USL+LSL)*0.5000;
        indicators.setUSL(USL);
        indicators.setLSL(LSL);
        indicators.setTargetValue(U);
        indicators.setC_id(selected_c_id);
//        System.out.println(USL+"---"+LSL+"----"+selected_c_id);
        dataService.uploadOrAddIndicatorsInfo(selected_c_id,indicators);//更新信息
        List<SampleData> SList = new ArrayList<SampleData>();
        List<Double> SVlaueList=new ArrayList<Double>();
        List<List<SampleData>>SDInGroup=dataService.getAllDataInCheckItem(selected_c_id);
        SList=dataService.getALLSampleDataByCID(selected_c_id);
        SVlaueList=dataService.getYOfSampleDataRunTable(SList);//
        if(SVlaueList!=null&&SVlaueList.size()!=0){//判断样本数据是否为空
            dataService.updateSampleStatus(SDInGroup,USL,LSL);//更新样本数据状态
        }

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

        if(sampleDataList!=null&&dataService.getIndicators(c_id).getUSL()!=0&&dataService.getIndicators(c_id).getUSL()!=0){

            dataService.updateSampleStatus(sampleDataList,dataService.getIndicators(c_id).getUSL()
                    ,dataService.getIndicators(c_id).getLSL());
            System.out.println("样本数据状态已经更新");
        }
        List<List<SampleData>> sampleDataList2 = dataService.getAllDataInCheckItem(c_id);
        CheckItem checkItem = dataService.get_CheckItem(c_id);
        List<CheckItem> checkItems=dataService.get_All_CheckItemInfoList(p_id);
        Indicators indicatorsInDB=dataService.getIndicators(c_id);
        request.setAttribute("indicatorsInDB",indicatorsInDB);
        request.setAttribute("showedCheckItem",checkItem);
        request.setAttribute("checkItems",checkItems);
        request.setAttribute("sampleDataList",sampleDataList2);
        request.setAttribute("selected_p_id",p_id);
        request.setAttribute("selected_c_id",c_id);
        return new ModelAndView("checkItem");
    }

    @RequestMapping(value = "/gotoSampleDataRuningTable.html")
    public ModelAndView gotoSampleDataRuningTable(HttpServletRequest request,int c_id,int selected_p_id){
        List<Double> SVlaueList = new ArrayList<Double>();
        List<List<SampleData>> allSList=dataService.getAllDataInCheckItem(c_id);
        List<Integer> xList = new ArrayList<Integer>();

        List<SampleData> SList = new ArrayList<SampleData>();
        List<Double> Rlist=new ArrayList<Double>();
        SList=dataService.getALLSampleDataByCID(c_id);
        SVlaueList=dataService.getYOfSampleDataRunTable(SList);//获取Y轴值
        xList=dataService.getXOfSampleDataRunTable(SList);//获取X轴值
        List<Integer> xRlist=dataService.get_XRlist(allSList);
        Rlist=dataService.getRList(allSList);

        double USL=dataService.getIndicators(c_id).getUSL();
        double LSL=dataService.getIndicators(c_id).getLSL();
        double U=dataService.getIndicators(c_id).getTargetValue();
        double maxInSampleData=dataService.getMaxInList(SVlaueList);
        double max2=dataService.getMaxInList(Rlist);
        SampleDataHandler sampleDataHandler =new SampleDataHandler();
        double SDRBD2=allSList==null?0.0:dataService.getSDBy_RBar_D2(Rlist);
//        double test=dataService.getStandardDevicationInTotal(SVlaueList);


        request.setAttribute("SVlaueList",SVlaueList);
        request.setAttribute("RList",Rlist);
        request.setAttribute("xRlist",xRlist);
        request.setAttribute("xList",xList);
        request.setAttribute("max",maxInSampleData);
        request.setAttribute("max2",max2);
        request.setAttribute("USL",USL);
        request.setAttribute("LSL",LSL);
        request.setAttribute("U",U);
        request.setAttribute("SDRBD2",SDRBD2);
//        request.setAttribute("test",test);
//        request.setAttribute("cpk",cpk);
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
        List<Double> Rlist=dataInCheckItemByGroup==null?null:dataService.getRList(dataInCheckItemByGroup);
        List<Double> CPkList = dataInCheckItemByGroup==null?null:dataService.getCPkList(dataInCheckItemByGroup,USL,LSL);
        xList=dataService.getObtainTimeInGID(c_id);
        Double max=dataService.getMaxInList(CPkList);
        Double min =dataService.getMinInList(CPkList);
        double U=dataService.getIndicators(c_id).getTargetValue();


        request.setAttribute("CPkList",CPkList);
        request.setAttribute("xList",xList);
        request.setAttribute("max",max);
        request.setAttribute("min",min);
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


        List<Double> passRateList = dataInCheckItemByGroup==null?null:dataService.getPassRateList(dataInCheckItemByGroup,USL,LSL);
        request.setAttribute("passRateList",passRateList);
        request.setAttribute("xList",xList);
        request.setAttribute("selected_p_id",selected_p_id);
        return new ModelAndView("passRateRunningTable");
    }

    @RequestMapping(value = "gotoAnalysisTable.html")
    public ModelAndView gotoAnalysisTable(HttpServletRequest request ,int c_id,int selected_p_id){
        List<SampleData> SListBySample=dataService.getALLSampleDataByCID(c_id);
        List<List<SampleData>> dataInCheckItemByGroup = dataService.getAllDataInCheckItem(c_id);
        SampleDataHandler sampleDataHandler=new SampleDataHandler();

        if(SListBySample!=null) {
            List<Double> SVlaueList = dataService.getYOfSampleDataRunTable(SListBySample);//获取Y轴值
            List Xlist=dataService.getXOfNormalDistributionChar(SListBySample);

            List<Double> Y = dataService.ChangY(dataService.getYOfNormalDistributionChar(SListBySample),3);//为了和X轴坐标对应，在两边再加上几个0
            double max = dataService.getMaxInList(Y);
        /*统计值*/
            //整体样本总数
            //整体平均值
            //整体最大值
            //整体最小值
            //组内  子组内样本数
            int Num_total = SListBySample.size();
            double avg_total = dataService.getAVGIntotal(SListBySample);
            double max_total = dataService.getMaxInListForSampleData(SListBySample);
            double min_total = dataService.getMinInListForSampleData(SListBySample);
            int num_group = 5;//这是固定的组内最大输入数



        /*常量*/
            //组内 USL
            //组内 LSL
            //组内 U
            double USL = dataService.getIndicators(c_id).getUSL();
            double LSL = dataService.getIndicators(c_id).getLSL();
            double U = dataService.getIndicators(c_id).getTargetValue();

            List<Double> X=dataService.ChangX(Xlist,USL,LSL,5);

        /*计算值*/
            //标准差（整体）
            //正三倍标准差
            //负三倍标准差
            CurveFitting curveFitting=new CurveFitting();
            double[] p=curveFitting.CurveFittingParameters(Xlist,dataService.getYOfNormalDistributionChar(SListBySample));
            double standardDeviation = p[2];
                    //dataService.getStandardDevicationInTotal(SVlaueList);
            double middleValue_total = p[1];
                    //dataService.getMinddleValue(SVlaueList);
            double middleValue_total_AddThreeSD = middleValue_total + 3 * standardDeviation;
            double middleValue_total_DecreaseThreeSD = middleValue_total - 3 * standardDeviation;

            /*工序能力  组内*/
            //CPL 测量过程均值趋近规格下限的程度
            //CP
            //CPU测量过程均值趋近规格上限的程度
            //CPK等于 CPU 与 CPL 中的较小者。
            double Cpl=-99999 ;
            double Cpu=-99999 ;
            double Cpk=-99999;




        /*工序能力  整体*/
            //PPL 测量过程均值趋近规格下限的程度
            //PPU测量过程均值趋近规格上限的程度
            //PPK等于 PPU 与 PPL 中的较小者。
            double Ppl=-99999 ;//初始值为-99999，说明USL，LSL未输入
            double Ppu=-99999 ;
            double Ppk=-99999 ;

        /*其它值*/
            //Ca
            double Ca=-99999 ;

        /*
        * 实测性能*/
            //1PPM 每百万件中有一件是不合格
            //PPM<LSL=
            //PPM>USL=
            //PPM total=
            double PPM_LSL=-99999 ;
            double PPM_USL=-99999 ;
            double PPM=-99999 ;

            if(USL!=0.0||LSL!=0.0){
                /*工序能力  组内*/
                //CPL 测量过程均值趋近规格下限的程度
                //CP
                //CPU测量过程均值趋近规格上限的程度
                //CPK等于 CPU 与 CPL 中的较小者。
                Cpl = dataService.getCPULK(dataInCheckItemByGroup, USL, LSL).get(0);
                Cpu = dataService.getCPULK(dataInCheckItemByGroup, USL, LSL).get(1);
                Cpk = dataService.getCPULK(dataInCheckItemByGroup, USL, LSL).get(2);




        /*工序能力  整体*/
                //PPL 测量过程均值趋近规格下限的程度
                //PPU测量过程均值趋近规格上限的程度
                //PPK等于 PPU 与 PPL 中的较小者。
                Ppl = dataService.get_PPULK(SVlaueList, USL, LSL).get(0);
                Ppu = dataService.get_PPULK(SVlaueList, USL, LSL).get(1);
                Ppk = dataService.get_PPULK(SVlaueList, USL, LSL).get(2);

        /*其它值*/
                //Ca
                Ca = dataService.getCa(SVlaueList, USL, LSL);

        /*
        * 实测性能*/
                //1PPM 每百万件中有一件是不合格
                //PPM<LSL=
                //PPM>USL=
                //PPM total=
                PPM_LSL = dataService.getPPM(SVlaueList, USL, LSL).get(0);
                PPM_USL = dataService.getPPM(SVlaueList, USL, LSL).get(1);
                PPM = dataService.getPPM(SVlaueList, USL, LSL).get(2);
            }


        /*预期性能（组内）*/


        /*预期性能（整体）*/


            request.setAttribute("Num_total", Num_total);
            request.setAttribute("avg_total", avg_total);
            request.setAttribute("max_total", max_total);
            request.setAttribute("min_total", min_total);
            request.setAttribute("num_group", num_group);

            request.setAttribute("USL", USL);
            request.setAttribute("LSL", LSL);
            request.setAttribute("U", U);

            request.setAttribute("standardDeviation", standardDeviation);
            request.setAttribute("middleValue_total", middleValue_total);
            request.setAttribute("middleValue_total_AddThreeSD", middleValue_total_AddThreeSD);
            request.setAttribute("middleValue_total_DecreaseThreeSD", middleValue_total_DecreaseThreeSD);

            request.setAttribute("Ca", Ca);

            request.setAttribute("PPM_LSL", PPM_LSL);
            request.setAttribute("PPM_USL", PPM_USL);
            request.setAttribute("PPM", PPM);

            request.setAttribute("Ppl", Ppl);
            request.setAttribute("Ppu", Ppu);
            request.setAttribute("Ppk", Ppk);

            request.setAttribute("Cpl", Cpl);
            request.setAttribute("Cpu", Cpu);
            request.setAttribute("Cpk", Cpk);


            request.setAttribute("X",X);
            request.setAttribute("Y", Y);
            request.setAttribute("max", max);

        }
        else {
            request.setAttribute("Num_total", null);
            request.setAttribute("avg_total", null);
            request.setAttribute("max_total", null);
            request.setAttribute("min_total", null);
            request.setAttribute("num_group", null);

            request.setAttribute("USL", null);
            request.setAttribute("LSL", null);
            request.setAttribute("U", null);

            request.setAttribute("standardDeviation", null);
            request.setAttribute("middleValue_total", null);
            request.setAttribute("middleValue_total_AddThreeSD", null);
            request.setAttribute("middleValue_total_DecreaseThreeSD", null);

            request.setAttribute("Ca", null);

            request.setAttribute("PPM_LSL", null);
            request.setAttribute("PPM_USL", null);
            request.setAttribute("PPM", null);

            request.setAttribute("Ppl", null);
            request.setAttribute("Ppu", null);
            request.setAttribute("Ppk", null);

            request.setAttribute("Cpl", null);
            request.setAttribute("Cpu", null);
            request.setAttribute("Cpk", null);


            request.setAttribute("Y", null);
            request.setAttribute("max", null);

        }


        request.setAttribute("selected_p_id", selected_p_id);
        return new ModelAndView("CPK_A_table");
    }

    @RequestMapping(value = "/exporeExcelOfProcessCapability.html")
    public ModelAndView exporeExcelOfProcessCapability(HttpServletRequest request, int p_id){
        try {
            dataService.exporeExcelOfProcessCapability(p_id);

        } catch (Exception e) {
            e.printStackTrace();
        }
        List<CheckItem> checkItems = new ArrayList<CheckItem>();
        checkItems=dataService.get_All_CheckItemInfoList(p_id);
        int selected_c_id=dataService.getNestCheckItem(p_id).getC_id();
        request.setAttribute("checkItems",checkItems);
        request.setAttribute("selected_p_id",p_id);
        request.setAttribute("selected_c_id",selected_c_id);
        return new ModelAndView("checkItem");
    }

    @RequestMapping(value = "/exporeGoodProductRate.html")
    public ModelAndView exporeGoodProductRate(HttpServletRequest request,int p_id){
        try {
            dataService.exporeGoodProductRate(p_id);

        } catch (Exception e) {
            e.printStackTrace();
        }
        List<CheckItem> checkItems = new ArrayList<CheckItem>();
        checkItems=dataService.get_All_CheckItemInfoList(p_id);
        int selected_c_id=dataService.getNestCheckItem(p_id).getC_id();
        request.setAttribute("checkItems",checkItems);
        request.setAttribute("selected_p_id",p_id);
        request.setAttribute("selected_c_id",selected_c_id);
        return new ModelAndView("checkItem");
    }

    @RequestMapping(value = "/updateDataIntable.html")
    public ModelAndView updateDataIntable(HttpServletRequest request,GroupDataCommand groupDataCommand,int p_id,int c_id){
        dataService.updateDataIntable(groupDataCommand.sampleDataList);//更新数据

        List<List<SampleData>> sampleDataList = dataService.getAllDataInCheckItem(c_id);

        if(dataService.getIndicators(c_id).getUSL()!=0&&dataService.getIndicators(c_id).getUSL()!=0){

            dataService.updateSampleStatus(sampleDataList,dataService.getIndicators(c_id).getUSL()
                    ,dataService.getIndicators(c_id).getLSL());
            System.out.println("样本数据状态已经更新");
        }
        List<List<SampleData>> sampleDataList2 = dataService.getAllDataInCheckItem(c_id);
        CheckItem checkItem = dataService.get_CheckItem(c_id);
        List<CheckItem> checkItems=dataService.get_All_CheckItemInfoList(p_id);
        Indicators indicatorsInDB=dataService.getIndicators(c_id);
        request.setAttribute("indicatorsInDB",indicatorsInDB);
        request.setAttribute("showedCheckItem",checkItem);
        request.setAttribute("checkItems",checkItems);
        request.setAttribute("sampleDataList",sampleDataList2);
        request.setAttribute("selected_p_id",p_id);
        request.setAttribute("selected_c_id",c_id);
        return new ModelAndView("checkItem");
    }

}
