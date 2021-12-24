package com.liziedu.fake.core;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

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

        private final OkHttpClient client = new OkHttpClient();

        /**
         * @param fakeRequest
         * @return
         * @throws IOException
         */
        @Override
        public FakeResponse execute(FakeRequest fakeRequest) throws IOException {

            Request request = new Request.Builder()
                    .url(fakeRequest.url())
                    .build();

            FakeResponse fakeResponse = new FakeResponse();
            Response response = client.newCall(request).execute();
            fakeResponse.setBody(response.body().string());

            return fakeResponse;
        }
    }
}
