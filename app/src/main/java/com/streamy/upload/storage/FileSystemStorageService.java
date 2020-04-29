package com.streamy.upload.storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements IStorageService {

  @Value("${upload.storage.file-system.location}")
  private String location;

  private Path rootLocation;

  @Override
  public void init() {
    try {
      this.rootLocation = Paths.get(location);
      Files.createDirectories(this.rootLocation);
    } catch (IOException e) {
      throw new StorageException("Could not initialize storage", e);
    }
  }

  @Override
  public void store(MultipartFile file) {
    // TODO Auto-generated method stub

  }

  @Override
  public Stream<Path> loadAll() {
    try {
      return Files.walk(this.rootLocation, 1).filter(path -> !path.equals(this.rootLocation))
          .map(this.rootLocation::relativize);
    } catch (IOException e) {
      throw new StorageException("Failed to read stored files", e);
    }
  }

  @Override
  public Path load(String fileName) {
    return rootLocation.resolve(fileName);
  }

  @Override
  public Resource loadAsResource(String fileName) {
    Path file = load(fileName);
    try {
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() && resource.isReadable()) {
        return resource;
      } else {
        throw new StorageException("Could not read file: " + fileName);
      }
    } catch (MalformedURLException e) {
      throw new StorageException("Could not read file: " + fileName, e);
    }
  }

  @Override
  public void deleteAll() {
    FileSystemUtils.deleteRecursively(this.rootLocation.toFile());
  }

}