package com.chs.cafeapp.global.file.service;

import com.chs.cafeapp.global.file.type.FileFolder;
import org.springframework.web.multipart.MultipartFile;


public interface FileProcessService {

  String uploadImage(MultipartFile file, FileFolder fileFolder);
  String createFileName(String originalFileName);

  String getFileExtension(String fileName);
  void deleteImage(String url);
  String getFileName(String url);
}
