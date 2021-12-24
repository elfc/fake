package com.liziedu.fake.core;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

/**
 * 动态代理方法类
 */
public class SynchronousMethodHandler implements InvocationHandlerFactory.MethodHandler {

    private final MethodMetadata metadata;
    private final FakeTarget<?> target;
    private final RequestTemplate.Factory requestTemplateFactory;
    private final Client client;

    public SynchronousMethodHandler(
            FakeTarget<?> target,
            MethodMetadata metadata,
            RequestTemplate.Factory requestTemplateFactory,
            Client client) {

        this.target = target;
        this.metadata = metadata;
        this.requestTemplateFactory = requestTemplateFactory;
        this.client = client;
    }

    @Override
    public Object invoke(Object[] argv) throws Throwable {
        // 生成请求模版
        RequestTemplate requestTemplate = requestTemplateFactory.create(argv);

        // 执行请求
        return execute(requestTemplate);
    }

    /**
     * 执行请求
     * @param requestTemplate 请求模版
     */
    private Object execute(RequestTemplate requestTemplate) {
        FakeRequest fakeRequest = requestTemplate.request();

        try {
            // TODO 返回合适的response 对象
            FakeResponse fakeResponse = client.execute(fakeRequest);

            if (metadata.returnType() instanceof Class<?>) {
                Class<?> returnType = (Class<?>) metadata.returnType();

                return FakeResponse.Coder.deCoder(fakeResponse.getBody(), returnType);
            } else if (metadata.returnType() instanceof ParameterizedType) {
                ParameterizedType returnType = (ParameterizedType) metadata.returnType();

                return FakeResponse.Coder.deCoder(fakeResponse.getBody(), returnType);
            } else {
                throw new IllegalArgumentException("传入对象非法....");
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("执行请求失败...");
        }

        return null;
    }

    static class Factory {

        private final Client client;

        public Factory(Client client) {
            this.client = client;
        }

        public InvocationHandlerFactory.MethodHandler create(
                FakeTarget<?> target,
                MethodMetadata md,
                RequestTemplate.Factory buildTemplateFromArgs) {
            return new SynchronousMethodHandler(target, md, buildTemplateFromArgs, client);
        }
    }
}
