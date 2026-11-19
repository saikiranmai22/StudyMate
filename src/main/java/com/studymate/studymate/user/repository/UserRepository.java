package com.studymate.studymate.user.repository;

import com.studymate.studymate.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameIgnoreCase(String username);
    //findBy, Username, IgnoreCase

    Optional<User> findByEmailIgnoreCase(String email);
    //Because the username might not exist.
    //Instead of returning null, Java returns an Optional that can either contain a User or be empty.
    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);
}
