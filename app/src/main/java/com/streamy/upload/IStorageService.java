package com.streamy.upload;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.web.multipart.MultipartFile;

/**
 * IStorageService
 */
public interface IStorageService {
  void init();

  void store(MultipartFile file);

  Stream<Path> loadAll();

}