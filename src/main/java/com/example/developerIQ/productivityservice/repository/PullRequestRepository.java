package com.example.developerIQ.productivityservice.repository;

import com.example.developerIQ.productivityservice.entity.PullRequestEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PullRequestRepository extends MongoRepository<PullRequestEntity, String> {

//    @Query("SELECT e FROM PullRequestEntity e WHERE e.username = :name and e.created_date > :date")
    @Query("{ 'username' : ?0, 'created_date' : { $gt : ?1 } }")
    List<PullRequestEntity> findPullRequestByNameDate(String name, LocalDateTime date);

//    @Query("SELECT e FROM PullRequestEntity e WHERE e.username = :name")
    @Query("{ 'username' : ?0 }")
    List<PullRequestEntity> findPullRequestByName(String user);

//    @Query("SELECT e FROM PullRequestEntity e WHERE (e.created_date BETWEEN :start AND :end)")
    @Query("{'created_date': {$gte: ?0, $lte: ?1}}")
    List<PullRequestEntity> findPullRequestByDate(LocalDateTime start, LocalDateTime end);

}
