package com.wj.domain;

import org.springframework.jdbc.core.RowMapper;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
* p_id 项目id
* u_id 项目创建人
* creat_time 项目创建时间
* project_name项目名称 默认设置为 project+时间戳
* p_status 项目状态 ：
*                   1=当前正在使用中
*                   2=未使用
*                   3=将不被使用，软删除
* */
public class Project implements RowMapper<Project>, Serializable {
    private int p_id;
    private int u_id;
    private String create_time;
    private String p_name;
    private int p_status;
    private int current_num;
    private String remarks;


    public int getP_id() {
        return p_id;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public int getU_id() {
        return u_id;
    }

    public void setU_id(int u_id) {
        this.u_id = u_id;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getP_name() {
        return p_name;
    }

    public void setP_name(String p_name) {
        this.p_name = p_name;
    }

    public int getP_status() {
        return p_status;
    }

    public int getCurrent_num() {
        return current_num;
    }

    public void setCurrent_num(int current_num) {
        this.current_num = current_num;
    }


    public void setP_status(int p_status) {
        this.p_status = p_status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    @Override
    public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
        Project project = new Project();
        project.setP_id(rs.getInt(p_id));
        project.setU_id(rs.getInt(u_id));
        project.setP_name(rs.getString(p_name));
        project.setCreate_time(rs.getTime(create_time).toString());
        project.setP_status(rs.getInt(p_status));
        project.setRemarks(rs.getString(remarks));
        return project;
    }


}
