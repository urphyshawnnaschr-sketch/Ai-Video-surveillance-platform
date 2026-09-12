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

public class MapRuleConfigListTypeHandler extends BaseTypeHandler<List<MapRuleConfig>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<MapRuleConfig> parameter, JdbcType jdbcType) throws SQLException {
        if(parameter != null) {
            ps.setString(i, JSON.toJSONString(parameter));
        }
    }

    @Override
    public List<MapRuleConfig> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return this.parse(value);
    }

    @Override
    public List<MapRuleConfig> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return this.parse(value);
    }

    @Override
    public List<MapRuleConfig> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return this.parse(value);
    }

    /**
* Data Convert
* @param str
* @return
*/
    private List<MapRuleConfig> parse(String str) {
        List<MapRuleConfig> mapRuleConfigList = new ArrayList<>();
        if(StrUtil.isBlank(str)) {
            return mapRuleConfigList;
        }

        try {
            JSONArray array = JSON.parseArray(str);
            int len = array.size();
            for(int i = 0; i < len; i++) {
                JSONObject object = array.getJSONObject(i);
                String color = object.getString("color");
                String name = object.getString("name");
                Integer min = object.getInteger("min");
                Integer max = object.getInteger("max");


                MapRuleConfig mapRuleConfig = new MapRuleConfig();
                mapRuleConfig.setColor(StrUtil.isBlank(color) ? "#999999" : color);
                mapRuleConfig.setName(StrUtil.isBlank(name) ? "Unknown" : name);
                mapRuleConfig.setMin(min == null ? 0 : min);
                mapRuleConfig.setMax(max == null ? 0 : max);
                mapRuleConfigList.add(mapRuleConfig);
            }
        } catch (Exception e) {
            //
}
return mapRuleConfigList;
}
}
