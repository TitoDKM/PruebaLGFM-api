package me.daboy.blog.api.repositories;

import me.daboy.blog.api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<User, Long> {
}
