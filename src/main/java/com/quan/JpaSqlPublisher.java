package com.quan;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * @author: xiexinquan520@163.com
 * User: XieXinQuan
 * DATE:2021/5/15
 */
public class JpaSqlPublisher implements ApplicationEventPublisherAware {

    static ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        JpaSqlPublisher.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * 发布事件
     * @param jpaEventContent
     */
    protected static void publisher(JpaEventContent jpaEventContent) {
        JpaSqlPublisher.applicationEventPublisher.publishEvent(new JpaDynamicSqlEvent(jpaEventContent));
    }

    protected static void publisher(String originSql, String jpaDynamicSql) {
        publisher(new JpaEventContent(originSql, jpaDynamicSql));
    }
}
