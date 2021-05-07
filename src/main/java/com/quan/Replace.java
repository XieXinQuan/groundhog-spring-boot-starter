package com.quan;

import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: xiexinquan520@163.com
 * User: XieXinQuan
 * DATE:2021/4/30
 */
public class Replace {

    protected static Replace INSTANCE = new Replace();

    /**
     * sql =
     * @param sql
     * @param key
     * @return
     */
    protected String equalsOrLike(String sql, String key){
        boolean isNeedDynamic = this.isNeedDynamic(key);
        if (isNeedDynamic) {
            LogUtils.debug("{} is equals to set default value, be delete", key);
            return "(1 = 1 or '1' = ?)";
        }
        return sql;
    }

    /**
     * sql (>, >=)
     * @param sql
     * @param key
     * @return
     */
    protected String greaterEquals(String sql, String key) {
        String tranKey = "max" + (char) (key.charAt(0) - 32) + key.substring(1);
        boolean isNeedDynamic = this.isNeedDynamic(tranKey);
        if (isNeedDynamic) {
            LogUtils.debug("origin key : {}, condition is greater , key need change to -- {} --> {}", key, key, tranKey);
            LogUtils.debug("{} is equals to set default value, be delete", tranKey);
            return "(1 =1 or '1' = ? )";
        }
        return sql;
    }

    /**
     * sql (<, <=)
     * @param sql
     * @param key
     * @return
     */
    protected String lessEquals(String sql, String key){
        String tranKey = "min" + (char) (key.charAt(0) - 32) + key.substring(1);
        boolean isNeedDynamic = this.isNeedDynamic(tranKey);
        if (isNeedDynamic) {
            LogUtils.debug("origin key : {}, condition is less , key need change to -- {} --> {}", key, key, tranKey);
            LogUtils.debug("{} is equals to set default value, be delete", tranKey);
            return "(1 =1 or '1' = ? )";
        }
        return sql;
    }

    /**
     * sql (in, not in)
     * @param sql
     * @param key
     * @return
     */
    protected String in(String sql, String key){
        boolean isNeedDynamic = this.isNeedDynamic(key);
        if (isNeedDynamic) {
            LogUtils.debug("{} is equals to set default value, be delete", key);
            return " ( 1 = 1 or '1' = ?) ";
        }
        return sql;
    }

    /**
     * sql is null
     * @param sql
     * @param key
     * @return
     */
    protected String isNull(String sql, String key) {
        boolean isNeedDynamic = this.isNeedDynamic(key);
        if (isNeedDynamic) {
            LogUtils.debug("{} is equals to set default value, be delete", key);
            return "";
        }
        return sql;
    }

    private boolean isNeedDynamic(String key) {
        List<String> jpaParam = JpaDynamicSql.jpaParamReplace.get();
        List<String> jpaSelectParam = JpaDynamicSql.jpaSelectParamNullValue.get();
        if (CollectionUtils.isEmpty(jpaParam) || CollectionUtils.isEmpty(jpaSelectParam)) {
            return false;
        }
        return jpaParam.contains(key) && jpaSelectParam.contains(key);
    }
}
