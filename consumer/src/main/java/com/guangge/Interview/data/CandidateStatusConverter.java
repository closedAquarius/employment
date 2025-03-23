package com.guangge.Interview.data;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CandidateStatusConverter implements AttributeConverter<CandidateStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(CandidateStatus candidateStatus) {
        if (candidateStatus == null) {
            return null;
        }
        return candidateStatus.getCode();
    }

    @Override
    public CandidateStatus convertToEntityAttribute(Integer code) {
        if (code == null) {
            return null;
        }
        for (CandidateStatus status : CandidateStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException();
    }
}
