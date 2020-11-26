package com.yeyeck.yebatis;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class HikariCPTest {
  private static HikariDataSource datasource;

  @BeforeAll
  public static void before() {
    HikariConfig config = new HikariConfig("db.properties");
    datasource = new HikariDataSource(config);
  }

  @Test
  public void getConnection() throws SQLException {
    Connection connection = datasource.getConnection();
    Assertions.assertNotNull(connection);
  }

  @AfterAll
  public static void after() {
    datasource.close();
  }
}
