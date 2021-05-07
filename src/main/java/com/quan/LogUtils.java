package com.quan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: xiexinquan520@163.com
 * User: XieXinQuan
 * DATE:2021/4/30
 */
public class LogUtils {

    private static Logger log = LoggerFactory.getLogger(JpaDynamicSql.class);

    protected static void debug(String str, String ... param){
        if (log.isDebugEnabled()){
            str = str.replace("{}", "%s");
            log.debug(String.format(str, param));
        }
    }

    protected static void info(String str, String ... param){
        if (log.isInfoEnabled()){
            str = str.replace("{}", "%s");
            log.info(String.format(str, param));
        }
    }

    protected static void trace(String str, String ... param){
        if (log.isTraceEnabled()){
            str = str.replace("{}", "%s");
            log.trace(String.format(str, param));
        }
    }

    protected static void warn(String str, String ... param){
        if (log.isWarnEnabled()){
            str = str.replace("{}", "%s");
            log.warn(String.format(str, param));
        }
    }
}
