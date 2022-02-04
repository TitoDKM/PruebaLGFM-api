package me.daboy.blog.api.controllers;

import me.daboy.blog.api.entities.Category;
import me.daboy.blog.api.entities.Post;
import me.daboy.blog.api.entities.User;
import me.daboy.blog.api.repositories.CategoriesRepository;
import me.daboy.blog.api.repositories.PostsRepository;
import me.daboy.blog.api.repositories.UsersRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("api/posts")
public class PostsController {
    @Autowired
    private PostsRepository postsRepository;
    @Autowired
    private CategoriesRepository categoriesRepository;
    @Autowired
    private UsersRepository usersRepository;

    @GetMapping("/latests")
    public HttpEntity<HashMap<String, Object>> getPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "0") Long category) {
        Pageable first = PageRequest.of(page, size, Sort.by("id").descending());
        HashMap<String, Object> jsonResponse = new HashMap<>();
        Page<Post> posts;
        if(category == 0)
             posts = postsRepository.findAll(first);
        else
            posts = postsRepository.findAllByCategory(category, first);

        jsonResponse.put("posts", posts.getContent());
        jsonResponse.put("total", posts.getTotalElements());
        jsonResponse.put("current", posts.getNumber());
        jsonResponse.put("pages", posts.getTotalPages());

        return ResponseEntity.ok(jsonResponse);
    }

    @GetMapping("/single/{id}")
    public HttpEntity<String> getPost(@PathVariable Long id) {
        Optional<Post> post = postsRepository.findById(id);
        JSONObject jsonResponse = new JSONObject();

        if(post.isPresent()) {
            Optional<Category> cat = categoriesRepository.findById(post.get().getCategory());
            jsonResponse.put("id", post.get().getId());
            jsonResponse.put("author", post.get().getAuthor());
            jsonResponse.put("date", post.get().getDate());
            jsonResponse.put("title", post.get().getTitle());
            jsonResponse.put("category", cat.isPresent() ? cat.get().getTitle() : "Sin categoría");
            jsonResponse.put("category_id", cat.get().getId());
            jsonResponse.put("image", post.get().getImage());
            jsonResponse.put("body", post.get().getBody());
            jsonResponse.put("comments_type", post.get().getCommentsType());
        }
        else return ResponseEntity.notFound().build();

        return ResponseEntity.ok(jsonResponse.toString());
    }

    @GetMapping("/search")
    public HttpEntity<HashMap<String, Object>> getPost(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "25") int size, @RequestParam String search) {
        HashMap<String, Object> jsonResponse = new HashMap<>();
        Pageable first = PageRequest.of(page, size);

        Page<Post> allPosts = postsRepository.findAllByTitleContainingOrBodyContaining(search, search, first);
        jsonResponse.put("posts", allPosts.getContent());
        jsonResponse.put("total", allPosts.getTotalElements());
        jsonResponse.put("current", allPosts.getNumber());
        jsonResponse.put("pages", allPosts.getTotalPages());

        return ResponseEntity.ok(jsonResponse);
    }

    @PostMapping("/create")
    public HttpEntity<HashMap<String, Object>> addPost(@RequestBody Map<String, Object> payload) {
        HashMap<String, Object> jsonResponse = new HashMap<>();
        if(payload.isEmpty() || payload.get("title").equals("") || payload.get("category").equals("") || payload.get("comments").equals("") || payload.get("body").equals("")) {
            jsonResponse.put("message", "Rellena todos los campos para realizar una publicación");
            return ResponseEntity.badRequest().body(jsonResponse);
        }

        Optional<User> owner = usersRepository.findByEmail(payload.get("email").toString());
        if(!owner.isPresent()) {
            jsonResponse.put("messsage", "Ha ocurrido un error al crear la publicación, vuelve a intentarlo");
            return ResponseEntity.badRequest().body(jsonResponse);
        }
        String body = payload.get("body").toString();
        body = body.replaceAll("&nbsp;", "");

        if(payload.containsKey("id") && !payload.get("id").toString().equals("")) {
            Optional<Post> post = postsRepository.findById(Long.valueOf(payload.get("id").toString()));
            if(!post.isPresent()) {
                jsonResponse.put("messsage", "Ha ocurrido un error al editar la publicación, vuelve a intentarlo");
                return ResponseEntity.badRequest().body(jsonResponse);
            }

            post.get().setTitle(payload.get("title").toString());
            post.get().setBody(payload.get("body").toString());
            post.get().setCategory(Long.valueOf(payload.get("category").toString()));
            post.get().setCommentsType(Integer.valueOf(payload.get("comments").toString()));
            if(!payload.get("image").equals(""))
                post.get().setImage("/assets/images/uploads/" + payload.get("image").toString());
            postsRepository.save(post.get());

            jsonResponse.put("id", post.get().getId());
        } else {
            Post post = new Post(null, owner.get().getId(), payload.get("title").toString(), body, new Timestamp(System.currentTimeMillis()), Long.valueOf(payload.get("category").toString()),
                    "/assets/images/uploads/" + payload.get("image").toString(), Integer.valueOf(payload.get("comments").toString()));
            Post createdPost = postsRepository.save(post);

            jsonResponse.put("id", createdPost.getId());
        }
        return ResponseEntity.ok(jsonResponse);
    }
}
