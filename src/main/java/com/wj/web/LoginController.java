package com.wj.web;

import com.wj.domain.Project;
import com.wj.domain.User;
import com.wj.service.DataService;
import com.wj.service.UserService;
import com.wj.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//
@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @Autowired
    private DataService dataService;

    @RequestMapping(value = {"/","/index.html"})
    public String loginPage(){
        return "login";
    }

    @RequestMapping(value = "/loginCheck.html")
    public ModelAndView loginCheck(HttpServletRequest request,LoginCommand loginCommand) throws Exception,NullPointerException {

        boolean isValidUser =userService.hasMatchUser(loginCommand.getUserName(),loginCommand.getPassword());
        if(!isValidUser){
            MD5Util md5Util=new MD5Util();
            return new ModelAndView("login","error","用户名或密码错误，你输入的账号为"
                    +loginCommand.getUserName()+" 密码为"+loginCommand.getPassword());
        }
        User user=userService.findUserByUsername(loginCommand.getUserName());
        user.setLastIp(request.getLocalAddr());
        user.setLastVisit(new Date());
        userService.loginSuccess(user);

        List<Project> projects = new ArrayList<Project>();
        projects=dataService.get_All_ProjectInfoList(user.getUserId());

        request.getSession().setAttribute("user",user);
        if (!projects.isEmpty())
            request.getSession().setAttribute("projects",projects);
        return new ModelAndView("main");
    }

    @RequestMapping(value = "/gotoMain.html")
    public ModelAndView gotoMain(HttpServletRequest request,int u_id) {
        List<Project> projects = new ArrayList<Project>();
        projects=dataService.get_All_ProjectInfoList(u_id);
        User user = userService.getUserById(u_id);
        request.getSession().setAttribute("user",user);
        if (!projects.isEmpty())
            request.getSession().setAttribute("projects",projects);
        return new ModelAndView("main");
    }


}
