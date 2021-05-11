
### Spring Boot with Quan support,help you simplify Jpa Dynamic Sql Config in Spring Boot.
#### 1. 首先把源码下载
    在pom.xml中导入Spring Boot Starter所需的包

#### 2. 如何使用?<br>
    Maven Install

#### 3. 在你的项目中配置pom.xml
    <dependency>
        <groupId>com.quan</groupId>
        <artifactId>groundhog-spring-boot-starter</artifactId>
        <version>1.0</version>
    </dependency>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <debug>true</debug>
                    <debuglevel>lines,vars,source</debuglevel>
                    <compilerArgs>
                        <arg>-parameters</arg>
                    </compilerArgs>
                </configuration>
            </plugin>
        </plugins>
    </build>

#### 4. 在application.yml中配置
    spring:
      jpa:
        dynamic-sql:
          repository-package: com.quan.repository
        properties:
          hibernate:
            ejb:
              interceptor: com.quan.JpaDynamicSql

#### 5. 在Repository调用前配置需要动态的key<br>
##### 5.1 example:
###### MySQL:
    CREATE TABLE `test` (
      `id` int(11) NOT NULL AUTO_INCREMENT,
      `name` varchar(5) DEFAULT NULL,
      `age` int(11) DEFAULT NULL,
      `height` int(11) DEFAULT NULL,
      `sex` char(1) DEFAULT NULL,
      PRIMARY KEY (`id`),
      KEY `index_test` (`id`) USING BTREE
    ) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
    INSERT INTO `test` VALUES ('1', '曹操', '66', '161', '男');
    INSERT INTO `test` VALUES ('2', '孙权', '77', '190', '男');
    INSERT INTO `test` VALUES ('3', '貂蝉', '18', '160', '女');
    INSERT INTO `test` VALUES ('4', '刘备', '62', '177', '男');
###### Entity:
    @Data
    @Entity
    @Table(name = "test")
    public class Test {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;
        
        private String name;
        
        private Integer age;
        
        private Integer height;
        
        private String sex;
    }
###### Repository:
    @Repository
    public interface TestRepository extends JpaRepository<Test, Integer> {
    
        List<Test> findAllByNameAndAgeGreaterThanEqualAndAgeLessThanAndHeightAndSexIn(String name, Integer minAge, Integer maxAge, Integer height, List<String> sex);
        
        @Query("select t from Test t where t.name = :name and t.age >= :minAge and t.age < :maxAge and t.height = :height and t.sex in (:sex)")
        List<Test> selectTest(@Param("name") String name, @Param("minAge") Integer minAge,
                              @Param("maxAge") Integer maxAge, @Param("height") Integer height, @Param("sex") List<String> sex);
    }
###### Service:
    public class TestService {
        @Resource
        private TestRepository testRepository;
        
        public List<Test> selectUser() {
            JpaDynamicSql.addParam("name", "minAge", "maxAge", "height", "sex");
            List<Test> list = testRepository.findAllByNameAndAgeGreaterThanEqualAndAgeLessThanEqualAndHeightAndSexIn( "貂蝉", 18, 22, 160, Arrays.asList("男", "女"));
            //List<Test> list = testRepository.findAllByNameAndAgeGreaterThanEqualAndAgeLessThanEqualAndHeightAndSexIn( null, null, null, null, null);
            
            JpaDynamicSql.addParam("name", "minAge", "maxAge", "height", "sex");
            List<Test> list2 = testRepository.selectTest("貂蝉", 18, 22, 160, Arrays.asList("男", "女"));
            //List<Test> list2 = testRepository.selectTest(null, null, null, null, null);
            return list;
        }
    }
#### 注意: key的规则
    1. 与Jpa生成的字段一致 db: my_name  Java: myName
    2. 范围查询时 大于 > or >= 的key 要加上max Java: myAge key: maxMyAge  小于则min 
