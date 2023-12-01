package com.example.developerIQ.productivityservice.dto.productivity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OverallCommits {

    private int total_commits_opened;
    private int total_commits_opened_within_sprint;
    private int total_commits_opened_out_sprint;
    private double commit_rate_within_sprint;
}
