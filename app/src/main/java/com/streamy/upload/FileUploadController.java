package com.streamy.upload;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.streamy.upload.storage.FileSystemStorageService;
import com.streamy.upload.storage.StorageException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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

  @GetMapping(value = "/files")
  public ResponseEntity<List<String>> listUploadedFiles() {

    Stream<Path> files = storageService.loadAll().get();

    List<String> response = files.map(p -> p.toString()).collect(Collectors.toList());

    return ResponseEntity.ok(response);
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
  public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
    storageService.store(file);
    return ResponseEntity.ok().build();
  }

  @ExceptionHandler(StorageException.class)
  public ResponseEntity<?> storageExcResponseEntity(StorageException exception) {
    return ResponseEntity.notFound().build();
  }

}