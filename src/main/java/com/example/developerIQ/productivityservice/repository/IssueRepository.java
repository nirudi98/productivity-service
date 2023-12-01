package com.example.developerIQ.productivityservice.repository;

import com.example.developerIQ.productivityservice.entity.IssueEntity;
import com.example.developerIQ.productivityservice.entity.PullRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface IssueRepository extends JpaRepository<IssueEntity, Integer> {

    // Custom JPQL query
    @Query("SELECT e FROM IssueEntity e WHERE e.username = :name and e.created_date > :date")
    List<IssueEntity> findIssuesByNameDate(String name, LocalDateTime date);

    // open issues
    @Query("SELECT e FROM IssueEntity e WHERE e.username = :name and e.state = :state")
    List<IssueEntity> findIssuesByNameState(String name, String state);

    @Query("SELECT e FROM IssueEntity e WHERE e.username = :name")
    List<IssueEntity> findPullRequestByName(String name);

    @Query("SELECT e FROM IssueEntity e WHERE (e.created_date BETWEEN :start AND :end) AND e.state = :state")
    List<IssueEntity> findPullRequestByDate(LocalDateTime start, LocalDateTime end, String state);

}
