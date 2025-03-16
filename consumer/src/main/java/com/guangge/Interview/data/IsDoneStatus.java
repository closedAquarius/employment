package com.guangge.Interview.data;

public enum IsDoneStatus {
    PENDING(0,"待定"),ONE_DONE(1,"笔试结束"),TWO_DONE(2,"面试结束");
    private String value;
    private Integer code;

    private IsDoneStatus(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
