package com.yeyeck.yebatis.db;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import com.yeyeck.yebatis.test.Student;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DBUtilTest {

  private static final DBUtils dbUtils = DBUtils.getInstance();
  private static Connection connection;

  @BeforeEach
  public void getConnection() {
    connection = dbUtils.getConnection();
  }

  @AfterEach
  public void releaseConnection() {
    dbUtils.release(connection);
  }

  @Test
  public void selectOne() {
    String sql = "select * from t_student where id = ?";
    Student student = dbUtils.selectOne(connection, Student.class, sql, 2);
    Assertions.assertNotNull(student);
  }

  @Test
  public void selectList() {
    String sql = "select * from t_student";
    List<Student> list = dbUtils.selectList(connection, Student.class, sql);
    Assertions.assertTrue(list.size() > 0);
  }

  @Test
  public void selectValue() {
    String sql = "select birthday from t_student where id = ?";
    LocalDate birthday = dbUtils.selectValue(connection, LocalDate.class, sql, 2);
    Assertions.assertNotNull(birthday);
  }

  @Test
  public void execute() {
    String sql = "update t_student set name = ? where id = ?";
    Integer res = dbUtils.execute(connection, sql, "李莫愁", 2);
    Assertions.assertEquals(1, res);
  }
  
}
