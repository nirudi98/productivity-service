package com.example.developerIQ.productivityservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "commits")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommitEntity implements Serializable {

    @Id
    private String commit_id;

    @Column(nullable = false)
    private String author_name;

    @Column(nullable = false)
    private String committer_name;

    @Column(nullable = false)
    private String url;

    @Column
    private String message;

    @Column
    private LocalDateTime authored_date;

    @Column
    private LocalDateTime committed_date;
}
