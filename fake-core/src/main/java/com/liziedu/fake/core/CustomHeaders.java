package com.liziedu.fake.core;

import java.util.Map;

public interface CustomHeaders {

    /**
     * 获取headers 信息
     * 请求头: {key: value}
     * @return
     */
    Map<String, String> getHeaders();
}
