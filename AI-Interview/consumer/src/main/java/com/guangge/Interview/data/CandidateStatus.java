package com.guangge.Interview.data;

public enum CandidateStatus {
    PENDING(0, "待面试"),
    INTERVIEWING(1, "面试中"),
    PASSED(2, "通过"),
    FAILED(3, "未通过"),
    CANCELLED(4, "取消"),
    WAIT(5, "等待"),
    NOTIFY(6, "通知"),
    FINISH(7, "完成");

    private String value;
    private Integer code;

    private CandidateStatus(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
    
    /**
     * 根据状态码获取对应的枚举值
     * @param code 状态码
     * @return 对应的枚举值，如果没有匹配则返回PENDING
     */
    public static CandidateStatus fromCode(Integer code) {
        if (code == null) {
            return PENDING;
        }
        
        for (CandidateStatus status : CandidateStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        
        return PENDING; // 默认返回待面试状态
    }
}
