package com.gr.geias.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.*;

import java.sql.*;
import java.util.Collections;
import java.util.List;

@MappedTypes(List.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class JsonListTypeHandler implements TypeHandler<List<String>> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void setParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType)
            throws SQLException {
        try {
            ps.setString(i, parameter == null ? "[]" : MAPPER.writeValueAsString(parameter));
        } catch (Exception e) {
            throw new SQLException("Error serializing List<String> to JSON", e);
        }
    }

    @Override
    public List<String> getResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        if (json == null) return Collections.emptyList();
        try {
            return MAPPER.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            throw new SQLException("Error deserializing JSON to List<String>", e);
        }
    }

    @Override
    public List<String> getResult(ResultSet rs, int columnIndex) throws SQLException {
        return getResult(rs, rs.getMetaData().getColumnName(columnIndex));
    }

    @Override
    public List<String> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return getResult(cs.getResultSet(), columnIndex);
    }
}
