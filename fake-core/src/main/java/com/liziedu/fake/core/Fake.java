package com.liziedu.fake.core;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * rpc
 */
public abstract class Fake {

    public static String configKey(Class targetType, Method method) {
        StringBuilder builder = new StringBuilder();
        builder.append(targetType.getSimpleName());
        builder.append('#').append(method.getName()).append('(');

        for (Type param : method.getGenericParameterTypes()) {
            param = Types.resolve(targetType, targetType, param);
            builder.append(Types.getRawType(param).getSimpleName()).append(',');
        }

        if (method.getParameterTypes().length > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.append(')').toString();
    }

    /**
     * 构造RpcTarget 对象
     * @param target
     * @param <T>
     * @return
     */
    public abstract <T> T newInstance(FakeTarget<T> target);

    public static class Builder {

        private InvocationHandlerFactory InvocationHandlerFactory = new InvocationHandlerFactory.Default();
        private Client client = new Client.Default();

        private Contract contract = new Contract.Default();

        public Fake build() {

            SynchronousMethodHandler.Factory syncMethodHandlerFactory = new SynchronousMethodHandler.Factory(client);
            FakeReflective.ParseHandlersByName handlersByName =
                    new FakeReflective.ParseHandlersByName(contract, syncMethodHandlerFactory);
            return new FakeReflective(handlersByName, InvocationHandlerFactory);
        }
    }
}
