package com.yeyeck.yebatis.db;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.yeyeck.yebatis.utils.ReflectUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DBUtils {

  private static final char UNDER_LINE = '_';
  private static final String SETTER_PREFFIX = "set";

  private HikariDataSource dataSource;

  private static final DBUtils instance = new DBUtils();


  private DBUtils() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl("jdbc:mysql://47.106.185.245:3306/test?characterEncoding=utf8");
    config.setUsername("root");
    config.setPassword("cckk00522");
    dataSource = new HikariDataSource(config);
  }

  public static DBUtils getInstance() {
    return instance;
  }

  public Connection getConnection(){
    try {
      return dataSource.getConnection();
    } catch (SQLException e) {
      throw new RuntimeException("SQLException: " + e.getMessage());
    }
  }
  public <T> T selectOne(Connection connection, Class<T> clazz, String sql, Object... args) {
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    try {
      statement = connection.prepareStatement(sql);
      initStatementArgs(statement, args);
      resultSet = statement.executeQuery();
      List<String> columns = getColumns(resultSet.getMetaData());
      if (resultSet.next()) {
        return mapTo(clazz, resultSet, columns);
      }
      return null;
    } catch (SQLException e) {
      throw new RuntimeException("SQLException: " + e.getMessage());
    } finally {
      release(statement, resultSet);
    }
  }

  public <T> List<T> selectList(Connection connection, Class<T> clazz, String sql, Object...args) {
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    List<T> list = new LinkedList<>();
    try {
      statement = connection.prepareStatement(sql);
      initStatementArgs(statement, args);
      resultSet = statement.executeQuery();
      List<String> columns = getColumns(resultSet.getMetaData());
      if (ReflectUtil.isPrimaryType(clazz)) {
        while (resultSet.next()) {
          list.add(resultSet.getObject(1, clazz));
        }
      } else {
        while (resultSet.next()) {
          T t = mapTo(clazz, resultSet, columns);
          list.add(t);
        }
      }

    } catch (SQLException e) {
      throw new RuntimeException("SQLException: " + e.getMessage());
    } finally {
      release(statement, resultSet);
    }
    return list;
  }

  public <T> T selectValue(Connection connection, Class<T> clazz, String sql, Object...args) {
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    try {
      statement = connection.prepareStatement(sql);
      initStatementArgs(statement, args);
      resultSet = statement.executeQuery();
      if (resultSet.next()) {
        return resultSet.getObject(1, clazz);
      } else {
        return null;
      }
    } catch (SQLException e) {
      throw new RuntimeException("SQLException: " + e.getMessage());
    } finally {
      release(statement, resultSet);
    }
  }


  public int execute(Connection connection, String sql, Object...args) {
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    try {
      statement = connection.prepareStatement(sql);
      initStatementArgs(statement, args);
      return statement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("SQLException: " + e.getMessage());
    } finally {
      release(statement, resultSet);
    }
  }

  private void release(PreparedStatement statement, ResultSet resultSet) {
    if (resultSet != null) {
      try {
        resultSet.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    if (statement != null) {
      try {
        statement.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public <T> T mapTo(Class<T> clazz, ResultSet row, List<String> columns) throws SQLException {

    Object obj = null;
    try {
      obj = clazz.getConstructor().newInstance();
      for (String column: columns) {
        String fieldName = toCamelCase(column);
        Field field = clazz.getDeclaredField(fieldName);
        Class<?> fieldType = field.getType();
        Method setter = clazz.getMethod(getSetter(column), field.getType());
        Object value = row.getObject(column, fieldType);
        setter.invoke(obj, value);
      }
    } catch (NoSuchMethodException e){
      throw new RuntimeException("NoSuchMethodException: " + e.getMessage());
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
        | SecurityException e) {
      throw new RuntimeException(e.getMessage());
    } catch (NoSuchFieldException e) {
      throw new RuntimeException("NoSuchFieldException: " + e.getMessage());
    }
    
    return clazz.cast(obj);
  }

  public void release(Connection connection) {
    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  private void initStatementArgs(PreparedStatement statement, Object...args) throws SQLException {
    if (args != null) {
      for (int i = 0; i < args.length; i ++) {
        statement.setObject(i+1, args[i]);
      }
    }
  }

  private List<String> getColumns(ResultSetMetaData metaData) throws SQLException {
    List<String> list = new LinkedList<>();
    int countCols = metaData.getColumnCount();
    for (int i = 1; i <= countCols; i++) {
      list.add(metaData.getColumnName(i));
    }
    return list;
  }

  private String getSetter(String column) {
    StringBuilder builder = new StringBuilder(SETTER_PREFFIX);
    boolean upper = true;
    for (int i = 0; i < column.length(); i ++) {
      char c = column.charAt(i);
      if (c == UNDER_LINE) {
        upper = true;
        continue;
      }
      if (upper) {
        builder.append(Character.toUpperCase(c));
        upper = false;
      } else {
        builder.append(c);
      }
    }
    return builder.toString();
  }

  private String toCamelCase(String column) {
    if (column.indexOf(UNDER_LINE) == -1) return column;
    StringBuilder builder = new StringBuilder();
    boolean upper = false;
    for (int i = 0; i < column.length(); i ++) {
      char c = column.charAt(i);
      if (c == UNDER_LINE) {
        upper = true;
        continue;
      }
      if (upper) {
        builder.append(Character.toUpperCase(c));
        upper = false;
      } else {
        builder.append(c);
      }
    }
    return builder.toString();
  }

  
}
