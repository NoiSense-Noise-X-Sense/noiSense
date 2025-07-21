package com.dosion.noisense.common.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataSourceCheck {
  @Autowired
  DataSource dataSource;

  @PostConstruct
  public void logDataSourceInfo() {
    if (dataSource instanceof HikariDataSource hikari) {
      System.out.println("===== Real JDBC URL : " + hikari.getJdbcUrl());
      System.out.println("===== Real JDBC Username : " + hikari.getUsername());
    }
  }
}
