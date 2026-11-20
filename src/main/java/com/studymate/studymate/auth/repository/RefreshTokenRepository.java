package com.studymate.studymate.auth.repository;

import com.studymate.studymate.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);
    //Find a refresh token using its hash.

    List<RefreshToken> findAllByUser_Id(Long userId);
    //Retrieve all refresh tokens belonging to a student.

    void deleteAllByUser_Id(Long userId);
    //Delete all of a student's refresh-token records when appropriate.

}
