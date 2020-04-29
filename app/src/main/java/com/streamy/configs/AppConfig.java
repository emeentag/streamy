package com.streamy.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@Data
public class AppConfig {
  @Value("${upload.storage.file-system.location}")
  private String location;

  @Value("${upload.storage.date-format}")
  private String uploadDateFormat;
}