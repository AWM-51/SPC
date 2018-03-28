package com.wj.dao;

import com.wj.domain.Indicators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IndicatorsDao {
    private JdbcTemplate jdbcTemplate = new JdbcTemplate();
    private final static String INSERT_INDICATORS_INFO_SQL="INSERT INTO indicators_info(c_id,USL,LSL,tragetValue) VALUES (?,?,?,?)";
    private final static String UPDATE_INDICATORS_INFO_SQL="UPDATE indicators_info SET USL = ? , LSL = ?,tragetValue=? WHERE c_id = ?";
    private final static String SELECT_INDICATORS_INFO_SQL="SELECT * FROM indicators_info WHERE c_id = ?";

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    /*插入指标信息*/
    public void insertIndicatorInfo(Indicators indicators) {
        Object[] args={indicators.getC_id(),indicators.getUSL(),indicators.getLSL(),indicators.getTargetValue()};
        jdbcTemplate.update(INSERT_INDICATORS_INFO_SQL,args);
    }

    /*更新指标信息*/
    public void updateIndicatorInfo(Indicators indicators){
        Object[] args={indicators.getUSL(),indicators.getLSL(),indicators.getTargetValue(),indicators.getC_id()};
        jdbcTemplate.update(UPDATE_INDICATORS_INFO_SQL,args);
    }

    /*查询指标信息
    * 只搜索1条*/
    public List<Indicators> getIndicatorInfo(int c_id){
        Object[] args={c_id};
        return  jdbcTemplate.query(SELECT_INDICATORS_INFO_SQL, args, new ResultSetExtractor<List<Indicators>>() {
            @Override
            public List<Indicators> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<Indicators> list = new ArrayList<Indicators>();

                while (rs.next()) {

                    Indicators indicators = new Indicators();

                    indicators.setId(rs.getInt("id"));

                    indicators.setC_id(rs.getInt("c_id"));

                    indicators.setLSL(rs.getDouble("LSL"));

                    indicators.setUSL(rs.getDouble("USL"));

                    indicators.setTargetValue(rs.getDouble("tragetValue"));

                    list.add(indicators);

                }

                return  list;
            }
        });
    }

}
