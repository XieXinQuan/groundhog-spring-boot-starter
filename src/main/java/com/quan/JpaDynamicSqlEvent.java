package com.quan;

import org.springframework.context.ApplicationEvent;

/**
 * @author: xiexinquan520@163.com
 * User: XieXinQuan
 * DATE:2021/5/15
 */
public class JpaDynamicSqlEvent extends ApplicationEvent {

    public JpaDynamicSqlEvent(Object source) {
        super(source);
    }
}
