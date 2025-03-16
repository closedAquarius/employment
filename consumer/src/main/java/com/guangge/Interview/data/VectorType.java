package com.guangge.Interview.data;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.postgresql.util.PGobject;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;

public class VectorType implements UserType<float[]> {
    @Override
    public int getSqlType() {
        return Types.OTHER; // 使用 Types.OTHER 表示自定义类型
    }

    @Override
    public Class<float[]> returnedClass() {
        return float[].class;
    }

    @Override
    public boolean equals(float[] floats, float[] j1) {
        return false;
    }

    @Override
    public int hashCode(float[] floats) {
        return 0;
    }

    @Override
    public float[] nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner)
            throws SQLException {
        PGobject pgObject = (PGobject) rs.getObject(position);
        if (pgObject == null) return null;
        String value = pgObject.getValue();
        return parseVector(value); // 解析 vector 字符串
    }

    @Override
    public void nullSafeSet(PreparedStatement st, float[] value, int index, SharedSessionContractImplementor session)
            throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
            return;
        }
        PGobject pgObject = new PGobject();
        pgObject.setType("vector"); // 设置类型为 vector
        pgObject.setValue(Arrays.toString(value).replace(" ", "")); // 设置值
        st.setObject(index, pgObject);
    }

    private float[] parseVector(String value) {
        String[] parts = value.replaceAll("[\\[\\]]", "").split(",");
        float[] vector = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            vector[i] = Float.parseFloat(parts[i].trim());
        }
        return vector;
    }

    // 其他方法实现（如 deepCopy, assemble, disassemble 等）
    @Override
    public float[] deepCopy(float[] value) {
        return value == null ? null : Arrays.copyOf(value, value.length);
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(float[] value) {
        return value;
    }

    @Override
    public float[] assemble(Serializable cached, Object owner) {
        return (float[]) cached;
    }
}