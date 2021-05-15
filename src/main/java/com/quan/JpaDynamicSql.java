package com.quan;

import org.hibernate.EmptyInterceptor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author: xiexinquan520@163.com
 * User: XieXinQuan
 * DATE:2021/4/29
 */
public class JpaDynamicSql extends EmptyInterceptor {

    /**
     * 由程序员决定哪些key需要动态
     */
    protected static ThreadLocal<List<String>> jpaParamReplace = new ThreadLocal<>();

    /**
     * 保存 jpa查询的参数值
     */
    protected static ThreadLocal<List<String>> jpaSelectParamNullValue = new ThreadLocal<>();

    public static void addParam(String ... keys){
        assert keys != null && keys.length != 0;
        if (jpaParamReplace.get() == null){
            jpaParamReplace.set(new LinkedList<>());
        }
        for (String key : keys) {
            jpaParamReplace.get().add(key);
        }
    }

    /**
     * 设置Jpa参数值
     * @param key
     */
    protected static void addJpaNullParam(String key){
        if (jpaSelectParamNullValue.get() == null) {
            jpaSelectParamNullValue.set(new LinkedList<>());
        }
        jpaSelectParamNullValue.get().add(key);
    }

    @Override
    public String onPrepareStatement(String sql) {
        List<String> dynamicKeys = jpaParamReplace.get();
        String select = sql.trim().substring(0, 6);
        if (CollectionUtils.isEmpty(dynamicKeys) || !"select".equalsIgnoreCase(select)){
            return super.onPrepareStatement(sql);
        }
        String selectSql = Constant.defaultStr;
        String whereSql = Constant.defaultStr;
        String groupSql = Constant.defaultStr;
        String orderSql = Constant.defaultStr;
        String limitSql = Constant.defaultStr;

        //查询出selectSql
        int whereIndex = -1;
        if ((whereIndex = sql.indexOf("where")) != -1 || (whereIndex = sql.indexOf("WHERE")) != -1){
        }else {
            return super.onPrepareStatement(sql);
        }
        selectSql = sql.substring(0, whereIndex);
        whereSql = sql.substring(whereIndex + 5);

        //查询出 groupSql
        int groupIndex = -1;
        if ((groupIndex = whereSql.indexOf("group by")) != -1 || (groupIndex = whereSql.indexOf("GROUP BY")) != -1){
            groupSql = whereSql.substring(groupIndex);
            whereSql = whereSql.substring(0, groupIndex);
        }

        //查询出 orderSql
        int orderIndex = -1;
        if (groupIndex == -1
                && ((orderIndex = whereSql.indexOf("order by")) != -1
                    || (orderIndex = whereSql.indexOf("ORDER BY")) != -1)){
            orderSql = whereSql.substring(orderIndex);
            whereSql = whereSql.substring(0, orderIndex);
        }

        //查询出 limitSql
        int limitIndex = -1;
        if (groupIndex == -1 && orderIndex == -1
                && ((limitIndex = whereSql.indexOf("limit")) != -1
                    || whereSql.indexOf("LIMIT") != -1)){
            limitSql = whereSql.substring(limitIndex);
            whereSql = whereSql.substring(0, limitIndex);
        }

        StringBuilder resultSql = new StringBuilder(selectSql);
        if (StringUtils.isEmpty(whereSql)){
            return super.onPrepareStatement(sql);
        }
        String[] andSql;
        if (whereSql.contains(" and ")){
            andSql = whereSql.split(" and ");
        }else {
            andSql = whereSql.split(" AND ");
        }
        List<String> dbConditionList = Arrays.asList(">=", "<>", ">", "<", "!=", "=", "not in", "NOT IN", "in", "IN", "is null", "IS NULL", "like", "LIKE");
        List<String> greater = Arrays.asList(">=", ">");
        List<String> less = Arrays.asList("<=", "<");
        List<String> in = Arrays.asList("not in", "NOT IN", "in", "IN");
        List<String> isNull = Arrays.asList("is null", "IS NULL");
        List<String> equals = Arrays.asList("=", "<>", "!=", "like", "LIKE");
        List<String> andSqlList = new ArrayList<>(4);
        for (String and : andSql){
            String append = Constant.defaultStr;
            out:
            for (String dbCondition : dbConditionList){
                int index = and.indexOf(dbCondition);
                if (index == -1){
                    continue;
                }
                int pointIndex = and.indexOf(".");
                pointIndex = pointIndex == -1 ? 0 : pointIndex + 1;
                String dbField = and.substring(pointIndex, index);
                String key = this.dbFieldToJavaField(dbField);
                if (greater.contains(dbCondition)){
                    append = Replace.INSTANCE.greaterEquals(and, key);
                }else if (less.contains(dbCondition)){
                    append = Replace.INSTANCE.lessEquals(and, key);
                }else if (in.contains(dbCondition)){
                    append = Replace.INSTANCE.in(and, key);
                }else if (isNull.contains(dbCondition)){
                    append = Replace.INSTANCE.isNull(and, key);
                }else if (equals.contains(dbCondition)){
                    append = Replace.INSTANCE.equalsOrLike(and, key);
                }else {
                    append = and;
                }
                break out;
            }
            if (!StringUtils.isEmpty(append)){
                andSqlList.add(append);
            }
        }
        if (!andSqlList.isEmpty()) {
            resultSql.append(" where ");
            for (int i = 0; i < andSqlList.size(); i++) {
                if (i != 0){
                    resultSql.append(" and ");
                }
                resultSql.append(andSqlList.get(i));
            }
        }
        resultSql.append(groupSql);
        resultSql.append(orderSql);
        resultSql.append(limitSql);

        String result = resultSql.toString();
        //发布事件
        JpaSqlPublisher.publisher(sql, result);

        return super.onPrepareStatement(result);
    }

    private String dbFieldToJavaField(String dbField){
        char[] chars = dbField.trim().toCharArray();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < chars.length; i++){
            char c = chars[i];
            if (c == '_'){
                continue;
            }else {
                if (i != 0 && chars[i-1] == '_'){
                    result.append((char) (c - 32));
                }else {
                    result.append(c);
                }
            }
        }
        return result.toString();
    }

}
