package site.campingon.campingon.common.s3bucket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.campingon.campingon.common.s3bucket.service.S3BucketService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/s3/bucket")
@RequiredArgsConstructor
public class S3BucketController {
  private final S3BucketService s3BucketService;

  @PostMapping("/one")
  public ResponseEntity<String> uploadFile(
      @RequestParam("file") MultipartFile file,
      @RequestParam("path") String path
  ) {
    String fileUrl = s3BucketService.upload(file, path);
    return ResponseEntity.ok(fileUrl);
  }

  @PostMapping("/many")
  public ResponseEntity<List<String>> uploadFiles(
      @RequestParam("file") List<MultipartFile> files,
      @RequestParam("path") String path
  ) {
    List<String> fileUrls = s3BucketService.upload(files, path);
    return ResponseEntity.ok(fileUrls);
  }

  @DeleteMapping("/one")
  public ResponseEntity<Void> deleteFile(@RequestBody String filename) {
    s3BucketService.remove(filename);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/many")
  public ResponseEntity<Void> deleteFiles(@RequestBody List<String> filenames) {
    s3BucketService.remove(filenames);
    return ResponseEntity.noContent().build();
  }
}

