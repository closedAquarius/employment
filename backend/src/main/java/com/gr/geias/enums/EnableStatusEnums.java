package com.gr.geias.enums;

/**
 * 用户权限状态枚举
 */
public enum EnableStatusEnums {
    /**
     * 普通老师
     */
    TEACHER(0, "普通老师"),
    /**
     * 院长
     */
    PREXY(1, "院长"),
    /**
     * 学校管理员
     */
    schoolmaster(2, "学校管理员");

    private Integer state;
    private String stateInfo;

    EnableStatusEnums(Integer state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public Integer getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    /**
     * 通过状态值获取枚举对象
     * 
     * @param state 状态值
     * @return 对应的枚举对象，如果没有找到则返回null
     */
    public static EnableStatusEnums stateOf(int state) {
        for (EnableStatusEnums stateEnum : values()) {
            if (stateEnum.getState() == state) {
                return stateEnum;
            }
        }
        return null;
    }

    /**
     * 兼容旧版本的方法
     * 
     * @param state 状态值
     * @return 对应的枚举对象
     */
    public static EnableStatusEnums getEnableStatusEnums(int state) {
        return stateOf(state);
    }
} 