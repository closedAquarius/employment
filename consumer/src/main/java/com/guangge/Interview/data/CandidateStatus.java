package com.guangge.Interview.data;

public enum CandidateStatus {
    WAIT(1,"等待"),NOTIFY(2,"通知"),FINISH(3,"完成");
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
}
