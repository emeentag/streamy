package com.streamy.upload.storage;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * IStorageService
 */
public interface IStorageService {
  void init();

  void store(MultipartFile file);

  Stream<Path> loadAll();

  Path load(String fileName);

  Resource loadAsResource(String fileName);

  void deleteAll();

  void delete(String fileName);
}