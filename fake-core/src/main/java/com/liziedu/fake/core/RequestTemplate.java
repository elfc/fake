package com.liziedu.fake.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 请求模版
 */
public final class RequestTemplate {

    private FakeRequest.HttpMethod method;

    /**
     * 请求url参数
     */
    private Map<String, QueryParameter> queries = new LinkedHashMap<>();

    /**
     * 请求头参数
     */
    private Map<String, String> headers = new LinkedHashMap<>();

    /**
     * 请求域名
     * https://x.x.x
     */
    private String domain;

    /**
     * 原始的uri
     */
    private String originalUri;

    /**
     * 可请求的uri
     */
    private String uri;

    /**
     * uri 按照 "/" 切分段落
     */
    private String[] uriSegment;

    /**
     * 可变参数名字对应可变参数索引
     * {"id", uriSegment[1]}
     * 最终需要替换
     */
    private Map<String, Integer> variableNameToIndex;

    /**
     * post 请求body内容
     * json body
     */
    private String body;

    private transient ParseUri parseUri;

    public RequestTemplate() {
        this.parseUri = new ParseUri();
    }

    /**
     * 设置http请求方法
     * @param method
     * @return
     */
    public RequestTemplate method(FakeRequest.HttpMethod method) {
        this.method = method;
        return this;
    }

    public FakeRequest.HttpMethod method() {
        return method;
    }

    /**
     * 设置uri
     * @param uri
     * @return
     */
    public RequestTemplate originalUri(String uri) {
        this.originalUri = uri;
        this.uriSegment = parseUri.separate(uri);

        return this;
    }

    public RequestTemplate domain(String domain) {
        this.domain = domain;
        return this;
    }

    public void uri(Map<String, String> variableNameToValue) {
        this.uri = parseUri.combine(uriSegment, variableNameToValue);
    }

    public void uri(String uri) {
        this.uri = uri;
    }

    public String originalUri() {
        return originalUri;
    }

    public void addQuery(QueryParameter queryParameter) {
        queries.put(queryParameter.getName(), queryParameter);
    }

    public void addHeader(QueryParameter queryParameter) {
        headers.put(queryParameter.getName(), String.valueOf(queryParameter.getValue()));
    }

    public Map<String, String> headers() {
        return headers;
    }

    public String url() {
        StringBuilder builder = new StringBuilder();
        builder.append(domain).append(uri);

        if (method == FakeRequest.HttpMethod.GET) {
            if (queries == null || queries.isEmpty()) {
                return builder.toString();
            }

            builder.append("?");

            for (QueryParameter queryParameter : queries.values()) {
                builder.append(queryParameter.pair()).append("&");
            }

            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    public void body(String body) {
        this.body = body;
    }

    /**
     * 拿一个新的请求
     * @return
     */
    public FakeRequest request() {
        return FakeRequest.create(this.method, this.url(), this.headers(), this.body);
    }

    /**
     * 解析uri
     * 以 / 为分隔符
     */
    private class ParseUri {

        private static final String SEPARATOR = "/";

        public String[] separate(String uri) {
            if (uri == null) {
                throw new IllegalArgumentException("uri 不能为空...");
            }

            String[] uriSegment;
            if (uri.startsWith("/")) {
                uriSegment = uri.replaceFirst("/", "")
                        .split(SEPARATOR);
            } else {
                uriSegment = uri.split(SEPARATOR);
            }

            parseVariableUri(uriSegment);

            return uriSegment;
        }

        /**
         * 合并uri 替换
         * @param uriSegment
         * @param variableNameToValue
         *  可变URI 名字对应的值
         *  {"id": "2"}
         * @return
         */
        public String combine(String[] uriSegment,
                              Map<String, String> variableNameToValue) {
            if (variableNameToValue == null) {
                return builderUri(uriSegment);
            }

            for (Map.Entry<String, String> entry : variableNameToValue.entrySet()) {
                int index = variableNameToIndex.get(entry.getKey());
                if (index >= uriSegment.length) {
                    throw new IndexOutOfBoundsException(
                            "variableIndexToValue 索引越界 index:"
                                    + entry.getKey()
                                    + ", uriSegment: " + uriSegment.length);
                }

                uriSegment[index] = entry.getValue();
            }

            return builderUri(uriSegment);
        }

        private String builderUri(String[] uriSegment) {
            StringBuilder builder = new StringBuilder();
            for (String s : uriSegment) {
                builder.append(SEPARATOR).append(s);
            }

            return builder.toString();
        }

        /**
         * 解析可变参数
         * @param uriSegment
         */
        private void parseVariableUri(String[] uriSegment) {
            for (int i = 0; i < uriSegment.length; i++) {
                String uriPart = uriSegment[i];
                // 可变参数
                if (uriPart.startsWith("{") && uriPart.endsWith("}")) {
                    if (variableNameToIndex == null) {
                        variableNameToIndex = new HashMap<>();
                    }
                    variableNameToIndex.put(uriPart.substring(1, uriPart.length() - 1), i);
                }
            }
        }
    }

    interface Factory {

        RequestTemplate create(Object[] argv);
    }

    @Override
    public String toString() {
        return "RequestTemplate{" +
                "method=" + method +
                ", queries=" + queries +
                ", headers=" + headers +
                ", originalUri='" + originalUri + '\'' +
                ", uri='" + uri + '\'' +
                ", uriSegment=" + Arrays.toString(uriSegment) +
                ", variableNameToIndex=" + variableNameToIndex +
                '}';
    }
}
