package com.chs.cafeapp.global.file.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.chs.cafeapp.global.file.service.FileProcessService;
import com.chs.cafeapp.global.file.type.FileFolder;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Profile("prod")
@Service
@RequiredArgsConstructor
public class AmazonS3FileProcessService implements FileProcessService {

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  private final AmazonS3 amazonS3;

  @Override
  public String uploadImage(MultipartFile file, FileFolder fileFolder) {
    String fileName = getFileFolder(fileFolder) + createFileName(file.getOriginalFilename());

    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(file.getSize());
    objectMetadata.setContentType(file.getContentType());

    try (InputStream inputStream = file.getInputStream()) {
      amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata).withCannedAcl(
          CannedAccessControlList.PublicReadWrite));
      log.info("AWS S3서버에 파일을 업로드 하였습니다.");
    } catch (IOException ioe) {
      log.error("파일 변환 중 에러가 발생했습니다 {}", file.getOriginalFilename());
      throw new IllegalArgumentException(String.format("파일 변환 중 에러가 발생했습니다 (%s)", file.getOriginalFilename()));
    }

    return getFileUrl(fileName);
  }

  @Override
  public String createFileName(String originalFileName) {
    return UUID.randomUUID().toString().concat(getFileExtension(originalFileName));
  }

  @Override
  public String getFileExtension(String fileName) {
    return fileName.substring(fileName.lastIndexOf("."));
  }

  @Override
  public void deleteImage(String url) {
    deleteFile(getFileName(url));
  }

  @Override
  public String getFileName(String url) {
    if (url == null) {
      return null;
    }
    String[] paths = url.split("/");
    return paths[paths.length-2] + "/" + paths[paths.length-1];
  }

  private String getFileFolder(FileFolder fileFolder) {
    switch (fileFolder) {
      case DRINK:
        return "drink/";
      case FOOD:
        return "food/";
      case GOODS:
        return "goods/";
      default:
        return "";
    }
  }

  private void deleteFile(String fileName) {
    if (fileName != null) {
      amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
      log.info("{}의 이미지 파일이 삭제되었습니다.", fileName);
    }
  }

  public String getFileUrl(String fileName) {
    return amazonS3.getUrl(bucket, fileName).toString();
  }
}

