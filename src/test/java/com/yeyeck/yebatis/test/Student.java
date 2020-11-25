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