package com.yeyeck.yebatis.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.List;

import com.yeyeck.yebatis.annotation.Delete;
import com.yeyeck.yebatis.annotation.Insert;
import com.yeyeck.yebatis.annotation.Select;
import com.yeyeck.yebatis.annotation.Update;
import com.yeyeck.yebatis.db.DBUtils;
import com.yeyeck.yebatis.utils.ReflectUtil;

public class ProxyFactory {
  public static Object proxyMapper(Class<?>... clazz) {
    return Proxy.newProxyInstance(ProxyFactory.class.getClassLoader(), clazz, new SQLInvocationHandler());
  }

  // 代理接口
  private static class SQLInvocationHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      DBUtils dbUtils = DBUtils.getInstance();
      Connection connection = dbUtils.getConnection();
      // 检查Select注解
			if (method.isAnnotationPresent(Select.class)) {
        Select one = method.getAnnotation(Select.class);
        String sql = one.value();

        Type returnType = method.getGenericReturnType();
        // 判断返回类型是否存在泛型
        if (returnType instanceof ParameterizedType) {
          ParameterizedType actualType = (ParameterizedType) returnType;
          // 集合支持List
          if (actualType.getRawType().equals(List.class)) {
            // 获取泛型的类型
            Class<?> mapperType = (Class<?>)actualType.getActualTypeArguments()[0];
            return dbUtils.selectList(connection, mapperType, sql, args);
          }
          throw new RuntimeException("Unsupported Return Type: " + method.toString());
        }
        // 不存在泛型
        Class<?> clazz = (Class<?>)returnType;
        if (ReflectUtil.isPrimaryType(clazz)){
          // 基本类型
          return dbUtils.selectValue(connection, clazz, sql, args);
        } else {
          return dbUtils.selectOne(connection, clazz, sql, args);
        }
      } else if (method.isAnnotationPresent(Update.class)) {
        // 处理Update
        Update update = method.getAnnotation(Update.class);
        return dbUtils.execute(connection, update.value(), args);
      } else if (method.isAnnotationPresent(Delete.class)) {
        // 处理Delete
        Delete delete = method.getAnnotation(Delete.class);
        return dbUtils.execute(connection, delete.value(), args);
      } else if (method.isAnnotationPresent(Insert.class)) {
        // 处理Insert
        Insert insert = method.getAnnotation(Insert.class);
        return dbUtils.execute(connection, insert.value(), args);
      }
			return null;
    }  
  }
}
