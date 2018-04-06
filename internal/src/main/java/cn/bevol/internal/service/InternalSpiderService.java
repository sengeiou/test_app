package cn.bevol.internal.service;

import cn.bevol.util.response.ReturnData;
import cn.bevol.internal.dao.mapper.SqlOldMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by mysens on 17-6-19.
 */
@Service
public class InternalSpiderService {
    private String[] result;
    private final Integer[] dataTypes = {1,2,3,6};
    private final String fields = "new,property,composition,manual,state,invalid_state,error,no_change,multiple_composition,state_hide,state_not_hide,main_composition";
    @Resource
    private SqlOldMapper sqlOldMapper;

    {
        result = fields.split(",");
    }

    public ReturnData statisticSpiderInto(){
        try {
            long current = System.currentTimeMillis();
            long today = (current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getTimeZone("GMT+8:00").getRawOffset()) / 1000;
            long yesterday = today - 24 * 60 * 60;

            String checkSql = "select id from hq_spider_statistic where `date` = " + yesterday;
            List<Map<String, Object>> statisticResult = sqlOldMapper.select(checkSql);

            if (statisticResult.size() == 0) {
                for (Integer dataType : dataTypes) {
                    String dataTypeWhere = "where `data_type`=" + dataType;
                    Integer allTotal = 0;
                    StringBuilder fieldsValue = new StringBuilder();
                    for (int i = 0; i < result.length; i++) {
                        String where = dataTypeWhere + " and `result`=" + (i + 1) + " and `time` between " + yesterday + " and " + today;
                        String selectSql = "select count(*) as total from hq_spider_log " + where;
                        List<Map<String, Object>> totalList = sqlOldMapper.select(selectSql);
                        Integer total = Integer.parseInt(totalList.get(0).get("total").toString());
                        allTotal += total;
                        fieldsValue.append(total).append(",");
                    }

                    String insertSql = "insert into hq_spider_statistic (" + fields + ",total,data_type,date)" +
                            "values (" + fieldsValue + allTotal + "," + dataType + "," + yesterday + ")";
                    sqlOldMapper.insert(insertSql);
                }
            }
            return ReturnData.SUCCESS;
        }catch(Exception e){
            e.printStackTrace();
            return ReturnData.ERROR;
        }


    }
}
