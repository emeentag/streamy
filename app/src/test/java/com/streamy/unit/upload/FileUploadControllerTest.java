package com.streamy.unit.upload;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamy.upload.FileUploadController;
import com.streamy.upload.FileUploadService;
import com.streamy.upload.storage.FileSystemStorageService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class FileUploadControllerTest {

  @Mock
  private FileSystemStorageService storageService;

  @Mock
  private FileUploadService uploadService;

  private FileUploadController controller;

  private String location = "target/test/shared/upload/files";

  private ObjectMapper objectMapper;

  @BeforeEach
  public void init() {
    objectMapper = new ObjectMapper();
    controller = new FileUploadController();
    ReflectionTestUtils.setField(controller, "storageService", storageService);
    ReflectionTestUtils.setField(controller, "uploadService", uploadService);
  }

  private Path createFile() {
    Path file = null;
    try {
      Path root = Paths.get(location);
      Files.createDirectories(root);

      file = root.resolve("test.json");
      String message = "Hello World!";
      file = Files.write(file, message.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return file;
  }

  private void clearDirectory() {
    Path path = Paths.get(location);
    // Clear file before each test.
    if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
      FileSystemUtils.deleteRecursively(path.toFile());
    }
  }

  @Test
  public void listUploadedFilesShouldReturnNameOfTheFiles() throws IOException {
    // given
    Path file1 = Paths.get("test1.json");
    Path file2 = Paths.get("test2.json");
    Path file3 = Paths.get("test3.json");
    Stream<Path> files = Arrays.asList(file1, file2, file3).stream();
    Mockito.when(storageService.loadAll()).thenReturn(Optional.ofNullable(files));
    Mockito.when(storageService.load("test1.json")).thenReturn(Optional.ofNullable(file1));
    Mockito.when(storageService.load("test2.json")).thenReturn(Optional.ofNullable(file2));
    Mockito.when(storageService.load("test3.json")).thenReturn(Optional.ofNullable(file3));
    Mockito.when(uploadService.getFileSize(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(30F);

    // when
    ResponseEntity<String> result = controller.listUploadedFiles();
    String payload = result.getBody();
    JsonNode results = objectMapper.readTree(payload);

    // then
    Assertions.assertEquals(3, results.size());
    Assertions.assertEquals(
        "[{\"fileName\":\"test1.json\",\"fileSize\":\"30.00\"},{\"fileName\":\"test2.json\",\"fileSize\":\"30.00\"},{\"fileName\":\"test3.json\",\"fileSize\":\"30.00\"}]",
        payload);
  }

  @Test
  public void serveFileShouldReturnOKWhenFileResourceExist() {
    // given
    Path file = createFile();
    Resource resource = null;

    try {
      resource = new UrlResource(file.toUri());
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

    Mockito.when(storageService.loadAsResource("test.json")).thenReturn(Optional.of(resource));

    // when
    ResponseEntity<Resource> result = controller.serveFile("test.json");

    // then
    Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());

    clearDirectory();
  }

  @Test
  public void serveFileShouldReturnNOTFOUNDWhenFileNotExist() {
    // given
    Mockito.when(storageService.loadAsResource("test.json")).thenReturn(Optional.empty());

    // when
    ResponseEntity<Resource> result = controller.serveFile("test.json");

    // then
    Assertions.assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
  }

  @Test
  public void handleFileUploadShouldReturnOK() {
    // given
    Path file = createFile();
    byte[] content;
    MultipartFile mFile = null;
    try {
      content = Files.readAllBytes(file);
      mFile = new MockMultipartFile("test.json", "test.json", "application/json", content);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // when
    ResponseEntity<String> result = controller.handleFileUpload(mFile, false);

    // then
    Mockito.verify(storageService, Mockito.times(1)).store(mFile);
    Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());

    clearDirectory();
  }
}