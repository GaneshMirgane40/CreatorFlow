package com.ganesh.creatorflow.repository;

import com.ganesh.creatorflow.entity.User;
import com.ganesh.creatorflow.entity.YouTubeAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface YouTubeAccountRepository
        extends JpaRepository<YouTubeAccount, Long> {

    Optional<YouTubeAccount> findByCreator(User creator);

    Optional<YouTubeAccount> findByCreatorEmail(String email);

    boolean existsByCreator(User creator);

    void deleteByCreator(User creator);
}