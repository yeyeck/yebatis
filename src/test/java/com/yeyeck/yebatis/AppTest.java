package com.yeyeck.yebatis;

import java.util.List;

import com.yeyeck.yebatis.proxy.ProxyFactory;
import com.yeyeck.yebatis.test.Student;
import com.yeyeck.yebatis.test.StudentDao;

public class AppTest {

  public static void main(String[] args) {
    // 代理接口
    StudentDao studentDao = (StudentDao)ProxyFactory.proxyMapper(StudentDao.class);
    // 使用接口
    List<Student> students = studentDao.findAll();
    System.out.println(students);
  }
  
}
