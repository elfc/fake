package com.liziedu.fake.core;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * 动态代理反射类
 */
public class FakeReflective extends Fake {

    private final ParseHandlersByName targetToHandlerByName;
    private final InvocationHandlerFactory factory;

    public FakeReflective(ParseHandlersByName targetToHandlerByName,
                          InvocationHandlerFactory factory) {
        this.targetToHandlerByName = targetToHandlerByName;
        this.factory = factory;
    }

    /**
     * 生成动态代理对象
     * @param target
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T newInstance(FakeTarget<T> target) {
        /* 生成方法签名对应代理方法对象
         * {"XXService#xxMethod(String,String)", SynchronousMethodHandler}
         */
        Map<String, InvocationHandlerFactory.MethodHandler> nameToHandler
                = targetToHandlerByName.apply(target);

        // 方法对应方法代理对象map
        Map<Method, InvocationHandlerFactory.MethodHandler> methodHandler
                = new LinkedHashMap<>();

        for (Method method : target.type().getMethods()) {
            methodHandler.put(method,
                    nameToHandler.get(Fake.configKey(target.type(), method)));
        }

        // 创建一个代理对象 RpcInvocationHandler
        InvocationHandler handler = factory.create(methodHandler);

        // 创建动态代理
        T proxy = (T) Proxy.newProxyInstance(target.type().getClassLoader(),
                new Class<?>[] {target.type()}, handler);

        return proxy;
    }

    /**
     * 标注FakeClient 类的动态代理对象
     */
    static class RpcInvocationHandler implements InvocationHandler {

        private final Map<Method, InvocationHandlerFactory.MethodHandler> dispatch;

        public RpcInvocationHandler(
                Map<Method, InvocationHandlerFactory.MethodHandler> dispatch) {
            this.dispatch = dispatch;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            /*
             * 对应的代理方法转发到对应的 SynchronousMethodHandler
             */
            return dispatch.get(method).invoke(args);
        }
    }

    /**
     * 解析需要动态代理的类
     * 生成方法元数据
     * 生成方法对应的代理对象类
     */
    static final class ParseHandlersByName {

        private final Contract contract;
        private final SynchronousMethodHandler.Factory factory;

        public ParseHandlersByName(
                Contract contract,
                SynchronousMethodHandler.Factory factory) {
            this.contract = contract;
            this.factory = factory;
        }

        /**
         * 生成动态代理方法
         * @param key
         * @return
         */
        public Map<String, InvocationHandlerFactory.MethodHandler> apply(FakeTarget key) {
            // 将接口方法及注解解析生成方法对应元数据
            List<MethodMetadata> metadata = contract.parseAndValidaMetadata(key.type());

            Map<String, InvocationHandlerFactory.MethodHandler> result = new LinkedHashMap<>();

            // 遍历接口方法, 生成接口动态代理对象
            for (MethodMetadata md : metadata) {
                // 请求模版解析器
                BuildTemplateByResolvingArgs template = new BuildTemplateByResolvingArgs(md);

                // 实际生成动态代理接口对象
                result.put(md.configKey(), factory.create(key, md, template));

                // 请求域名赋给请求模版
                md.template().domain(key.domain());
            }

            return result;
        }
    }

    /**
     * 请求模版解析器
     */
    private static class BuildTemplateByResolvingArgs implements RequestTemplate.Factory {

        protected final MethodMetadata metadata;

        private BuildTemplateByResolvingArgs(MethodMetadata metadata) {
            this.metadata = metadata;
        }

        /**
         * 创建请求模版
         * @param argv 调用方法时传入的实参
         * @return 请求模版
         */
        @Override
        public RequestTemplate create(Object[] argv) {

            // 如果请求中有可变参数, 先替换可变参数值
            if (metadata.hasPathVariable()) {
                metadata.template().uri(getVariables(argv));
            }
            // 正常请求参数使用原始uri
            else {
                metadata.template().uri(metadata.template().originalUri());
            }

            /*
             * 处理请求参数
             * metadata.indexToName()
             * {参数索引, "参数名称"}
             *
             * 匹配实参对应参数索引, 拿到当前参数索引所在位置的实际值
             *
             * 第一轮场景:
             * 方法: method.get(long id, String name);
             * metadata.indexToName(): {
             *      0, List<"id">,
             *      1, List<"name">
             * }
             * Object[] argv = [100, "蛟龙"]
             * Object value = argv[0] = 100
             * queryParameter(100, "id")
             *
             * 最终得到请求参数id=100, name="蛟龙"
             */
            QueryParameter queryParameter;
            for (Map.Entry<Integer, Collection<String>> entry :
                    metadata.indexToName().entrySet()) {
                // argv[0] = 100
                Object value = argv[entry.getKey()];

                for (String name : entry.getValue()) {
                    // queryParameter(100, "id")
                    queryParameter = QueryParameter.create(value, name);
                    metadata.template().addQuery(queryParameter);
                }
            }


            if (metadata.headersIndex() != null) {
                /*
                 * 处理参数中请求头对象
                 * argv[metadata.headersIndex()]
                 * 实际参数列表[请求头对象参数索引] = 请求头参数对象
                 * 将对象转换为json对象
                 * 添加到请求模版中
                 */
                JSONObject jsonObject = (JSONObject) JSONObject.toJSON(argv[metadata.headersIndex()]);

                for (Map.Entry entry : jsonObject.entrySet()) {
                    queryParameter = QueryParameter.create(entry.getValue(), (String) entry.getKey());
                    metadata.template().addHeader(queryParameter);
                }
            }

            return metadata.template();
        }

        /**
         * 获取可变参数名称对应可变参数值
         * @param argv
         * @return
         */
        private Map<String, String> getVariables(Object[] argv) {
            HashMap<String, String> variables = new HashMap<>();

            /*
             * metadata.variableNameToIndex()
             * 可变参数名称对应方法中的索引
             * /xx/{id}/yy
             * get(@PathVariables("id") long id)
             *
             * metadata.variableNameToIndex():
             * {"id": 0}
             *
             * argv = [500]
             * argv[entry.getValue()] = 实际参数列表[索引0] = 500
             *
             * variables.put(entry.getKey(), String.valueOf(argv[entry.getValue()]))
             * variables.put("id", "500")
             *
             * variables: {可变参数名称: 可变参数值}
             *
             */
            for (Map.Entry<String, Integer> entry :
                    metadata.variableNameToIndex().entrySet()) {
                if (entry.getValue() > argv.length) {
                    throw new IndexOutOfBoundsException("元数据中可变数据索引["
                            + entry.getValue() + "]大于传入参数索引" + argv.length);
                }

                variables.put(entry.getKey(), String.valueOf(argv[entry.getValue()]));
            }

            return variables;
        }
    }
}
