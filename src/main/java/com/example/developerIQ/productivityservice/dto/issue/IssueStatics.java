package com.example.developerIQ.productivityservice.dto.issue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueStatics {

    private int all_issue_count;

    private int open_issues_count;
    private double opened_as_percentage;
    private Map<String, Integer> open_issue_period;

    private int closed_issues_count;
    private double closed_as_percentage;
    private Map<String, Integer> closed_issue_period;

    private Map<String, Double> ratio;


}
