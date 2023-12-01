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
@Table(name = "issues")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class IssueEntity implements Serializable {

    @Id
    private String issue_id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String username;

    @Column
    private String description;

    @Column
    private LocalDateTime created_date;

    @Column
    private LocalDateTime closed_date;
}
