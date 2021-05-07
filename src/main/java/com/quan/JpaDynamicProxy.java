package com.quan;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author: xiexinquan520@163.com
 * User: XieXinQuan
 * DATE:2021/4/29
 */
public class JpaDynamicProxy implements BeanPostProcessor, InitializingBean, DisposableBean {

    @Value("${spring.jpa.dynamic-sql.repository-package}")
    private String repositoryPackage;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof JpaRepository && Proxy.isProxyClass(bean.getClass())){
            ProxyFactory proxyFactory = new ProxyFactory(bean);
            Class<?>[] proxiedInterfaces = proxyFactory.getProxiedInterfaces();
            if (proxiedInterfaces != null && proxiedInterfaces.length > 0){

                Class targetRepository = null;
                for (Class proxiedInterface : proxiedInterfaces){
                    if (proxiedInterface.getName().indexOf(repositoryPackage) == 0){
                        targetRepository = proxiedInterface;
                        break;
                    }
                }
                if (targetRepository != null){
                    Method[] methods = targetRepository.getDeclaredMethods();
                    if (methods != null && methods.length > 0){
                        proxyFactory.addAdvice(new JpaDynamicMethodInterceptor(bean, methods));

                        LogUtils.debug("Jpa Dynamic Sql, Proxying Repository : {}", targetRepository.getName());
                        return proxyFactory.getProxy();
                    }
                }else {
                    LogUtils.trace("Jpa Dynamic Sql, Want to Proxy : {}, but not in {}.", beanName, repositoryPackage);
                }
            }
        }
        return bean;
    }

    @Override
    public void destroy() {
        LogUtils.info("Jpa Dynamic Sql closed");
    }

    @Override
    public void afterPropertiesSet() {
        LogUtils.info("Jpa Dynamic Sql Init, Proxy Repository Package : {}", repositoryPackage);
    }
}
