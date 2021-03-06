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
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

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
  @PostConstruct
  public void init() throws StorageException {
    try {
      this.root = Paths.get(this.appConfig.getLocation());
      Files.createDirectories(this.root);
    } catch (IOException e) {
      throw new StorageException("Could not initialize storage", e);
    }
  }

  @Override
  public void store(MultipartFile file) throws StorageException {
    String datePrefix = new SimpleDateFormat(appConfig.getUploadDateFormat()).format(new Date());
    String fileName = datePrefix + "-" + StringUtils.cleanPath(file.getOriginalFilename());

    if (file.isEmpty()) {
      throw new StorageException("Failed to store empty file " + fileName);
    }
    if (fileName.contains("..")) {
      // This is a security check
      throw new StorageException("Cannot store file with relative path outside current directory " + fileName);
    }

    try {
      // Create new copy.
      InputStream inputStream = file.getInputStream();
      Files.copy(inputStream, load(fileName).get(), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new StorageException("Failed to store file " + fileName, e);
    }

  }

  @Override
  public Optional<Stream<Path>> loadAll() throws StorageException {
    try {
      Stream<Path> files = Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);

      return Optional.of(files);
    } catch (IOException e) {
      throw new StorageException("Failed to read stored files", e);
    }
  }

  @Override
  public Optional<Path> load(String fileName) {
    return Optional.of(root.resolve(fileName));
  }

  @Override
  public Optional<Resource> loadAsResource(String fileName) throws StorageException {
    Path file = load(fileName).get();
    try {
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() && resource.isReadable()) {
        return Optional.of(resource);
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

  @Override
  public void delete(String fileName) throws StorageException {
    try {
      Files.deleteIfExists(load(fileName).get());
    } catch (IOException e) {
      throw new StorageException("Failed to delete stored file" + fileName, e);
    }
  }

}