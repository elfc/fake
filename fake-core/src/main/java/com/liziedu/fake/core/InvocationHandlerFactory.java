package com.liziedu.fake.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public interface InvocationHandlerFactory {

    InvocationHandler create(Map<Method, MethodHandler> dispatch);

    interface MethodHandler {
        Object invoke(Object[] argv) throws Throwable;
    }

    final class Default implements InvocationHandlerFactory {

        @Override
        public InvocationHandler create(Map<Method, MethodHandler> dispatch) {
            return new FakeReflective.RpcInvocationHandler(dispatch);
        }
    }
}
