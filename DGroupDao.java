package com.wj.dao;

import com.wj.domain.D_Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DGroupDao {

    private JdbcTemplate jdbcTemplate = new JdbcTemplate();
    private String UPDATE_DGROUP_INFO_SQL="";
    private String INSERT_DGROUP_SQL="INSERT INTO d_group_info ( c_id ,g_status , obtain_time , creat_time ) " +
            "VALUES (?,?,?,?)";
    private String GET_GROUP_COUNT_SQL="SELECT COUNT(*) FROM d_group_info WHERE c_id = ?";
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    /*
    * 插入数据组信息
    * */
    public void InsertInto_DGroupInfo(D_Group d_group){
        Object[] args={d_group.getC_id(),d_group.getG_status(),d_group.getObtain_time(),d_group.getCreate_time()};
        try {
            jdbcTemplate.update(INSERT_DGROUP_SQL,args);
        }
        catch (Exception ex){
            System.out.println(ex);
        }
    }

    /*
    * 更新数据组信息
    * */
    public void Update_DGoupInfo(){}


    /*获取当前检测项目的数据组数*/
    public int get_CurrentCountsFromDGoup(int c_id){
        Object[] args={c_id};
        int count=0;
        try {
            count = jdbcTemplate.queryForObject(GET_GROUP_COUNT_SQL,args,Integer.class);
        }catch (Exception ex){
            System.out.println(ex);
        }
        return count;
    }
}
