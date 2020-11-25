package com.yeyeck.yebatis.proxy;

import java.time.LocalDate;

import com.yeyeck.yebatis.test.Student;
import com.yeyeck.yebatis.test.StudentDao;

import org.junit.jupiter.api.Test;

public class ProxyFactoryTest {
  
  @Test
  public void proxyMapper() {
    StudentDao studentDao = (StudentDao)ProxyFactory.proxyMapper(StudentDao.class);
    // System.out.println(studentDao.findAll());
    // System.out.println(studentDao.updateName("小王八", 1));
    // System.out.println(studentDao.findById(1));

    Integer insert = studentDao.addStudent("牛夫人", LocalDate.now(), "四川成都", "13333333333");
    
  }
}
