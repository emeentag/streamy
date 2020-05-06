package com.streamy.upload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Service;

@Service
public class FileUploadService {

  public enum SizeType {
    BYTE, KB, MB, GB
  }

  public Float getFileSize(SizeType type, Path file) throws IOException {
    Float size;

    switch (type) {
      case BYTE:
        size = (float) Files.size(file);
        break;
      case KB:
        size = (float) (Files.size(file) / 1024F);
        break;
      case MB:
        size = (float) (Files.size(file) / (1024F * 1024F));
        break;
      case GB:
        size = (float) (Files.size(file) / (1024F * 1024F * 1024F));
        break;

      default:
        size = (float) (Files.size(file));
        break;
    }

    return size;
  }
}