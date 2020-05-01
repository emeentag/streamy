package com.streamy.upload.storage;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * IStorageService
 */
public interface IStorageService {
  void init() throws StorageException;

  void store(MultipartFile file) throws StorageException;

  Optional<Stream<Path>> loadAll() throws StorageException;

  Optional<Path> load(String fileName);

  Optional<Resource> loadAsResource(String fileName) throws StorageException;

  void deleteAll();

  void delete(String fileName) throws StorageException;
}