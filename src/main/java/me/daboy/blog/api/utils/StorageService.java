package me.daboy.blog.api.utils;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    void store(MultipartFile file, String fileName);
}
