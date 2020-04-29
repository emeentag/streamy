package com.streamy.unit.upload.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import com.streamy.upload.storage.FileSystemStorageService;
import com.streamy.upload.storage.StorageException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.FileSystemUtils;

public class FileSystemStorageServiceTest {
  private FileSystemStorageService service;
  private final String testFilePath = "target/files";
  private static Path path;

  @BeforeEach
  public void init() {
    service = new FileSystemStorageService();
    ReflectionTestUtils.setField(service, "location", testFilePath);
    path = Paths.get(testFilePath);

    clearDirectory();
  }

  @AfterAll
  public static void after() {
    clearDirectory();
  }

  public static void clearDirectory() {
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
    ReflectionTestUtils.setField(service, "location", "/fake/directory");

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
    } catch (IOException e) {
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

    } catch (IOException e) {
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
    ReflectionTestUtils.setField(service, "rootLocation", Paths.get("/fake/directory"));

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
    } catch (IOException e) {
      e.printStackTrace();
    }

    // when
    Resource resource = service.loadAsResource("test1.json");
    System.out.println();

    // then
    Assertions.assertEquals(resource.getFilename(), "test1.json");
  }

  @Test
  public void loadAsResourceShouldThrowException() {
    // given
    service.init();

    // when
    Assertions.assertThrows(StorageException.class, () -> {
      // then
      service.loadAsResource("test.json");
    });
  }
}