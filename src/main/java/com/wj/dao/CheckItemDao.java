package com.wj.dao;

import com.wj.domain.CheckItem;
import com.wj.domain.D_Group;
import com.wj.domain.Project;
import com.wj.domain.SampleData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CheckItemDao {
    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    private final static String INSERT_ITEM_SQL="INSERT INTO checked_item (p_id , c_name , c_status ,current_num, remarks) VALUES (?,?,?,?,?)";
    private final static String UPDATE_CHECKITEM_STATUS_SQL="UPDATE checked_item SET c_status = ? WHERE c_id = ?";
    private final static String UPDATE_ALLCHECKITEM_STATUS_1_TO_2="UPDATE checked_item SET c_status = 2 WHERE c_status = 1";
    private final static String QUERY_ALLChECKITEM_1_OR_2="SELECT * FROM checked_item WHERE p_id = ? AND (c_status =1 OR c_status =2)";
    private final static String GET_G_ID_LIST_SQL="SELECT * From d_group_info WHERE c_id=?";
    private final static String UPDATE_NEST_CHECKITEM_TO_1_SQL="UPDATE checked_item SET c_status=1 WHERE c_id " +
            " = (SELECT c_id FROM (SELECT c_id FROM checked_item WHERE c_status=1 OR c_status=2 ORDER BY c_id DESC " +
            "LIMIT 1) a )";
    private final static String GET_CHECKITEM_SQL="SELECT * FROM checked_item WHERE c_id=?";

    private final static String GET_ALL_DATA_SQL="SELECT * From d_group_info LEFT JOIN sampleData ON d_group_info.g_id = sampleData.g_id WHERE d_group_info.c_id = ?";
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    /*插入检查项目*/
    public void InsertCheckItem(CheckItem checkItem){
        Object[] args = {checkItem.getP_id(),checkItem.getC_name(),checkItem.getC_status(),checkItem.getCurrent_num(),checkItem.getRemarks()};
        try{
            jdbcTemplate.update(INSERT_ITEM_SQL,args);
        }
        catch (Exception ex){
            System.out.println(ex);
        }
    }



    /*更新检查项目状态
    * * c_status 项目状态 ：
    *                   1=当前正在使用中
    *                   2=未使用
    *                   3=将不被使用，软删除*/
    public void Update_Project_Status(int c_id,int status){
        Object[] args = {status,c_id};
        jdbcTemplate.update(UPDATE_CHECKITEM_STATUS_SQL,args);
    }

    /*更新所有检查项由
            正在使用
            到
            未使用
     */
    public void Update_AllCheckItem_1_to_2(){
        jdbcTemplate.execute(UPDATE_ALLCHECKITEM_STATUS_1_TO_2);
    }

    /*获取所有可以使用或正在使用的项目信息
    * */
    public List<CheckItem> Query_AllCheckItems_Info(int p_id){
        Object[] args = {p_id};
        List<CheckItem> checkItems=new ArrayList<CheckItem>();
        try {
            checkItems=jdbcTemplate.query(QUERY_ALLChECKITEM_1_OR_2,args,new BeanPropertyRowMapper<CheckItem>(CheckItem.class));
        }
        catch (Exception ex){
            System.out.println(ex);
        }
        return  checkItems;
    }
    /*获取该检查项目下所有样本数据对象
     */
    public List<SampleData> Get_AllDataInCheckItem(int c_id){
        Object[] args = {c_id};
        List<SampleData> sampleDataList = new ArrayList<SampleData>();
        sampleDataList=jdbcTemplate.query(GET_ALL_DATA_SQL,args,new BeanPropertyRowMapper<SampleData>(SampleData.class));
        return sampleDataList;
    }
    /*获取所有组的id*/
    public List<D_Group> get_Group_In_CheckItem(int c_id){
        Object[] args = {c_id};
        List<D_Group> c_idList=jdbcTemplate.query(GET_G_ID_LIST_SQL,args,new BeanPropertyRowMapper<D_Group>(D_Group.class));
        return c_idList;
    }

    /*获取检查项目*/
    public CheckItem get_CheckItem(int c_id){
        Object[] args = {c_id};
        List<CheckItem> checkItem=jdbcTemplate.query(GET_CHECKITEM_SQL,args,new BeanPropertyRowMapper<CheckItem>(CheckItem.class));
        if(checkItem.isEmpty())
            return null;
        else
            return checkItem.get(0);
    }

}
