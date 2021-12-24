package com.liziedu.fake.core;

import java.util.List;

/**
 * 请求模版
 */
public class QueryParameter {

    private final Object value;
    private final String name;

    private QueryParameter(Object value, String name) {
        // 特殊参数
        if (value instanceof List) {
            List<Object> listValue = (List<Object>) value;
            this.value = listToStringObject(listValue);
        } else {
            this.value = value;
        }

        this.name = name;
    }

    private Object listToStringObject(List<Object> list) {
        StringBuilder builder = new StringBuilder();
        for (Object object : list) {
            builder.append(object).append(",");
        }

        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public static QueryParameter create(Object value, String name) {
        return new QueryParameter(value, name);
    }

    public Object getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String pair() {
        return name + "=" + value;
    }

    @Override
    public String toString() {
        return "QueryParameter{" +
                "value=" + value +
                ", name='" + name + '\'' +
                '}';
    }
}
