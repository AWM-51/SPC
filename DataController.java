package com.wj.web;

import com.wj.domain.Project;
import com.wj.domain.User;
import com.wj.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
            ,MultipartFile  file) throws IOException, ParseException {
        User user= (User) request.getSession().getAttribute("user");
        String info=dataService.uploadExcelSuccess(file,user)==1?"upload success":"upload fail";

        return new ModelAndView("main","info",info);
    }

    @RequestMapping(value = "/addNewProject.html")
    public ModelAndView addNewProject(int u_id,String p_name,String remarks,HttpServletRequest request,HttpServletResponse response) throws ParseException {
        dataService.creat_newProject(u_id,p_name,remarks);

        List<Project> projects = new ArrayList<Project>();
        projects=dataService.get_All_ProjectInfoList(u_id);//获取该用户的所有项目，用于刷新
        System.out.println("添加的项目属于》》》》u_id="+u_id);
        //request.getSession().setAttribute("user",user);

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
}
