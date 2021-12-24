package com.liziedu.fake.core;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.ParameterizedType;

public class FakeResponse {

    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "FakeResponse{" +
                "body='" + body + '\'' +
                '}';
    }

    public static class Coder {

        public static <T> T deCoder(String body, Class<?> clazz) {
            return (T) JSONObject.parseObject(body, clazz);
        }

        public static <T> T deCoder(String body, ParameterizedType type) {
            return (T) JSONObject.parseObject(body, type);
        }
    }
}
