package com.quan;

import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * @author: xiexinquan520@163.com
 * User: XieXinQuan
 * DATE:2021/4/30
 */
public class Constant {

    /**
     * 默认1970年1月1日
     */
    public static Date minDate = new Date(0L);

    /**
     * 默认为 2100年1月1日
     */
    public static Date maxDate = new Date(4102416000000L);

    /**
     * 默认为 -520
     */
    public static Integer minInt = -520;

    /**
     * 默认为 520
     */
    public static Integer maxInt = 520;

    /**
     * 默认为 ""
     */
    public static String defaultStr = Strings.EMPTY;

    public static Collection defaultCollection = new ArrayList();

    static {
        defaultCollection.add(new Object());
    }


}
