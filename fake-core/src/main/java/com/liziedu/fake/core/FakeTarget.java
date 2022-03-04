package com.liziedu.fake.core;

import java.util.Map;

public interface FakeTarget<T> {

    /**
     * 接口类型 interface com.xx.xx.XxService
     * @return
     */
    Class<T> type();

    /**
     * 请求的域名
     * @return
     */
    String domain();

    /**
     * 自定义头信息
     */
    Map<String, String> headers();

    class DefaultTarget<T> implements FakeTarget<T> {

        private final Class<T> type;
        private final String domain;
        private Map<String, String> headers;

        public DefaultTarget(Class<T> type,
                             String domain,
                             Map<String, String> headers) {
            this.type = type;
            this.domain = domain;
            this.headers = headers;
        }

        @Override
        public Class<T> type() {
            return type;
        }

        @Override
        public String domain() {
            return domain;
        }

        @Override
        public Map<String, String> headers() {
            return headers;
        }
    }
}
