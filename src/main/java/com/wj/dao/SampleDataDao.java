package com.wj.dao;

import com.wj.domain.SampleData;
import com.wj.domain.UploadSDExcelLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

@Repository
public class SampleDataDao {
    private JdbcTemplate jdbcTemplate;
    private static String INSERT_SAMPLE_DATA_SQL=" INSERT INTO sampleData(g_id,obtain_time,value,s_status) VALUE(?,?,?,?) ";
    private static String SELECT_UPLOADEXCEL_LOG_SQL="SELECT * from uploadSDExcel_log where upload_time = ?";

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


}
