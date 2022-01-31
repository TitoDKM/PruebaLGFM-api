package me.daboy.blog.api.repositories;

import me.daboy.blog.api.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriesRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByFeatured(Boolean featured);
}
