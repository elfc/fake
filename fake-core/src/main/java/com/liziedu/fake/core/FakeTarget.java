package com.liziedu.fake.core;

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

    class DefaultTarget<T> implements FakeTarget<T> {

        private final Class<T> type;
        private final String domain;

        public DefaultTarget(Class<T> type, String domain) {
            this.type = type;
            this.domain = domain;
        }

        @Override
        public Class<T> type() {
            return type;
        }

        @Override
        public String domain() {
            return domain;
        }

    }
}
