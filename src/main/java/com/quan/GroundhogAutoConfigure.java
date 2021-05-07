package com.quan;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: xiexinquan520@163.com
 * User: XieXinQuan
 * DATE:2021/4/29
 */
@ConfigurationProperties(prefix = "spring.jpa.dynamic-sql")
public class GroundhogAutoConfigure {

    private String repositoryPackage;

    public String getRepositoryPackage() {
        return repositoryPackage;
    }

    public void setRepositoryPackage(String repositoryPackage) {
        this.repositoryPackage = repositoryPackage;
    }
}
