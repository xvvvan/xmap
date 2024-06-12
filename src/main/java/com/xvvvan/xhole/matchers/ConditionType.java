package com.xvvvan.xhole.matchers;

import java.util.HashMap;
import java.util.Map;

public enum ConditionType {
    ANDCondition(1), // 匹配AND条件的响应
    ORCondition(2); // 匹配OR条件的响应

    // 条件类型值
    private final int value;

    // 构造函数
    private ConditionType(int value) {
        this.value = value;
    }
    /**
     * 根据条件类型字符串获取对应的条件类型
     * @param typeString 条件类型字符串
     * @return 条件类型
     */
    public static final Map<ConditionType,String> ConditionTypes= new HashMap() {
        {
            put(ConditionType.ANDCondition,"and");
            put(ConditionType.ORCondition,"or");
        }
    };;

    public String toString () {
        return ConditionTypes.get(this);
    }
    // 根据条件类型值获取对应的条件类型
    public static ConditionType fromValue(String value) {
        switch (value) {
            case "and":
                return ANDCondition;
            case "or":
                return ORCondition;
            default:
                return null;
        }
    }

    // 获取条件类型值
    public int getValue() {
        return this.value;
    }

}

