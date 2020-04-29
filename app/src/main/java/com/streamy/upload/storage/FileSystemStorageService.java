package com.streamy.upload.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

import com.streamy.configs.AppConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements IStorageService {

  @Autowired
  private AppConfig appConfig;

  private Path root;

  @Override
  public void init() {
    try {
      this.root = Paths.get(this.appConfig.getLocation());
      Files.createDirectories(this.root);
    } catch (IOException e) {
      throw new StorageException("Could not initialize storage", e);
    }
  }

  @Override
  public void store(MultipartFile file) {
    String datePrefix = new SimpleDateFormat(appConfig.getUploadDateFormat()).format(new Date());
    String fileName = datePrefix + StringUtils.cleanPath(file.getOriginalFilename());

    if (file.isEmpty()) {
      throw new StorageException("Failed to store empty file " + fileName);
    }
    if (fileName.contains("..")) {
      // This is a security check
      throw new StorageException("Cannot store file with relative path outside current directory " + fileName);
    }

    try {
      InputStream inputStream = file.getInputStream();
      Files.copy(inputStream, load(fileName), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new StorageException("Failed to store file " + fileName, e);
    }

  }

  @Override
  public Stream<Path> loadAll() {
    try {
      return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
    } catch (IOException e) {
      throw new StorageException("Failed to read stored files", e);
    }
  }

  @Override
  public Path load(String fileName) {
    return root.resolve(fileName);
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
    FileSystemUtils.deleteRecursively(this.root.toFile());
  }

}