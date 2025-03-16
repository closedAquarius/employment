package com.guangge.Interview.data;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class InterViewStatusConverter implements AttributeConverter<InterViewStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(InterViewStatus status) {
        if (status == null) {
            return null;
        }
        return status.getCode(); // 假设 UserStatus 有一个 getCode() 方法
    }

    @Override
    public InterViewStatus convertToEntityAttribute(Integer code) {
        if (code == null) {
            return null;
        }
        for (InterViewStatus status : InterViewStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException();
    }
}
