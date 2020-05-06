package com.streamy.upload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Stream;

import com.streamy.upload.FileUploadService.SizeType;
import com.streamy.upload.storage.FileSystemStorageService;
import com.streamy.upload.storage.StorageException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadController {

  @Autowired
  FileSystemStorageService storageService;

  @Autowired
  FileUploadService uploadService;

  @GetMapping(value = "/files", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> listUploadedFiles() {

    Stream<Path> files = storageService.loadAll().get();

    JSONArray response = files.map(p -> {
      JSONObject jObject = new JSONObject();
      try {
        jObject.put("fileName", p.toString());
        Float fSize = uploadService.getFileSize(SizeType.MB, storageService.load(p.toString()).get());
        jObject.put("fileSize", String.format("%.2f MB", fSize));
      } catch (JSONException | IOException e) {
        e.printStackTrace();
      }

      return jObject;

    }).collect(Collector.of(JSONArray::new, JSONArray::put, JSONArray::put));

    return ResponseEntity.ok(response.toString());
  }

  @GetMapping(value = "/files/{fileName:.+}")
  @ResponseBody
  public ResponseEntity<Resource> serveFile(@PathVariable String fileName) {
    Optional<Resource> file = storageService.loadAsResource(fileName);
    if (file.isPresent()) {
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.get().getFilename() + "\"")
          .body(file.get());
    }

    return ResponseEntity.notFound().build();
  }

  @PostMapping(value = "/files/upload")
  public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,
      @RequestParam("isRealtime") boolean isRealtime) {
    storageService.store(file);
    return ResponseEntity.ok().build();
  }

  @ExceptionHandler(StorageException.class)
  public ResponseEntity<?> storageExcResponseEntity(StorageException exception) {
    return ResponseEntity.notFound().build();
  }

}