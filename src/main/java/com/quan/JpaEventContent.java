package com.quan;

/**
 * @author: xiexinquan520@163.com
 * User: XieXinQuan
 * DATE:2021/5/15
 */
public class JpaEventContent {

    private String originSql;

    private String dynamicSql;

    public JpaEventContent(String originSql, String dynamicSql) {
        this.originSql = originSql;
        this.dynamicSql = dynamicSql;
    }

    public String getOriginSql() {
        return originSql;
    }

    public String getDynamicSql() {
        return dynamicSql;
    }
}
