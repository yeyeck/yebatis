package com.yeyeck.yebatis.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class ReflectUtil {
  private static final Class<?>[] primaryClasses = {
    Integer.class, Double.class, Byte.class, Boolean.class, 
    Long.class, Float.class, Character.class, Short.class,
    String.class,LocalDateTime.class, LocalDate.class, Date.class
  };

  public static  boolean isPrimaryType(Class<?> clazz) {
    for (Class<?> c : primaryClasses) {
      if (clazz.equals(c)) {
        return true;
      }
    }
    return false;
  }
}
