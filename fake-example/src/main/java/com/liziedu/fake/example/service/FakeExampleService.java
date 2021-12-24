package com.liziedu.fake.example.service;

import com.liziedu.fake.core.*;
import com.liziedu.fake.example.domain.FakeTestDTO;
import com.liziedu.fake.example.domain.FakeTestPageResponse;
import com.liziedu.fake.example.domain.FakeTestResponse;

@FakeClient(domain = "http://127.0.0.1:9001")
public interface FakeExampleService {

    /**
     * 测试主页
     */
    @FakeRequestMapping("/test/homePage")
    FakeTestResponse<FakeTestPageResponse<FakeTestDTO>> homePage();
}
