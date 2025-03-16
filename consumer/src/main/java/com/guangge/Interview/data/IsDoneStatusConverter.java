package com.guangge.Interview.data;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class IsDoneStatusConverter implements AttributeConverter<IsDoneStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(IsDoneStatus status) {
        if (status == null) {
            return null;
        }
        return status.getCode(); // 假设 UserStatus 有一个 getCode() 方法
    }

    @Override
    public IsDoneStatus convertToEntityAttribute(Integer code) {
        if (code == null) {
            return null;
        }
        for (IsDoneStatus status : IsDoneStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException();
    }
}
