可参考下列文章
[仿Mybatis手撸注解SQL实现DAO接口(上)——Java反射的使用](https://yeyeck.com/article/24)  
[仿Mybatis手撸注解SQL实现DAO接口(下)——动态代理的使用](https://yeyeck.com/article/25)
# 1. 修改db.properties
参考[HikariCP](https://github.com/brettwooldridge/HikariCP)
```properties
jdbcUrl=jdbc:mysql://localhost:3306/test?characterEncoding=utf8
username=root
password=password
dataSource.cachePrepStmts=true
dataSource.prepStmtCacheSize=250
dataSource.prepStmtCacheSqlLimit=2048
dataSource.useServerPrepStmts=true
dataSource.useLocalSessionState=true
dataSource.rewriteBatchedStatements=true
dataSource.cacheResultSetMetadata=true
dataSource.cacheServerConfiguration=true
dataSource.elideSetAutoCommits=true
dataSource.maintainTimeStats=false
```
# 使用
参考test目录下的[com.yeyeck.yebatis.test](https://github.com/yeyeck/yebatis/tree/master/src/test/java/com/yeyeck/yebatis/test)
1. 首先准备一张数据库表
```sql
CREATE DATABASE /*!32312 IF NOT EXISTS*/ `test` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `test`;
DROP TABLE IF EXISTS `t_student`;
CREATE TABLE `t_student` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `created_time` datetime DEFAULT NULL COMMENT 'created time',
  `updated_time` datetime DEFAULT NULL COMMENT 'updated time',
  `name` varchar(20) DEFAULT NULL COMMENT '姓名',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `address` varchar(200) DEFAULT NULL COMMENT '家庭住址',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系方式',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='学生信息表';

INSERT INTO `t_student` (`id`,`created_time`,`updated_time`,`name`,`birthday`,`address`,`phone`)
VALUES (2,'2020-11-03 11:43:06','2020-11-03 11:43:06','李自成','2000-05-01','广东韶关','177888888'),
(3,'2020-11-03 11:43:06','2020-11-03 11:43:06','王老五','2002-03-01','江苏南京','199888888'),
(4,'2020-11-03 11:43:06','2020-11-03 11:43:06','葛二蛋','2000-12-01','四川成都','166888888'),
(5,'2020-11-03 02:15:31','2020-11-03 02:15:31','赵四','2000-03-01','新疆乌鲁木齐','166666666'),
(6,'2020-11-25 17:17:44','2020-11-25 17:17:44','???','2020-11-25','????','13333333333'),
(7,'2020-11-25 17:20:18','2020-11-25 17:20:18','牛夫人','2020-11-25','四川成都','13333333333'),
(8,'2020-11-26 10:42:31','2020-11-26 10:42:31','蒋某','2020-11-26','广东东莞','1234322112');


```
2. 在test目录下写一个对应的类
这只是一段练习代码，并没有对命名方式做其他兼容
表的字段使用下划线命名，对应的java bean字段使用驼峰命名
比如表的字段 created_time 对应的java bean 字段为 createdTime
```java
package com.yeyeck.yebatis.test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Student {
  private Integer id;
  private String name;
  private LocalDate birthday;
  private String address;
  private String phone;
  private LocalDateTime createdTime;
  private LocalDateTime updatedTime;
}
```
3. 编写接口，使用注解sql
```java
package com.yeyeck.yebatis.test;

import java.time.LocalDate;
import java.util.List;

import com.yeyeck.yebatis.annotation.Delete;
import com.yeyeck.yebatis.annotation.Insert;
import com.yeyeck.yebatis.annotation.Select;
import com.yeyeck.yebatis.annotation.Update;

public interface StudentDao {
  @Select("select * from t_student where id = ?")
  Student findById(Integer id);

  @Select("select * from t_student")
  List<Student> findAll();

  @Select("select count(id) from t_student")
  Integer countAll();

  @Update("update t_student set name = ? where id = ?")
  int updateName(String name, Integer id);

  @Insert("insert into t_student(name, birthday, address, phone, created_time, updated_time)" + 
           "values (?, ?, ?, ?, now(), now())")
  int addStudent(String name, LocalDate birthday, String address, String phone);

  @Delete("delete from t_student where id = ?")
  int removeById(Integer id);

  @Select("select birthday from t_student where id = ?")
  LocalDate findBirthdayById(Integer id);

  @Select("select name from t_student")
  List<String> findAllNames();
  
}
```
4. 测试类
```java
package com.yeyeck.yebatis.test;

import java.time.LocalDate;
import java.util.List;

import com.yeyeck.yebatis.proxy.ProxyFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StudentDaoTest {

  private static final StudentDao studentDao = (StudentDao)ProxyFactory.proxyMapper(StudentDao.class);
  

  @Test
  public void findAll() {
    List<Student> list = studentDao.findAll();
    Assertions.assertNotNull(list);
    Assertions.assertFalse(list.size() == 0);
  }

  @Test
  public void countAll() {
    Assertions.assertFalse(studentDao.countAll() == 0);
  }

  @Test
  public void findById() {
    Student student = studentDao.findById(2);
    Assertions.assertNotNull(student);
  }

  @Test
  public void updateName() {
    Assertions.assertFalse(studentDao.updateName("李自成", 2) == 0);
  }

  @Test
  public void addStudent() {
    int res = studentDao.addStudent("蒋某", LocalDate.now(), "广东东莞", "1234322112");
    Assertions.assertTrue(res==1);
  }

  @Test
  public void removeById(Integer id) {
    int res = studentDao.removeById(3);
    Assertions.assertEquals(res, 1);
  }

  @Test
  public void findBirthdayById() {
    LocalDate birthday = studentDao.findBirthdayById(2);
    Assertions.assertNotNull(birthday);
  }

  @Test
  public void  findAllNames() {
    List<String> names = studentDao.findAllNames();
    System.out.println(names);
  }
}
```
5. 在实际生产代码中使用
```java
  public static void main(String[] args) {
    // 代理接口
    StudentDao studentDao = (StudentDao)ProxyFactory.proxyMapper(StudentDao.class);
    // 使用接口
    List<Student> students = studentDao.findAll();
    System.out.println(students);
  }
```


