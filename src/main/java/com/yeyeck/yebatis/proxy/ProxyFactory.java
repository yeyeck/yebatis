package com.yeyeck.yebatis.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.yeyeck.yebatis.annotation.Delete;
import com.yeyeck.yebatis.annotation.Insert;
import com.yeyeck.yebatis.annotation.Select;
import com.yeyeck.yebatis.annotation.Update;
import com.yeyeck.yebatis.db.DBUtils;

public class ProxyFactory {
  public static Object proxyMapper(Class<?>... clazz) {
    return Proxy.newProxyInstance(ProxyFactory.class.getClassLoader(), clazz, new SQLInvocationHandler());
  }

  private static class SQLInvocationHandler implements InvocationHandler {

    private Class<?>[] primaryClasses = new Class<?>[] {
      Integer.class, Double.class, Byte.class, Boolean.class, 
      Long.class, Float.class, Character.class, Short.class,
      String.class,LocalDateTime.class, LocalDate.class
    };

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      DBUtils dbUtils = DBUtils.getInstance();
      Connection connection = dbUtils.getConnection();
			if (method.isAnnotationPresent(Select.class)) {
        Select one = method.getAnnotation(Select.class);
        String sql = one.value();

        Type returnType = method.getGenericReturnType();
        // 判断是否存在泛型
        if (returnType instanceof ParameterizedType) {
          ParameterizedType actualType = (ParameterizedType) returnType;
          // 集合支持List
          if (actualType.getRawType().equals(List.class)) {
            Class<?> mapperType = (Class<?>)actualType.getActualTypeArguments()[0];
            return dbUtils.selectList(connection, mapperType, sql, args);
          }
          throw new RuntimeException("Unsupported Return Type: " + method.toString());
        }
        Class<?> clazz = (Class<?>)returnType;
        if (isPrimaryType(clazz)){
          return dbUtils.selectValue(connection, clazz, sql, args);
        } else {
          return dbUtils.selectOne(connection, clazz, sql, args);
        }
      } else if (method.isAnnotationPresent(Update.class)) {
        Update update = method.getAnnotation(Update.class);
        return dbUtils.execute(connection, update.value(), args);
      } else if (method.isAnnotationPresent(Delete.class)) {
        Delete delete = method.getAnnotation(Delete.class);
        return dbUtils.execute(connection, delete.value(), args);
      } else if (method.isAnnotationPresent(Insert.class)) {
        Insert insert = method.getAnnotation(Insert.class);
        return dbUtils.execute(connection, insert.value(), args);
      }
			return null;
    }

    private boolean isPrimaryType(Class<?> clazz) {
      for (Class<?> c : primaryClasses) {
        if (clazz.equals(c)) {
          return true;
        }
      }
      return false;
    }  
   }
}
