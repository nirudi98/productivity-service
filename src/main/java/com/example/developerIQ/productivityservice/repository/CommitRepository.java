package com.example.developerIQ.productivityservice.repository;

import com.example.developerIQ.productivityservice.entity.CommitEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface CommitRepository extends MongoRepository<CommitEntity, String> {

    @Query("{ 'committer_name' : ?0, 'committed_date': {$gte: ?1, $lte: ?2} }")
//    @Query("SELECT e FROM CommitEntity e WHERE e.committer_name = :name and (e.committed_date BETWEEN :start AND :end)")
    List<CommitEntity> findBySprintDate(String name, LocalDateTime start, LocalDateTime end);

    @Query("{ 'committer_name' : ?0, 'committed_date': {$lte: ?1, $gte: ?2} }")
//    @Query("SELECT e FROM CommitEntity e WHERE e.committer_name = :name and (e.committed_date NOT BETWEEN :start AND :end)")
    List<CommitEntity> findCommitsNotInSprintByDate(String name, LocalDateTime start, LocalDateTime end);

    @Query("{ 'committer_name' : ?0 }")
//    @Query("SELECT e FROM CommitEntity e WHERE e.committer_name = :name")
    List<CommitEntity> findPullRequestByName(String name);

    @Query("{ 'committed_date': {$gte: ?0, $lte: ?1} }")
//    @Query("SELECT e FROM CommitEntity e WHERE (e.committed_date BETWEEN :start AND :end)")
    List<CommitEntity> findPullRequestByDate(LocalDateTime start, LocalDateTime end);

}
