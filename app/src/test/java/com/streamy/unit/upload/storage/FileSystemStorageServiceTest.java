package com.streamy.unit.upload.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

import com.streamy.configs.AppConfig;
import com.streamy.upload.storage.FileSystemStorageService;
import com.streamy.upload.storage.StorageException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@TestInstance(Lifecycle.PER_CLASS)
public class FileSystemStorageServiceTest {
  private Path path;
  private FileSystemStorageService service;
  private AppConfig appConfig;

  @BeforeAll
  public void setUp() {
    appConfig = new AppConfig();
    appConfig.setLocation("target/files");
    appConfig.setUploadDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
    path = Paths.get(appConfig.getLocation());
  }

  @AfterAll
  public void after() {
    clearDirectory();
  }

  @BeforeEach
  public void init() {
    service = new FileSystemStorageService();
    ReflectionTestUtils.setField(service, "appConfig", appConfig);

    clearDirectory();
  }

  public void clearDirectory() {
    // Clear file before each test.
    if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
      FileSystemUtils.deleteRecursively(path.toFile());
    }
  }

  @Test
  public void initShouldCreateDirectories() {
    // when
    service.init();

    // then
    Assertions.assertTrue(Files.exists(path, LinkOption.NOFOLLOW_LINKS));
  }

  @Test
  public void initShouldThrowExceptionWhenDirectoryNotExist() {
    // given
    final AppConfig appConfig = new AppConfig();
    appConfig.setLocation("/fake/directory");
    ReflectionTestUtils.setField(service, "appConfig", appConfig);

    // then
    Assertions.assertThrows(StorageException.class, () -> {
      // when
      service.init();
    });
  }

  @Test
  public void deleteAllShouldDeleteCreatedFiles() {
    // given
    service.init();

    // when
    service.deleteAll();

    // then
    Assertions.assertFalse(Files.exists(path, LinkOption.NOFOLLOW_LINKS));
  }

  @Test
  public void deleteAllShouldThrowExceptionWhenDirectoryNotExist() {
    // then
    Assertions.assertThrows(NullPointerException.class, () -> {
      // when
      service.deleteAll();
    });
  }

  @Test
  public void loadShouldResolveTheFileInCreatedPath() {
    // given
    service.init();

    // when
    Path file = service.load("test.json");
    try {
      file = Files.createFile(file);
    } catch (final IOException e) {
      e.printStackTrace();
    }

    // then
    Assertions.assertTrue(Files.exists(file, LinkOption.NOFOLLOW_LINKS));
  }

  @Test
  public void loadAllShouldResolveTheFilesInCreatedPath() {
    // given
    Stream<Path> paths = null;
    service.init();
    Path file1 = service.load("test1.json");
    Path file2 = service.load("test2.json");
    Path file3 = service.load("test3.json");
    try {
      file1 = Files.createFile(file1);
      file2 = Files.createFile(file2);
      file3 = Files.createFile(file3);

    } catch (final IOException e) {
      e.printStackTrace();
    }

    // when
    paths = service.loadAll();

    // then
    paths.forEach(p -> {
      Assertions.assertTrue(Files.exists(service.load(p.toString()), LinkOption.NOFOLLOW_LINKS));
    });
  }

  @Test
  public void loadAllShouldThrowExceptionWhenDirectoryNotExist() {
    // given
    ReflectionTestUtils.setField(service, "root", Paths.get("/fake/directory"));

    // then
    Assertions.assertThrows(StorageException.class, () -> {
      // when
      service.loadAll();
    });
  }

  @Test
  public void loadAsResourceShouldReturnResource() {
    // given
    service.init();
    Path file = service.load("test1.json");
    try {
      file = Files.createFile(file);
    } catch (final IOException e) {
      e.printStackTrace();
    }

    // when
    final Resource resource = service.loadAsResource("test1.json");

    // then
    Assertions.assertEquals(resource.getFilename(), "test1.json");
  }

  @Test
  public void loadAsResourceShouldThrowException() {
    // given
    service.init();

    // then
    Assertions.assertThrows(StorageException.class, () -> {
      // when
      service.loadAsResource("test.json");
    });
  }

  @Test
  public void storeShouldThrowExceptionWhenFileIsEmpty() {
    // given
    service.init();

    // then
    Exception exception = Assertions.assertThrows(StorageException.class, () -> {
      // Create a multipartfile
      MultipartFile mfile = null;
      Path file = service.load("test.json");
      try {
        file = Files.createFile(file);
        final byte[] content = Files.readAllBytes(file);
        mfile = new MockMultipartFile("test.json", "test.json", "application/json", content);

      } catch (final IOException e) {
        e.printStackTrace();
      }

      // when
      service.store(mfile);
    });

    Assertions.assertTrue(exception.getMessage().contains("Failed to store empty file"));

  }

  @Test
  public void storeShouldThrowExceptionWhenFileHasSecurityIssues() {
    // given
    service.init();

    // then
    Exception exception = Assertions.assertThrows(StorageException.class, () -> {
      // Create a multipartfile
      MultipartFile mfile = null;
      Path file = service.load("test.json");
      String message = "Hello World!";
      try {
        file = Files.write(file, message.getBytes());
        final byte[] content = Files.readAllBytes(file);
        mfile = new MockMultipartFile("test.json", "..test.json", "application/json", content);

      } catch (final IOException e) {
        e.printStackTrace();
      }

      // when
      service.store(mfile);
    });

    Assertions
        .assertTrue(exception.getMessage().contains("Cannot store file with relative path outside current directory"));
  }

  @Test
  public void storeShouldThrowExceptionWhenFileHasCopyIssues() {
    // given
    service.init();

    // then
    Exception exception = Assertions.assertThrows(StorageException.class, () -> {
      // Create a multipartfile
      MultipartFile mfile = null;
      Path file = service.load("test.json");
      String message = "Hello World!";
      try {
        file = Files.write(file, message.getBytes());
        final byte[] content = Files.readAllBytes(file);
        mfile = new MockMultipartFile("test.json", "test.json", "application/json", content);

      } catch (final IOException e) {
        e.printStackTrace();
      }

      // when
      // Fake the path
      ReflectionTestUtils.setField(service, "root", Paths.get("/fake/directory"));
      service.store(mfile);
    });

    Assertions.assertTrue(exception.getMessage().contains("Failed to store file"));
  }

  @Test
  public void storeShouldCreateStoredFileByCopyingOriginalStream() {
    // given
    service.init();

    // Create a multipartfile
    MultipartFile mfile = null;
    Path file = service.load("test.json");
    String message = "Hello World!";
    try {
      file = Files.write(file, message.getBytes());
      final byte[] content = Files.readAllBytes(file);
      mfile = new MockMultipartFile("test.json", "test.json", "application/json", content);

    } catch (final IOException e) {
      e.printStackTrace();
    }

    // when
    service.store(mfile);

    // then
    String datePrefix = new SimpleDateFormat(appConfig.getUploadDateFormat()).format(new Date());
    String fileName = datePrefix + "-" + StringUtils.cleanPath(mfile.getOriginalFilename());

    service.loadAll().forEach(p -> {
      boolean isExists = p.toString().contains(fileName.substring(0, 5));
      Assertions.assertTrue(isExists);
    });

  }

  @Test
  public void deleteShouldDeleteTheFileByName() {
    // given
    service.init();
    Path file = service.load("test.json");
    try {
      file = Files.createFile(file);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Make sure file is created
    Stream<Path> files = service.loadAll();
    files.forEach(p -> {
      Assertions.assertTrue(Files.exists(service.load(p.toString()), LinkOption.NOFOLLOW_LINKS));
    });

    // when
    service.delete("test.json");

    // then
    files = service.loadAll();
    files.forEach(p -> {
      Assertions.assertFalse(Files.exists(p, LinkOption.NOFOLLOW_LINKS));
    });
  }
}