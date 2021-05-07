# quan-spring-boot-starter
Spring Boot with Quan support,help you simplify Jpa Dynamic Sql Config in Spring Boot.

Maven Install

在Repository调用前配置需要动态的key
JpaDynamicSql.addParam("name", "minAge", "maxAge", "height", "sex");
List<Test> list = testRepository.findAllByNameAndAgeGreaterThanEqualAndAgeLessThanEqualAndHeightAndSexIn(null, null, null, null, Arrays.asList("男", "女"));
