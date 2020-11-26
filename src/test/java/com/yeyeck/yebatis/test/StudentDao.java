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
