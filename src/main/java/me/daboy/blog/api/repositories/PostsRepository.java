package me.daboy.blog.api.repositories;

import me.daboy.blog.api.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostsRepository extends PagingAndSortingRepository<Post, Long> {
    Optional<Post> findById(Long id);
    Page<Post> findAllByCategory(Long id, Pageable pageable);
    Page<Post> findAllByTitleContainingOrBodyContaining(String title, String body, Pageable pageable);
}
