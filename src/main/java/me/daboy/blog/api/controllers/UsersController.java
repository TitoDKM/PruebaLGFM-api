package me.daboy.blog.api.controllers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import me.daboy.blog.api.entities.User;
import me.daboy.blog.api.repositories.UsersRepository;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("api/users")
public class UsersController {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/login")
    public HttpEntity<HashMap<String, Object>> tryLogin(@RequestBody Map<String, Object> payload) {
        HashMap<String, Object> jsonResponse = new HashMap<>();
        if(payload.isEmpty() || !payload.containsKey("email") || !payload.containsKey("password")) {
            jsonResponse.put("message", "Rellena todos los campos para iniciar sesión");
            return ResponseEntity.badRequest().body(jsonResponse);
        }
        Optional<User> user = usersRepository.findByEmail(payload.get("email").toString());
        if(!user.isPresent()) {
            jsonResponse.put("message", "El email introducido no existe en nuestra base de datos");
            return ResponseEntity.badRequest().body(jsonResponse);
        }
        if(!bCryptPasswordEncoder.matches(payload.get("password").toString(), user.get().getPassword())){
            jsonResponse.put("message", "La contraseña introducida es incorrecta");
            return ResponseEntity.badRequest().body(jsonResponse);
        }

        String token = generateToken(payload.get("email").toString());
        jsonResponse.put("token", token);

        return ResponseEntity.ok(jsonResponse);
    }

    @PostMapping("/register")
    public HttpEntity<HashMap<String, Object>> tryRegister(@RequestBody Map<String, Object> payload) {
        HashMap<String, Object> jsonResponse = new HashMap<>();
        if(payload.isEmpty() || !payload.containsKey("email") || !payload.containsKey("password") || !payload.containsKey("password2")
            || !payload.containsKey("name") || !payload.containsKey("surname")) {
            jsonResponse.put("message", "Rellena todos los campos para completar el registro");
            return ResponseEntity.badRequest().body(jsonResponse);
        }

        String email = payload.get("email").toString();
        String password = payload.get("password").toString();
        if(!password.equals(payload.get("password2").toString())) {
            jsonResponse.put("message", "Las contraseñas introducidas no coinciden");
            return ResponseEntity.badRequest().body(jsonResponse);
        }

        Optional<User> emailTaken = usersRepository.findByEmail(email);
        if(emailTaken.isPresent()) {
            jsonResponse.put("message", "El correo electrónico introducido ya existe en nuestros registros");
            return ResponseEntity.badRequest().body(jsonResponse);
        }

        User newUser = new User(null, email, bCryptPasswordEncoder.encode(password), "", "", payload.get("name").toString(), payload.get("surname").toString(), "", "", payload.get("location").toString());
        usersRepository.save(newUser);

        String token = generateToken(email);
        jsonResponse.put("token", token);

        return ResponseEntity.ok(jsonResponse);
    }

    @GetMapping("/info")
    public HttpEntity<HashMap<String, Object>> userInfo(@RequestParam String email) {
        HashMap<String, Object> jsonResponse = new HashMap<>();

        Optional<User> user = usersRepository.findByEmail(email);
        if(!user.isPresent()) ResponseEntity.notFound().build();

        User returnUser = user.get();
        returnUser.setPassword(null);

        jsonResponse.put("user", returnUser);

        return ResponseEntity.ok(jsonResponse);
    }

    @PostMapping("/save")
    public HttpEntity<HashMap<String, Object>> saveInfo(@RequestBody Map<String, Object> payload) {
        HashMap<String, Object> jsonResponse = new HashMap<>();

        if(payload.isEmpty() || !payload.containsKey("email") || !payload.containsKey("firstname") || !payload.containsKey("lastname")) {
            jsonResponse.put("message", "Rellena los campos obligatorios para continuar");
            return ResponseEntity.badRequest().body(jsonResponse);
        }

        Optional<User> user = usersRepository.findByEmail(payload.get("email").toString());
        if(!user.isPresent()) {
            jsonResponse.put("message", "Ha ocurrido un error al actualizar tus datos");
            return ResponseEntity.badRequest().body(jsonResponse);
        }

        if(payload.get("firstname").equals("") || payload.get("lastname").equals("")) {
            jsonResponse.put("message", "Los campos \"Nombre\" y \"Apellido\" son obligatorios");
            return ResponseEntity.badRequest().body(jsonResponse);
        }

        if(!payload.get("firstname").equals(user.get().getFirstname()))
            user.get().setFirstname(payload.get("firstname").toString());
        if(!payload.get("lastname").equals(user.get().getLastname()))
            user.get().setLastname(payload.get("lastname").toString());
        if(!payload.get("phone").equals(user.get().getPhone()))
            user.get().setPhone(payload.get("phone").toString());
        if(!payload.get("location").equals(user.get().getLocation()))
            user.get().setLocation(payload.get("location").toString());
        if(!payload.get("biography").equals(user.get().getBiography()))
            user.get().setBiography(payload.get("biography").toString());

        usersRepository.save(user.get());
        jsonResponse.put("user", user.get());
        return ResponseEntity.ok(jsonResponse);
    }

    private String generateToken(String mail) {
        String secretKey = "mySecretKey";
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList("DEFAULT");

        String token = Jwts.builder().setId("LGFMTokenJWT").setSubject(mail)
                .claim("authorities", grantedAuthorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 172800000))
                .signWith(SignatureAlgorithm.HS512, secretKey.getBytes()).compact();
        return token;
    }
}
