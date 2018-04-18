package com.wj.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AttributesValueDao {
    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    private final static String SELECT_AVINFO_BY_G_ID_SQL="";
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }
}
