package com.liziedu.fake.core;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 方法元数据
 */
public class MethodMetadata {

    /**
     * 反射的名字: 类名#(方法参数,方法参数)
     */
    private String configKey;

    /**
     * 返回类型
     */
    private transient Type returnType;

    /**
     * 请求模版
     */
    private final RequestTemplate template = new RequestTemplate();

    /**
     * 请求对象索引
     */
    private Integer bodyIndex;

    /**
     * 请求对象类型
     */
    private Type bodyType;

    /**
     * 请求头对象索引
     */
    private Integer headersIndex;

    /**
     * 请求头对象类型
     */
    private Type headersType;

    /**
     * 方法参数对应索引
     */
    private final Map<Integer, Collection<String>> indexToName
            = new LinkedHashMap<>();

    /**
     * 方法中有可变参数对应在indexToName 中索引
     * {"id": indexToName[1]}
     */
    private final Map<String, Integer> variableNameToIndex = new HashMap<>();

    /**
     * 是否有可变参数
     */
    private boolean hasVariable = false;

    /**
     * 反射的名字: 类名#(方法参数,方法参数)
     * @return
     */
    public String configKey() {
        return configKey;
    }

    public MethodMetadata configKey(String configKey) {
        this.configKey = configKey;
        return this;
    }

    public MethodMetadata returnType(Type returnType) {
        this.returnType = returnType;
        return this;
    }

    public Type returnType() {
        return returnType;
    }

    public RequestTemplate template() {
        return template;
    }

    public MethodMetadata bodyIndex(int bodyIndex) {
        this.bodyIndex = bodyIndex;
        return this;
    }

    public MethodMetadata bodyType(Type bodyType) {
        this.bodyType = bodyType;
        return this;
    }

    public Map<Integer, Collection<String>> indexToName() {
        return indexToName;
    }

    public Map<String, Integer> variableNameToIndex() {
        return variableNameToIndex;
    }

    public boolean hasPathVariable() {
        return hasVariable;
    }

    public void pathVariable() {
        this.hasVariable = true;
    }

    public MethodMetadata headersIndex(int headersIndex) {
        this.headersIndex = headersIndex;
        return this;
    }

    public Integer headersIndex() {
        return headersIndex;
    }

    public MethodMetadata headersType(Type headersType) {
        this.headersType = headersType;
        return this;
    }

    @Override
    public String toString() {
        return "MethodMetadata{" +
                "configKey='" + configKey + '\'' +
                ", returnType=" + returnType +
                ", template=" + template +
                ", bodyIndex=" + bodyIndex +
                ", bodyType=" + bodyType +
                ", indexToName=" + indexToName +
                ", variableNameToIndex=" + variableNameToIndex +
                '}';
    }
}
