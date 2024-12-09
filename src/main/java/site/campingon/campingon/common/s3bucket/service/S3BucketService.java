package site.campingon.campingon.common.s3bucket.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import site.campingon.campingon.common.exception.GlobalException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static site.campingon.campingon.common.exception.ErrorCode.*;


@Service
@RequiredArgsConstructor
public class S3BucketService {
  private final AmazonS3 s3Client; // S3 클라이언트 주입

  @Value("${aws.s3.bucket.name}")
  private String bucketName;

  // 단일 파일 업로드
  public String upload(MultipartFile file, String path) {
    String fileName = generateValidPath(path) + createUniqueFileName(file);
    String fileUrl = "https://" + bucketName + ".s3.amazonaws.com/" + fileName;

    try {
      s3Client.putObject(bucketName, fileName, file.getInputStream(), createFileMetadata(file));
      return fileUrl;
    } catch (IOException e) {
      throw new GlobalException(FILE_UPLOAD_FAILED);
    }
  }

  // 다중 파일 업로드
  public List<String> upload(List<MultipartFile> files, String path) {
    List<String> uploadedUrls = new ArrayList<>();

    for (MultipartFile file : files) {
      uploadedUrls.add(upload(file, path));
    }

    return uploadedUrls;
  }

  // 단일 파일 삭제
  public void remove(String filename) {
    if (filename == null || filename.trim().isEmpty()) {
      throw new IllegalArgumentException("파일명이 없습니다.");
    }

    try {
      if (!s3Client.doesObjectExist(bucketName, filename)) {
        throw new FileNotFoundException("파일을 찾을 수 없습니다: " + filename);
      }
      s3Client.deleteObject(bucketName, filename);
    } catch (Exception e) {
      throw new RuntimeException("파일 삭제 실패: " + e.getMessage());
    }
  }

  // 다중 파일 삭제
  public void remove(List<String> filenames) {
    List<String> failedFiles = new ArrayList<>();

    for (String filename : filenames) {
      try {
        remove(filename);
      } catch (Exception e) {
        failedFiles.add(filename + " (" + e.getMessage() + ")");
      }
    }

    if (!failedFiles.isEmpty()) {
      throw new RuntimeException("다음 파일들의 삭제가 실패했습니다: " + String.join(", ", failedFiles));
    }
  }

  // 고유 파일명 생성 (UUID 사용)
  private String createUniqueFileName(MultipartFile file) {
    String originalFileName = file.getOriginalFilename();
    String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
    return UUID.randomUUID().toString() + extension;
  }

  // 경로 생성 및 검증
  private String generateValidPath(String path) {
    String trimmedPath = path.trim();

    // 빈 문자열인 경우
    if (trimmedPath.isEmpty()) {
      return "";
    }

    // 허용되지 않는 문자 패턴 검사
    String invalidChars = "[\\\\/:*?\"<>|]";
    if (trimmedPath.matches(".*" + invalidChars + ".*")) {
      throw new IllegalArgumentException("경로에 허용되지 않는 특수문자가 포함되어 있습니다.");
    }

    // 경로 끝에 / 추가
    return trimmedPath.endsWith("/") ? trimmedPath : trimmedPath + "/";
  }

  // 파일 메타데이터 생성
  private ObjectMetadata createFileMetadata(MultipartFile file) {
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType(file.getContentType());
    metadata.setContentLength(file.getSize());
    return metadata;
  }

  public void moveObject(String sourceKey, String destinationKey) {
    // 원본 파일 존재 여부 확인
    if (!s3Client.doesObjectExist(bucketName, sourceKey)) {
      throw new GlobalException(FILE_NOT_FOUND);
    }

    try {
      CopyObjectRequest copyRequest = new CopyObjectRequest(
          bucketName,
          sourceKey,
          bucketName,
          destinationKey
      );

      CopyObjectResult copyResult = s3Client.copyObject(copyRequest);

      if (copyResult != null && copyResult.getETag() != null) {
        s3Client.deleteObject(bucketName, sourceKey);
      } else {
        throw new GlobalException(FILE_MOVE_FAILED);
      }

    } catch (AmazonS3Exception e) {
      throw new GlobalException(S3_OPERATION_FAILED);
    } catch (Exception e) {
      throw new GlobalException(FILE_MOVE_FAILED);
    }
  }
}
