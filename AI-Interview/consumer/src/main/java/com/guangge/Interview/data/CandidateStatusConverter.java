package com.guangge.Interview.data;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 面试状态转换器，用于AI-Interview系统与就业系统之间的状态转换
 */
@Component
public class CandidateStatusConverter implements AttributeConverter<CandidateStatus, Integer> {

    // AI-Interview系统面试状态与就业系统状态映射
    private static final Map<Integer, Integer> INTERVIEW_TO_EMPLOYMENT_STATUS = new HashMap<>();
    private static final Map<Integer, Integer> EMPLOYMENT_TO_INTERVIEW_STATUS = new HashMap<>();

    static {
        // AI-Interview状态 -> 就业系统状态
        INTERVIEW_TO_EMPLOYMENT_STATUS.put(0, 1);     // 待面试 -> 简历已投递
        INTERVIEW_TO_EMPLOYMENT_STATUS.put(1, 2);     // 面试中 -> 面试进行中
        INTERVIEW_TO_EMPLOYMENT_STATUS.put(2, 3);     // 通过 -> 面试通过
        INTERVIEW_TO_EMPLOYMENT_STATUS.put(3, 4);     // 未通过 -> 面试未通过
        INTERVIEW_TO_EMPLOYMENT_STATUS.put(4, 5);     // 取消 -> 已取消

        // 就业系统状态 -> AI-Interview状态
        EMPLOYMENT_TO_INTERVIEW_STATUS.put(1, 0);     // 简历已投递 -> 待面试
        EMPLOYMENT_TO_INTERVIEW_STATUS.put(2, 1);     // 面试进行中 -> 面试中
        EMPLOYMENT_TO_INTERVIEW_STATUS.put(3, 2);     // 面试通过 -> 通过
        EMPLOYMENT_TO_INTERVIEW_STATUS.put(4, 3);     // 面试未通过 -> 未通过
        EMPLOYMENT_TO_INTERVIEW_STATUS.put(5, 4);     // 已取消 -> 取消
    }

    /**
     * 将AI-Interview系统的面试状态转换为就业系统的状态
     * @param interviewStatus AI-Interview系统面试状态
     * @return 就业系统状态
     */
    public Integer toEmploymentStatus(Integer interviewStatus) {
        return INTERVIEW_TO_EMPLOYMENT_STATUS.getOrDefault(interviewStatus, 1); // 默认为简历已投递
    }

    /**
     * 将就业系统的状态转换为AI-Interview系统的面试状态
     * @param employmentStatus 就业系统状态
     * @return AI-Interview系统面试状态
     */
    public Integer toInterviewStatus(Integer employmentStatus) {
        return EMPLOYMENT_TO_INTERVIEW_STATUS.getOrDefault(employmentStatus, 0); // 默认为待面试
    }

    @Override
    public Integer convertToDatabaseColumn(CandidateStatus candidateStatus) {
        return candidateStatus.getCode();
    }

    @Override
    public CandidateStatus convertToEntityAttribute(Integer code) {
        return CandidateStatus.fromCode(code);
    }
}
