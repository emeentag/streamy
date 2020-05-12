package com.streamy.integration.upload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamy.configs.AppConfig;
import com.streamy.upload.storage.FileSystemStorageService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StringUtils;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class FileUploadControllerIntegrationTest {

  @Autowired
  AppConfig appConfig;

  @Autowired
  FileSystemStorageService storageService;

  @Autowired
  MockMvc mockMvc;

  ObjectMapper objectMapper;

  @BeforeEach
  public void setUp() {
    objectMapper = new ObjectMapper();
    createFile("test1.json", "test2.json", "test3.json");
  }

  @AfterEach
  public void cleanUp() {
    storageService.deleteAll();
  }

  private void createFile(final String... fileNames) {
    storageService.init();

    for (final String fileName : fileNames) {
      final Path file = storageService.load(fileName).get();

      try {
        Files.createFile(file);
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public void listUploadedFilesShouldListTheFilesInUploadDirectory() throws Exception {
    // when
    final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/files"));

    final MvcResult result = resultActions.andReturn();
    final String content = result.getResponse().getContentAsString();

    // then
    resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    Assertions.assertEquals(
        "[{\"fileName\":\"test1.json\",\"fileSize\":\"0.00\"},{\"fileName\":\"test2.json\",\"fileSize\":\"0.00\"},{\"fileName\":\"test3.json\",\"fileSize\":\"0.00\"}]",
        content);
  }

  @Test
  public void serveFileShouldReturnOKWhenFileExists() throws Exception {
    // when
    final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/files/test1.json"));

    final MvcResult result = resultActions.andReturn();

    // then
    resultActions.andExpect(MockMvcResultMatchers.status().isOk());

    final String downloadedFile = result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION);
    Assertions.assertEquals("attachment; filename=\"test1.json\"", downloadedFile);
  }

  @Test
  public void serveFileShouldReturnNOTFOUNDWhenFileNotExist() throws Exception {
    // when
    final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/files/test100.json"));

    // then
    resultActions.andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void deleteFileShouldReturnOK() throws Exception {
    // when
    int firstSize = storageService.loadAll().get().toArray().length;
    final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/files/test1.json"));
    int secondSize = storageService.loadAll().get().toArray().length;

    final MvcResult result = resultActions.andReturn();

    // then
    resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    Assertions.assertEquals(result.getResponse().getContentAsString(), "File deleted.");
    Assertions.assertEquals(firstSize - 1, secondSize);
  }

  @Test
  public void handleFileUploadShouldStoreTheFileAndReturnOK() throws Exception {
    // given
    cleanUp();
    createFile("test1.json");
    final String message = "Hello World!";
    final byte[] content = message.getBytes();
    final MockMultipartFile mFile = new MockMultipartFile("file", "test1.json", "application/json", content);

    // when
    final ResultActions resultActions = mockMvc
        .perform(MockMvcRequestBuilders.multipart("/files/upload").file(mFile).param("isRealtime", "false"));

    // then
    resultActions.andExpect(MockMvcResultMatchers.status().isOk());

    String datePrefix = new SimpleDateFormat(appConfig.getUploadDateFormat()).format(new Date());
    String fileName = datePrefix + "-" + StringUtils.cleanPath(mFile.getOriginalFilename());

    storageService.loadAll().get().forEach(p -> {
      if (!p.toString().equalsIgnoreCase("test1.json")) {
        boolean isExists = p.toString().contains(fileName.substring(0, 5));
        Assertions.assertTrue(isExists);
      }
    });
  }

}