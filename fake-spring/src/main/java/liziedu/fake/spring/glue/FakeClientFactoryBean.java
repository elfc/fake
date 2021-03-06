package liziedu.fake.spring.glue;

import com.liziedu.fake.core.CustomHeaders;
import com.liziedu.fake.core.Fake;
import com.liziedu.fake.core.FakeTarget;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * 创建spring bean 工厂类
 */
public class FakeClientFactoryBean implements FactoryBean<Object>,
        InitializingBean, ApplicationContextAware, BeanFactoryAware {

    private Class<?> type;

    private String domain;

    private Class<? extends CustomHeaders> customHeaders;

    private ApplicationContext applicationContext;

    private BeanFactory beanFactory;

    /**
     * FactoryBean 重写方法
     * @return
     */
    @Override
    public Object getObject() {
        return getTarget();
    }

    <T> T getTarget() {
        Map<String, String> headers = null;
        if (customHeaders != null && beanFactory.getBean(customHeaders) != null) {
            headers = beanFactory.getBean(customHeaders).getHeaders();
        }

        return (T) new Fake.Builder().build()
                .newInstance(new FakeTarget.DefaultTarget<>(type, domain, headers));
    }

    /**
     * FactoryBean 重写方法
     * @return
     */
    @Override
    public Class<?> getObjectType() {
        return this.type;
    }

    /**
     * 重写FactoryBean 方法
     * @return
     */
    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * InitializingBean 重写方法
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {

    }

    /**
     * ApplicationContextAware 重写方法
     * @param context
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }

    /**
     * BeanFactoryAware 重写方法
     * @param beanFactory
     * @throws BeansException
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setCustomHeaders(Class<? extends CustomHeaders> customHeaders) {
        this.customHeaders = customHeaders;
    }
}
