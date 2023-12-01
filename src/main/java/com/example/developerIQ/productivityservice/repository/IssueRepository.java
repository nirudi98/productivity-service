package com.example.developerIQ.productivityservice.repository;

import com.example.developerIQ.productivityservice.entity.IssueEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface IssueRepository extends MongoRepository<IssueEntity, String> {

    // Custom JPQL query
    @Query("{ 'username' : ?0, 'created_date': {$gte: ?1} }")
//    @Query("SELECT e FROM IssueEntity e WHERE e.username = :name and e.created_date > :date")
    List<IssueEntity> findIssuesByNameDate(String name, LocalDateTime date);

    // open issues
    @Query("{ 'username' : ?0, 'state': ?1 }")
//    @Query("SELECT e FROM IssueEntity e WHERE e.username = :name and e.state = :state")
    List<IssueEntity> findIssuesByNameState(String name, String state);

    @Query("{ 'username' : ?0 }")
//    @Query("SELECT e FROM IssueEntity e WHERE e.username = :name")
    List<IssueEntity> findPullRequestByName(String name);

    @Query("{ 'created_date': {$gte: ?0, $lte: ?1}, 'state' : ?2 }")
//    @Query("SELECT e FROM IssueEntity e WHERE (e.created_date BETWEEN :start AND :end) AND e.state = :state")
    List<IssueEntity> findPullRequestByDate(LocalDateTime start, LocalDateTime end, String state);

}
