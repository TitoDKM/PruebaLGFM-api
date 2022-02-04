package me.daboy.blog.api.controllers;

import me.daboy.blog.api.dtos.CommentDTO;
import me.daboy.blog.api.entities.Comment;
import me.daboy.blog.api.entities.User;
import me.daboy.blog.api.repositories.CommentsRepository;
import me.daboy.blog.api.repositories.UsersRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("api/comments")
public class CommentsController {
    @Autowired
    private CommentsRepository commentsRepository;
    @Autowired
    private UsersRepository usersRepository;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpEntity<String> getComments(@RequestParam Long postId) {
        JSONArray jsonResponse = new JSONArray();

        List<Comment> comments = commentsRepository.findAllByPostId(postId);
        for(Comment comment : comments) {
            JSONObject tempComment = new JSONObject();
            Optional<User> owner = usersRepository.findById(comment.getAuthor());
            if(owner.isPresent()) {
                tempComment.put("id", comment.getId());
                tempComment.put("author_photo", owner.get().getPhoto());
                tempComment.put("author_name", owner.get().getFirstname() + " " + owner.get().getLastname());
                tempComment.put("body", comment.getBody());
                jsonResponse.put(tempComment);
            }
        }

        return ResponseEntity.ok(jsonResponse.toString());
    }

    @PostMapping(value = "/anon", produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpEntity<HashMap<String, Object>> addAnonComment(@RequestBody Map<String, Object> payload) {
        HashMap<String, Object> jsonResponse = new HashMap<>();
        if(payload.isEmpty() || payload.get("body").equals("")) {
            jsonResponse.put("message", "No puedes enviar un comentario vacío");
            return ResponseEntity.badRequest().body(jsonResponse);
        }

        Comment comment = new Comment(null, 0L, Long.valueOf(payload.get("postId").toString()), payload.get("body").toString(), new Timestamp(System.currentTimeMillis()));
        Comment created = commentsRepository.save(comment);

        CommentDTO commentDTO = new CommentDTO(created.getId(), "/assets/images/default.jpeg", "Invitado", created.getBody());

        jsonResponse.put("comment", commentDTO);
        return ResponseEntity.ok(jsonResponse);
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpEntity<HashMap<String, Object>> addComment(@RequestBody Map<String, Object> payload) {
        HashMap<String, Object> jsonResponse = new HashMap<>();
        if(payload.isEmpty() || payload.get("body").equals("")) {
            jsonResponse.put("message", "No puedes enviar un comentario vacío");
            return ResponseEntity.badRequest().body(jsonResponse);
        }

        Optional<User> owner = usersRepository.findByEmail(payload.get("email").toString());

        if(!owner.isPresent()) {
            jsonResponse.put("message", "Ha ocurrido un error al publicar el comentario");
            return ResponseEntity.badRequest().body(jsonResponse);
        }

        Comment comment = new Comment(null, owner.get().getId(), Long.valueOf(payload.get("postId").toString()), payload.get("body").toString(), new Timestamp(System.currentTimeMillis()));
        Comment created = commentsRepository.save(comment);
        CommentDTO commentDTO = new CommentDTO(created.getId(), owner.get().getPhoto(), owner.get().getFirstname() + " " + owner.get().getLastname(), created.getBody());

        jsonResponse.put("comment", commentDTO);
        return ResponseEntity.ok(jsonResponse);
    }
}
