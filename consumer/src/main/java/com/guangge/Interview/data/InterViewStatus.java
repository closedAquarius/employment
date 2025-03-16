package com.guangge.Interview.data;

public enum InterViewStatus {
    WAIT(1,"等待"),PERFECT(2,"完美"),EXCELLENT(3,"优秀"),QUALIFIED(4,"合格"),ELIMINATE(5,"淘汰");
    private String value;
    private Integer code;

    private InterViewStatus(Integer code, String value) {
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
