package com.quan;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
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
        try {

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
                    LogUtils.debug("参数 : {} 值为null", parameter.getName());
                    if (parameter.getType().isAssignableFrom(String.class)){
                        arguments[i] = Constant.defaultStr;
                    }else if (parameter.getType().isAssignableFrom(Date.class)){
                        arguments[i] = Constant.minDate;
                    }else if (parameter.getType().isAssignableFrom(Integer.class)){
                        arguments[i] = Constant.minInt;
                    }else if (parameter.getType().isAssignableFrom(java.sql.Date.class)){
                        arguments[i] = Constant.minDate;
                    }else if (parameter.getType().isAssignableFrom(List.class)
                            || parameter.getType().isAssignableFrom(Set.class)){
                        String typeName = parameter.getParameterizedType().getTypeName();
                        String[] split = typeName.split("<");
                        if (split.length > 1) {
                            typeName = split[1].replace(">", "");
                            if (String.class.getName().equals(typeName)) {
                                arguments[i] = Arrays.asList(Constant.defaultStr);
                            }else if (Integer.class.getName().equals(typeName)) {
                                arguments[i] = Arrays.asList(Constant.minInt);
                            }else if (BigDecimal.class.getName().equals(typeName)) {
                                arguments[i] = Arrays.asList(BigDecimal.ZERO);
                            }
                        }else {
                            LogUtils.debug("参数 : {} 值为null, 即将报错", parameter.getName());
                        }
                    }
                    JpaDynamicSql.addJpaNullParam(parameter.getName());
                }
            }
            LogUtils.debug("捕捉到需要动态的列: {}", dynamicKeys.toString());
            Object invoke = method.invoke(repository, arguments);
            LogUtils.debug("执行成功");
            return invoke;
        }finally {
            JpaDynamicSql.jpaSelectParamNullValue.remove();
            JpaDynamicSql.jpaParamReplace.remove();
        }
    }

}
