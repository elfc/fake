package com.liziedu.fake.core;

import java.util.Map;

public class FakeRequest {

    public enum HttpMethod {

        GET, POST

    }

    private final HttpMethod method;

    private final String url;

    private final Map<String, String> headers;

    private final String body;

    public FakeRequest(HttpMethod method,
                       String url,
                       Map<String, String> headers,
                       String body) {
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.body = body;
    }

    public static FakeRequest create(HttpMethod method,
                                     String url,
                                     Map<String, String> headers,
                                     String body) {
        return new FakeRequest(method, url, headers, body);
    }

    public String url() {
        return url;
    }

    public String body() {
        return body;
    }

    public HttpMethod method() {
        return method;
    }

    public Map<String, String> headers() {
        return headers;
    }

    @Override
    public String toString() {
        return "Request{" +
                "url='" + url + '\'' +
                ", headers=" + headers +
                '}';
    }
}
