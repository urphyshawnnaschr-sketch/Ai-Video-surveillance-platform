package com.yihecode.camera.ai.entity.map.handler;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yihecode.camera.ai.entity.map.MapRuleConfig;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DoubleTypeHandler extends BaseTypeHandler<List<Double>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Double> parameter, JdbcType jdbcType) throws SQLException {
        if(parameter != null) {
            ps.setString(i, JSON.toJSONString(parameter));
        }
    }

    @Override
    public List<Double> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return this.parse(value);
    }

    @Override
    public List<Double> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return this.parse(value);
    }

    @Override
    public List<Double> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return this.parse(value);
    }

    /**
* Data Convert
* @param str
* @return
*/
    private List<Double> parse(String str) {
        List<Double> doubleList = new ArrayList<>();
        if(StrUtil.isBlank(str)) {
            return doubleList;
        }

        try {
            JSONArray array = JSON.parseArray(str);
            int len = array.size();
            for(int i = 0; i < len; i++) {
                doubleList.add(array.getDoubleValue(i));
            }
        } catch (Exception e) {
            //
}
return doubleList;
}
}
