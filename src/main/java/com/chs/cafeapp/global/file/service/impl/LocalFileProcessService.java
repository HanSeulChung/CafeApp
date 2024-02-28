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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Profile("dev")
@Service
@RequiredArgsConstructor
public class LocalFileProcessService implements FileProcessService {

  @Value("${cloud.aws.s3.bucket}")
  private String mockBucket;

  private final AmazonS3 amazonS3;

  @Override
  public String uploadImage(MultipartFile file, FileFolder fileFolder) {
    String fileName = getFileFolder(fileFolder) + createFileName(file.getOriginalFilename());

    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(file.getSize());
    objectMetadata.setContentType(file.getContentType());

    try (InputStream inputStream = file.getInputStream()) {
      amazonS3.putObject(new PutObjectRequest(mockBucket, fileName, inputStream, objectMetadata).withCannedAcl(
          CannedAccessControlList.PublicReadWrite));

    } catch (IOException ioe) {
      throw new IllegalArgumentException(String.format("파일 변환 중 에러가 발생했습니다 (%s)", file.getOriginalFilename()));
    }

    return getFileUrl(fileName);
  }

  @Override
  public String createFileName(String originalFileName) {
    return "Local-" + UUID.randomUUID().toString().concat(getFileExtension(originalFileName));
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
    amazonS3.deleteObject(new DeleteObjectRequest(mockBucket, fileName));
  }

  public String getFileUrl(String fileName) {
    return amazonS3.getUrl(mockBucket, fileName).toString();
  }
}
