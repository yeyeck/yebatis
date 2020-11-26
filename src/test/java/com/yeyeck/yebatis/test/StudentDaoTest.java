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
