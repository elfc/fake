package com.liziedu.fake.example.service;

import com.liziedu.fake.core.CustomHeaders;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AccessToken implements CustomHeaders {

    @Override
    public Map<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("token", "123");

        return headers;
    }
}
