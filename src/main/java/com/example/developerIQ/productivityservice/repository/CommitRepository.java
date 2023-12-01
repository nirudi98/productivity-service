package com.example.developerIQ.productivityservice.repository;

import com.example.developerIQ.productivityservice.entity.CommitEntity;
import com.example.developerIQ.productivityservice.entity.IssueEntity;
import com.example.developerIQ.productivityservice.entity.PullRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface CommitRepository extends JpaRepository<CommitEntity, Integer> {

    @Query("SELECT e FROM CommitEntity e WHERE e.committer_name = :name and (e.committed_date BETWEEN :start AND :end)")
    List<CommitEntity> findBySprintDate(String name, LocalDateTime start, LocalDateTime end);

    @Query("SELECT e FROM CommitEntity e WHERE e.committer_name = :name and (e.committed_date NOT BETWEEN :start AND :end)")
    List<CommitEntity> findCommitsNotInSprintByDate(String name, LocalDateTime start, LocalDateTime end);

    @Query("SELECT e FROM CommitEntity e WHERE e.committer_name = :name")
    List<CommitEntity> findPullRequestByName(String name);

    @Query("SELECT e FROM CommitEntity e WHERE (e.committed_date BETWEEN :start AND :end)")
    List<CommitEntity> findPullRequestByDate(LocalDateTime start, LocalDateTime end);

}
