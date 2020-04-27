package com.realtimeProcessor.configs;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfig {
  @Value("${spark.app.name}")
  private String appName;
  @Value("${spark.app.master}")
  private String masterUri;

  @Bean
  public SparkConf conf() {
    SparkConf conf = new SparkConf();
    return conf.setAppName(appName).setMaster(masterUri);
  }

  @Bean
  public JavaSparkContext sc() {
    JavaSparkContext jctx = new JavaSparkContext(conf());
    return jctx;
  }
}