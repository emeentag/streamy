package com.streamy.processing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/process")
public class ProcessingController {

  // @Autowired
  // ProcessingService processingService;

  @GetMapping(value = "/{fileName}")
  public ResponseEntity<String> processFile(@PathVariable String fileName) {
    log.info("fileName: " + fileName);

    return ResponseEntity.ok("test");
  }
}