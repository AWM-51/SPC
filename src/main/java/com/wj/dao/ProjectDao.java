package com.wj.dao;

import com.wj.domain.Project;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/*ProjectDao
* */
@Repository
public class ProjectDao {
    private JdbcTemplate jdbcTemplate = new JdbcTemplate();
    private final static String INSERT_PROJECT__SQL="INSERT INTO project_info (u_id,create_time,p_name,p_status,current_num,remarks) " +
            " VALUES (?,?,?,?,?,?)";
    private final static String UPDATE_PROJECT_SQL="UPDATE project_info SET u_id=? , create_time=? , p_name=? , p_status=? , current_num=? , remarks=? WHERE p_id = ?";
    private final static String UPDATE_PROJECT_STATUS_SQL="UPDATE project_info SET p_status = ? WHERE p_id = ?";
    private final static String UPDATE_ALLPROJECTS_STATUS_1_TO_2="UPDATE project_info SET p_status = 2 WHERE p_status = 1";
    private final static String QUERY_ALLPROJECT_1_OR_2="SELECT * FROM project_info WHERE u_id = ? AND (p_status =1 OR p_status =2)";
    private final static String UPDATE_NEST_PROJECT_TO_1_SQL="UPDATE project_info SET p_status=1 WHERE p_id " +
            " = (SELECT p_id FROM (SELECT p_id FROM project_info WHERE p_status=1 OR p_status=2 ORDER BY p_id DESC " +
            "LIMIT 1) a )";



    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    /*
    * 插入项目信息
    * */
    public void Insert_Project_info(Project project){
        Object[] args={project.getU_id(),project.getCreate_time(),project.getP_name(),project.getP_status(),project.getCurrent_num(),project.getRemarks()};
        try {
            jdbcTemplate.update(INSERT_PROJECT__SQL,args);
        }
        catch (Exception e){
            System.out.println(project.getU_id()+"!!!!"+project.getP_name()+"#########"+e);
        }
    }

    /*更新project整体信息*/
    public void Update_Project(Project project){
        Object[] args={project.getU_id(),project.getCreate_time(),project.getP_name(),project.getP_status(),project.getCurrent_num(),project.getRemarks(),project.getP_id()};
        try {
            jdbcTemplate.update(UPDATE_PROJECT_SQL,args);
        }
        catch (Exception ex){
            System.out.println(project.getP_name());
        }
    }

    /*更新项目状态
    * * p_status 项目状态 ：
    *                   1=当前正在使用中
    *                   2=未使用
    *                   3=将不被使用，软删除*/
    public void Update_Project_Status(int p_id,int status){
        Object[] args = {status,p_id};
        jdbcTemplate.update(UPDATE_PROJECT_STATUS_SQL,args);
    }




    /*更新所有项目由
            正在使用
            到
            未使用
     */
    public void Update_AllProjects_1_to_2(){
        jdbcTemplate.execute(UPDATE_ALLPROJECTS_STATUS_1_TO_2);
    }


    /*获取所有可以使用或正在使用的项目信息
    * */
    public List<Project> Query_AllProjects_Info(int u_id){
        @SuppressWarnings("unchecked")
        Object[] args = {u_id};
        List<Project> projects = new ArrayList<Project>(jdbcTemplate.query(QUERY_ALLPROJECT_1_OR_2,args,new BeanPropertyRowMapper<Project>(Project.class)));
        return  projects;
    }

    /*更新为删除（1，2）项目组最新项目状态为1*/
    public void Update_NewestProjectsTo1(){
        jdbcTemplate.execute(UPDATE_NEST_PROJECT_TO_1_SQL);
    }
}
