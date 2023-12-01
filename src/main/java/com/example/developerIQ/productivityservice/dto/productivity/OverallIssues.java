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
public class OverallIssues {

    private int total_issues_opened;
    private int total_issues_closed;
    private int total_issues_opened_within_sprint;
    private int total_issues_closed_within_sprint;
    private int total_issues_opened_out_sprint;
}
