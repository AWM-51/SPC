package com.wj.dao;

import com.wj.domain.SampleData;
import com.wj.domain.UploadSDExcelLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class SampleDataDao {
    private JdbcTemplate jdbcTemplate;
    private static String INSERT_SAMPLE_DATA_SQL=" INSERT INTO sampleData(g_id,obtain_time,value,s_status) VALUE(?,?,?,?) ";
    private static String SELECT_UPLOADEXCEL_LOG_SQL="SELECT * from uploadSDExcel_log where upload_time = ?";
    private static String UPDATE_SAMPLEDATA_STATUS_SQL="UPDATE sampleData SET s_status = ? WHERE id =?";
    private static String SELECT_SAMPLEDATA_BT_C_ID_SQL="SELECT * FROM sampleData LEFT JOIN d_group_info ON d_group_info.g_id = sampleData.g_id WHERE d_group_info.c_id = ? ";
    private static String GET_AVG_BY_GID="SELECT AVG(value),d_group_info.obtain_time FROM sampleData LEFT JOIN d_group_info ON d_group_info.g_id = sampleData.g_id WHERE d_group_info.c_id = ? GROUP BY d_group_info.g_id";


    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate){this.jdbcTemplate=jdbcTemplate;}

    public void insertSampleData(SampleData sampleData){
        Object[] arg={sampleData.getG_id(),sampleData.getObtain_time(),sampleData.getValue(),sampleData.getS_status()};
         jdbcTemplate.update(INSERT_SAMPLE_DATA_SQL,arg);
    }

    public UploadSDExcelLog selectUploadExcelID(Date data){
        final UploadSDExcelLog uploadSDExcelLog= new UploadSDExcelLog();
        jdbcTemplate.query(SELECT_UPLOADEXCEL_LOG_SQL,new Object[]{data}, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                uploadSDExcelLog.setUploadSDExcel_log_id(resultSet.getInt("uploadSDExcel_log_id"));
                uploadSDExcelLog.setUpload_user_id(resultSet.getInt("upload_user_id"));
                uploadSDExcelLog.setData_num(resultSet.getInt("data_num"));
                uploadSDExcelLog.setExcel_name(resultSet.getString("excel_name"));
            }
        });
        return uploadSDExcelLog;
    }

    /*获取c_id=?所有样本数据*/
    public List<SampleData> get_SampleDataListInDB(int c_id){
        Object[] args={c_id};

        return jdbcTemplate.query(SELECT_SAMPLEDATA_BT_C_ID_SQL, args,new ResultSetExtractor<List<SampleData>>(){
            @Override
            public List<SampleData> extractData(ResultSet resultSet)throws SQLException,DataAccessException {
                List<SampleData> list = new ArrayList<SampleData>();

                while (resultSet.next()){

                    SampleData sampleData=new SampleData();

                    sampleData.setId(resultSet.getInt("id"));

                    sampleData.setG_id(resultSet.getInt("g_id"));

                    sampleData.setValue(resultSet.getDouble("value"));

                    sampleData.setS_status(resultSet.getInt("s_status"));

                    sampleData.setObtain_time(resultSet.getString("obtain_time"));

                    list.add(sampleData);
                }
                return list;
            }

        });

    }

    /*获取每组的平均值*/
    public List<Double> get_AVGofValueByG_id(int c_id){
        Object[] args={c_id};
        return jdbcTemplate.query(GET_AVG_BY_GID, args,new ResultSetExtractor<List<Double>>(){
            @Override
            public List<Double> extractData(ResultSet resultSet)throws SQLException,DataAccessException {
                List<Double> list = new ArrayList<Double>();

                while (resultSet.next()){
                    list.add(resultSet.getDouble("AVG(value)"));
                }
                return list;
            }

        });

    }

    /*获取 每组的抽检时间*/
    public List<String> get_ObtainTimeofValueByG_id(int c_id){
        Object[] args={c_id};
        return jdbcTemplate.query(GET_AVG_BY_GID, args,new ResultSetExtractor<List<String>>(){
            @Override
            public List<String> extractData(ResultSet resultSet)throws SQLException,DataAccessException {
                List<String> list = new ArrayList<String>();

                while (resultSet.next()){
                    list.add(resultSet.getString("obtain_time"));
                }
                return list;
            }

        });

    }

    /*更新样本数据的状态*/
    public void upataeSD_Status(int id,int status){
        Object[] args={status,id};

        //jdbcTemplate.update(UPDATE_SAMPLEDATA_STATUS_SQL,args);
        System.out.println("id:"+id+" status:"+status+"   "+jdbcTemplate.update(UPDATE_SAMPLEDATA_STATUS_SQL,args));
    }


}
