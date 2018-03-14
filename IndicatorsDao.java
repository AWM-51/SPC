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
    private final static String INSERT_INDICATORS_INFO_SQL="INSERT INTO Indicators_info(USL,LSL) VALUES (?,?)";
    private final static String UPDATE_INDICATORS_INFO_SQL="UPDATE Indicators_info SET USL = ? , LSL = ? WHERE id = ?";
    private final static String SELECT_INDICATORS_INFO_SQL="SELECT * FROM Indicatots_info WHERE id = ?";

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    /*插入指标信息*/
    public void insertIndicatorInfo(Indicators indicators) {
        Object[] args={indicators.getUSL(),indicators.getLSL()};
        jdbcTemplate.update(INSERT_INDICATORS_INFO_SQL,args);
    }

    /*更新指标信息*/
    public void updateIndicatorInfo(Indicators indicators){
        Object[] args={indicators.getUSL(),indicators.getLSL(),indicators.getId()};
        jdbcTemplate.update(UPDATE_INDICATORS_INFO_SQL,args);
    }

    /*查询指标信息
    * 只搜索1条*/
    public Indicators getIndicatorInfo(int id){
        Object[] args={id};
        return jdbcTemplate.query(SELECT_INDICATORS_INFO_SQL, args, new ResultSetExtractor<Indicators>() {
            @Override
            public Indicators extractData(ResultSet rs) throws SQLException, DataAccessException {
                List list = new ArrayList();

                while (rs.next()) {

                    Indicators indicators = new Indicators();

                    indicators.setId(rs.getInt("id"));

                    indicators.setLSL(rs.getDouble("LSL"));

                    indicators.setUSL(rs.getDouble("USL"));

                    list.add(indicators);

                }

                return (Indicators) list.get(0);
            }
        });
    }

}
