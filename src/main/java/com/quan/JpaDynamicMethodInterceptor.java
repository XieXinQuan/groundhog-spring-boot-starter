package com.quan;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author: xiexinquan520@163.com
 * User: XieXinQuan
 * DATE:2021/4/30
 */
public class JpaDynamicMethodInterceptor implements MethodInterceptor {

    private Object repository;
    private List<Method> targetMethods;

    public JpaDynamicMethodInterceptor(Object repository, Method[] methods){
        this.repository = repository;
        this.targetMethods = Arrays.asList(methods);
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        //如有上次的缓存, 先清空上次的
        JpaDynamicSql.jpaSelectParamNullValue.remove();

        Object[] arguments = methodInvocation.getArguments();
        Method method = methodInvocation.getMethod();
        if (!targetMethods.contains(method)) {
            return method.invoke(repository, arguments);
        }
        Parameter[] parameters = method.getParameters();
        if (parameters == null || parameters.length == 0) {
            return method.invoke(repository, arguments);
        }
        //比对设置的动态列, 如果是日期等, 需要在调用时设置默认值, 否则jpa在生成Sql时会报错
        List<String> dynamicKeys = JpaDynamicSql.jpaParamReplace.get();
        if (CollectionUtils.isEmpty(dynamicKeys)) {
            return method.invoke(repository, arguments);
        }
        //开始设置jpaDynamicSql ThreadLocal
        for (int i = 0; i < parameters.length; i++){
            Parameter parameter = parameters[i];
            Object argument = arguments[i];
            if (!dynamicKeys.contains(parameter.getName())){
                continue;
            }
            if (argument == null){
                if (parameter.getType().isAssignableFrom(String.class)){
                    arguments[i] = Constant.defaultStr;
                    LogUtils.debug("参数 : {} 值为null, 设置为默认的String值: {}", parameter.getName(), Constant.defaultStr);
                }else if (parameter.getType().isAssignableFrom(Date.class)){
                    arguments[i] = Constant.minDate;
                    LogUtils.debug("参数 : {} 值为null, 设置为默认的Date值: {}", parameter.getName(), Constant.minDate.toString());
                }else if (parameter.getType().isAssignableFrom(Integer.class)){
                    arguments[i] = Constant.minInt;
                    LogUtils.debug("参数 : {} 值为null, 设置为默认的Integer值: {}", parameter.getName(), String.valueOf(Constant.minInt));
                }else if (parameter.getType().isAssignableFrom(java.sql.Date.class)){
                    arguments[i] = Constant.minDate;
                    LogUtils.debug("参数 : {} 值为null, 设置为默认的java.sql.Date值: {}", parameter.getName(), Constant.minDate.toString());
                }else {

                }
                JpaDynamicSql.addJpaNullParam(parameter.getName());
            }
        }
        try {
            LogUtils.debug("捕捉到需要动态的列: {}", dynamicKeys.toString());
            Object invoke = method.invoke(repository, arguments);
            LogUtils.debug("执行成功");
            return invoke;
        }finally {
            JpaDynamicSql.jpaSelectParamNullValue.remove();
            JpaDynamicSql.jpaParamReplace.remove();
        }
    }


    private void ss(){
        //Java基础，集合，JUC，了解23种设计模式
        //Spring, SpringMvc, Mybatis, Spring Data Jpa框架
        //熟悉SpringBoot 基于SpringBoot Starter实现Spring Data Jpa动态Sql，Github: https://github.com/XieXinQuan/groundhog-spring-boot-starter
        //熟悉Redis，RabbitMQ，Zookeeper分布式中间件
        //熟悉SpringCloud Alibaba组件
        //熟悉MySQL PostgreSQL，有SQL优化经验
        //熟悉Linux常用命令及Java问题分析工具，分析定位生产问题
    }

}
