package com.liziedu.fake.core;

import com.moczul.ok2curl.CurlInterceptor;
import com.moczul.ok2curl.logger.Loggable;
import okhttp3.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * http客户端
 */
public interface Client {

    /**
     * 执行请求
     * @param fakeRequest
     * @return
     * @throws IOException
     */
    FakeResponse execute(FakeRequest fakeRequest) throws IOException;

    class Default implements Client {

        private final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new CurlInterceptor(new Loggable() {
                    @Override
                    public void log(String message) {
                        System.out.println("Ok2Curl: " + message);
                    }
                })).build();

        public static final MediaType JSON
                = MediaType.get("application/json; charset=utf-8");

        /**
         * @param fakeRequest
         * @return
         * @throws IOException
         */
        @Override
        public FakeResponse execute(FakeRequest fakeRequest) throws IOException {

            Request.Builder builder = new Request.Builder().url(fakeRequest.url());

            if (fakeRequest.method() == FakeRequest.HttpMethod.POST
                    && fakeRequest.body() != null) {
                RequestBody body = RequestBody.create(JSON, fakeRequest.body());
                builder.post(body);
            }

            if (fakeRequest.headers() != null) {
                builder.headers(getHeaders(fakeRequest.headers()));
            }

            Request request = builder.build();

            FakeResponse fakeResponse = new FakeResponse();
            Response response = client.newCall(request).execute();
            fakeResponse.setBody(response.body().string());

            return fakeResponse;
        }

        public static Headers getHeaders(Map<String, String> headersMap) {
            okhttp3.Headers.Builder headersBuilder = new okhttp3.Headers.Builder();
            for (Map.Entry<String, String> entry : headersMap.entrySet()) {
                headersBuilder.add(entry.getKey(), entry.getValue());
            }

            return headersBuilder.build();
        }
    }
}
