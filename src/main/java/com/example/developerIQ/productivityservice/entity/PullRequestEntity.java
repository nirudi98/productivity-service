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
@Table(name = "pull_requests")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PullRequestEntity implements Serializable {

    @Id
    private String id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String username;

    @Column
    private String title;

    @Column(nullable = false)
    private String pull_number;

    @Column(nullable = false)
    private Boolean is_merged;

    @Column(nullable = false)
    private String user_type;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime created_date;

    @Column
    private LocalDateTime closed_date;

    @Column
    private LocalDateTime merged_date;
}
