package com.liziedu.fake.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 方法注解协议组件
 *
 * 定义哪些注释和值在方法上有效
 */
public interface Contract {

    /**
     * 解析和验证http请求方法
     * 生成方法的元数据对象
     *
     * @param targetType
     * @return
     */
    List<MethodMetadata> parseAndValidaMetadata(Class<?> targetType);

    /**
     * 协议解析抽象方法
     */
    abstract class BaseContract implements Contract {

        /**
         * 解析方法注解并生成方法签名对应方法元数据map
         *
         * @param targetType 标注FakeClient接口类
         * @return [{方法签名, 方法元数据}]
         */
        @Override
        public List<MethodMetadata> parseAndValidaMetadata(Class<?> targetType) {
            Map<String, MethodMetadata> result = new LinkedHashMap<>();

            // 遍历接口类的所有方法
            for (Method method : targetType.getMethods()) {
                // 生成元数据
                MethodMetadata metadata = parseAndValidateMetadata(targetType, method);
                if (result.containsKey(metadata.configKey())) {
                    throw new IllegalStateException("不支持" + metadata.configKey() + "方法重写...");
                }

                result.put(metadata.configKey(), metadata);
            }

            return new ArrayList<>(result.values());
        }

        /**
         * 解析和生成方法元数据
         *
         * @param targetType 标注FakeClient接口类
         * @param method 方法反射对象
         * @return 自定义方法元数据对象
         */
        protected MethodMetadata parseAndValidateMetadata(Class<?> targetType, Method method) {
            MethodMetadata data = new MethodMetadata();
            /*
             * 设置方法返回类型
             * 单类: class x.x.x.E
             * 泛型类: x.x.x.E<x.x.x.y.EY>
             */
            data.returnType(Types.resolve(targetType, targetType, method.getGenericReturnType()));
            // 设置方法签名
            data.configKey(Fake.configKey(targetType, method));

            // 处理方法注解, 子类实现
            for (Annotation methodAnnotation : method.getAnnotations()) {
                processAnnotationOnMethod(data, methodAnnotation, method);
            }

            FakeUtil.checkState((data.template().method() != null),
                    "未发现方法中标注FakeRequestMapping...", targetType.getName());

            // 处理方法参数
            Class<?>[] parameterTypes = method.getParameterTypes();
            // 方法形参的类型数组
            Type[] genericParameterTypes = method.getGenericParameterTypes();

            // 方法形参的注解
            Annotation[][] parameterAnnotations  = method.getParameterAnnotations();
            int count = parameterAnnotations.length;
            for (int i = 0; i < count; i++) {
                // http 注解
                boolean isHttpAnnotation = false;
                if (parameterAnnotations[i] != null) {
                    isHttpAnnotation = processAnnotationOnParameter(
                            data, parameterAnnotations[i], i, targetType, genericParameterTypes[i]);
                }

                // 常规参数
                if (!isHttpAnnotation) {
                    data.bodyIndex(i);
                    data.bodyType(Types.resolve(targetType, targetType, genericParameterTypes[i]));
                }

                // TODO 常规对象和post Body对象处理
            }

            return data;
        }

        /**
         * 处理方法上的注解
         * @param data 元数据对象
         * @param methodAnnotation 方法注解
         * @param method 方法
         */
        protected abstract void processAnnotationOnMethod(MethodMetadata data,
                                                          Annotation methodAnnotation,
                                                          Method method);

        /**
         * 处理方法参数中的注解
         * @param data 元数据对象
         * @param annotations 方法参数注解列表
         * @param paramIndex 方法参数索引
         * @param targetType 接口类
         * @param type 参数类型
         * @return http 方法true
         */
        protected abstract boolean processAnnotationOnParameter(MethodMetadata data,
                                                                Annotation[] annotations,
                                                                int paramIndex,
                                                                Class<?> targetType,
                                                                Type type);

    }

    /**
     * 默认方法解析类
     */
    class Default extends BaseContract {

        @Override
        protected void processAnnotationOnMethod(MethodMetadata data,
                                                 Annotation methodAnnotation,
                                                 Method method) {

            Class<? extends Annotation> annotationType = methodAnnotation.annotationType();
            // 只有RpcRequestMapping 才处理
            if (!(methodAnnotation instanceof FakeRequestMapping) && !annotationType
                    .isAnnotationPresent(FakeRequestMapping.class)) {
                return;
            }

            if (methodAnnotation instanceof FakeRequestMapping) {
                // 解析http请求方法
                FakeRequest.HttpMethod requestMethod =
                        ((FakeRequestMapping) methodAnnotation).method();
                data.template().method(requestMethod);

                // 设置请求路径
                data.template().originalUri(
                        ((FakeRequestMapping) methodAnnotation).value());
            }
        }

        @Override
        protected boolean processAnnotationOnParameter( MethodMetadata data,
                                                        Annotation[] annotations,
                                                        int paramIndex,
                                                        Class<?> targetType,
                                                        Type type) {
            boolean isHttpAnnotation = false;
            for (Annotation annotation : annotations) {
                Class<? extends Annotation> annotationType = annotation.annotationType();
                // 普通参数处理
                if (annotationType == FakeRequestParam.class) {
                    FakeRequestParam paramAnnotation = (FakeRequestParam) annotation;
                    String name = paramAnnotation.value();

                    nameParam(data, name, paramIndex);

                    isHttpAnnotation = true;
                }
                // 可变参数处理
                else if (annotationType == FakePathVariable.class) {
                    FakePathVariable pathAnnotation = (FakePathVariable) annotation;
                    String name = pathAnnotation.value();

                    data.variableNameToIndex().put(name, paramIndex);
                    data.pathVariable();

                    isHttpAnnotation = true;
                }
                // 头信息对象
                else if (annotationType == FakeHeaders.class) {
                    data.headersIndex(paramIndex);
                    data.headersType(Types.resolve(targetType, targetType, type));
                }
            }

            return isHttpAnnotation;
        }

        protected void nameParam(MethodMetadata data, String name, int i) {
            Collection<String> names =
                    data.indexToName().containsKey(i) ? data.indexToName().get(i) : new ArrayList<>();
            names.add(name);
            data.indexToName().put(i, names);
        }
    }
}
