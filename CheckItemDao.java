package com.wj.dao;

import com.wj.domain.CheckItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CheckItemDao {
    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    private String INSERT_ITEM_SQL="INSERT INTO checked_item (p_id , c_name , c_status , remarks) VALUES (?,?,?,?)";
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    /*插入检查项目*/
    public void InsertCheckItem(CheckItem checkItem){
        Object[] args = {checkItem.getP_id(),checkItem.getC_name(),checkItem.getC_status(),checkItem.getRemarks()};
        try{
            jdbcTemplate.update(INSERT_ITEM_SQL,args);
        }
        catch (Exception ex){
            System.out.println(ex);
        }
    }

    /*更新*/
}
