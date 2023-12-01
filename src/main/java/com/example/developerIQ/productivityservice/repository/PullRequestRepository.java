package com.example.developerIQ.productivityservice.repository;

import com.example.developerIQ.productivityservice.entity.PullRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PullRequestRepository extends JpaRepository<PullRequestEntity, Integer> {

    @Query("SELECT e FROM PullRequestEntity e WHERE e.username = :name and e.created_date > :date")
    List<PullRequestEntity> findPullRequestByNameDate(String name, LocalDateTime date);

    @Query("SELECT e FROM PullRequestEntity e WHERE e.username = :name")
    List<PullRequestEntity> findPullRequestByName(String name);

    @Query("SELECT e FROM PullRequestEntity e WHERE (e.created_date BETWEEN :start AND :end)")
    List<PullRequestEntity> findPullRequestByDate(LocalDateTime start, LocalDateTime end);

}
