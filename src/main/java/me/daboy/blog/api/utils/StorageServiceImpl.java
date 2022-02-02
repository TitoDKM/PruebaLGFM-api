package me.daboy.blog.api.utils;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class StorageServiceImpl implements StorageService {
    private final Path root = Paths.get("H:\\PruebaLGFM\\web\\public\\assets\\images\\uploads");

    @Override
    public void store(MultipartFile file, String fileName) {
        try {
            Files.copy(file.getInputStream(), this.root.resolve(fileName));
        } catch (IOException e) {
            System.out.println("Error while trying to upload file: " + e.getMessage());
        }
    }
}
