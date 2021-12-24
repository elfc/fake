package com.liziedu.fake.core;

import java.util.Map;

public class FakeRequest {

    public enum HttpMethod {

        GET, POST

    }

    private final HttpMethod method;

    private final String url;

    private final Map<String, String> headers;

    public FakeRequest(HttpMethod method, String url, Map<String, String> headers) {
        this.method = method;
        this.url = url;
        this.headers = headers;
    }

    public static FakeRequest create(HttpMethod method, String url, Map<String, String> headers) {
        return new FakeRequest(method, url, headers);
    }

    public String url() {
        return url;
    }

    @Override
    public String toString() {
        return "Request{" +
                "url='" + url + '\'' +
                ", headers=" + headers +
                '}';
    }
}
