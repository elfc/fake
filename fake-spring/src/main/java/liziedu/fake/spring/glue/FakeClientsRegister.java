package liziedu.fake.spring.glue;

import com.liziedu.fake.core.FakeClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
 import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.*;

/**
 * 注册fake客户端类
 */
class FakeClientsRegister
        implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private static final Log LOG = LogFactory.getLog(FakeClientsRegister.class);

    private ResourceLoader resourceLoader;
    private Environment environment;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {
        registerFakeClients(importingClassMetadata, registry);
    }

    public void registerFakeClients(AnnotationMetadata metadata,
                                    BeanDefinitionRegistry registry) {
        // 标注了FakeClients 的类集合
        LinkedHashSet<BeanDefinition> fakeClients = new LinkedHashSet<>();

        ClassPathScanningCandidateComponentProvider scanner = getScanner();

        scanner.setResourceLoader(this.resourceLoader);

        // 扫描标注了FakeClient 注解的类
        scanner.addIncludeFilter(new AnnotationTypeFilter(FakeClient.class));
        Set<String> scanPackages = getScanPackages(metadata);
        for (String pkg : scanPackages) {
            // 获取到包下所有候选组件
            fakeClients.addAll(scanner.findCandidateComponents(pkg));
        }

        for (BeanDefinition fakeClient : fakeClients) {
            if (fakeClient instanceof AnnotatedBeanDefinition) {
                AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) fakeClient;
                AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();

                if (!annotationMetadata.isInterface()) {
                    throw new IllegalStateException("@FakeClient 必须注释在接口上...");
                }

                Map<String, Object> attributes = annotationMetadata
                        .getAnnotationAttributes(FakeClient.class.getCanonicalName());

                registryFakeClient(registry, annotationMetadata, attributes);
            }
        }
    }

    /**
     * 获取fake beanName
     * @param attributes
     * @return
     */
    protected String getFakeClientName(Map<String, Object> attributes, Class<?> clazz) {
        String name = clazz.getSimpleName();
        name = name.substring(0, 1).toLowerCase() + name.substring(1);

        if (attributes == null) {
            return name;
        }

        if (!"".equals(attributes.get("value"))) {
            name = (String) attributes.get("value");
        }

        return name;
    }

    /**
     * 获取需要扫描包的路径
     * @param metadata
     * @return
     */
    protected Set<String> getScanPackages(AnnotationMetadata metadata) {
        // 获取注解属性
        Map<String, Object> attributes = metadata.getAnnotationAttributes(
                EnableFakeClients.class.getCanonicalName());

        Set<String> packages = new HashSet<>();

        for (String path : (String[]) attributes.get("value")) {
            if (path != null && !"".equals(path)) {
                packages.add(path);
            }
        }

        for (String path : (String[]) attributes.get("basePackages")) {
            if (path != null && !"".equals(path)) {
                packages.add(path);
            }
        }

        // 默认扫描包下所有
        if (packages.isEmpty()) {
            packages.add(ClassUtils.getPackageName(metadata.getClassName()));
        }

        return packages;
    }

    /**
     * 生成一个scanner
     * 规则: 独立类, 顶层类或者静态内部类
     * 非注解类
     * @return
     */
    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {

            @Override
            protected boolean isCandidateComponent(
                    AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                // 注解的接口类是否是独立, 一般顶层类或者静态内部类
                if (beanDefinition.getMetadata().isIndependent()) {
                    // 类不是注解, 则认为是候选类
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }

                return isCandidate;
            }
        };
    }

    /**
     * 生成标注FakeClient的动态代理类
     * 将类注入到spring 容器中
     *
     * @param registry
     * @param annotationMetadata
     * @param attributes
     */
    private void registryFakeClient(BeanDefinitionRegistry registry,
                                    AnnotationMetadata annotationMetadata,
                                    Map<String, Object> attributes) {
        String className = annotationMetadata.getClassName();
        Class clazz = ClassUtils.resolveClassName(className, null);

        ConfigurableBeanFactory beanFactory = registry instanceof ConfigurableBeanFactory
                ? (ConfigurableBeanFactory) registry : null;

        String contextId = getFakeClientName(attributes, clazz);

        FakeClientFactoryBean factoryBean = new FakeClientFactoryBean();
        factoryBean.setBeanFactory(beanFactory);
        factoryBean.setType(clazz);
        factoryBean.setDomain(getDomain(attributes));

        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(clazz, () -> {
                    LOG.info("生成" + clazz.getSimpleName() + "动态代理对象, 并注入spring 容器中...");
                    return factoryBean.getObject();
                });

        // 根据类型自动装配
        definitionBuilder.setAutowireMode(
                AbstractAutowireCapableBeanFactory.AUTOWIRE_BY_TYPE);
        // 懒加载
        definitionBuilder.setLazyInit(true);

        AbstractBeanDefinition beanDefinition = definitionBuilder.getBeanDefinition();
        beanDefinition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, className);
        beanDefinition.setAttribute("fakeClientsRegistrarFactoryBean", factoryBean);

        beanDefinition.setPrimary(true);

        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition,
                className, new String[]{contextId + "FakeClient"});

        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    /**
     * 获取请求的域名URL
     * @param attributes
     * @return
     */
    private String getDomain(Map<String, Object> attributes) {
        if ("".equals(attributes.get("domain"))) {
            throw new IllegalStateException("请求域名不能为空, @"
                    + FakeClient.class.getSimpleName());
        }

        return (String) attributes.get("domain");
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
