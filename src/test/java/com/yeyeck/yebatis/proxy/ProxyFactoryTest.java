package com.yeyeck.yebatis.proxy;

import java.time.LocalDate;

import com.yeyeck.yebatis.test.StudentDao;

import org.junit.jupiter.api.Test;

public class ProxyFactoryTest {

  private final static StudentDao studentDao= (StudentDao)ProxyFactory.proxyMapper(StudentDao.class);
  
  @Test
  public void proxyMapper() {
    LocalDate birthday = studentDao.findBirthdayById(2);
    System.out.println(birthday);    
  }
}
