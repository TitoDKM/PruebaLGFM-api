package me.daboy.blog.api.controllers;

import me.daboy.blog.api.entities.Category;
import me.daboy.blog.api.entities.Post;
import me.daboy.blog.api.repositories.CategoriesRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("api/categories")
public class CategoriesController {
    @Autowired
    private CategoriesRepository categoriesRepository;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpEntity<String> categoriesList() {
        List<Category> categories = categoriesRepository.findAll();
        JSONArray jsonResponse = new JSONArray();
        for(Category category : categories) {
            JSONObject tempCategory = new JSONObject();
            tempCategory.put("id", category.getId());
            tempCategory.put("title", category.getTitle());
            tempCategory.put("featured", category.getFeatured());
            jsonResponse.put(tempCategory);
        }
        return ResponseEntity.ok(jsonResponse.toString());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpEntity<String> getCategory(@PathVariable Long id) {
        Optional<Category> category = categoriesRepository.findById(id);
        JSONObject jsonResponse = new JSONObject();

        if(category.isPresent()) {
            jsonResponse.put("id", category.get().getId());
            jsonResponse.put("title", category.get().getTitle());
            jsonResponse.put("featured", category.get().getFeatured());
        } else {
            jsonResponse.put("id", 0);
            jsonResponse.put("title", "Todas las categorías");
            jsonResponse.put("featured", false);
        }

        return ResponseEntity.ok(jsonResponse.toString());
    }
}
