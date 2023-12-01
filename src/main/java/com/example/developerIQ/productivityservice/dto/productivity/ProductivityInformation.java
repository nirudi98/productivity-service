package com.example.developerIQ.productivityservice.dto.productivity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductivityInformation {

    private OverallCommits commitsList;
    private OverallPR prList;
    private OverallIssues issuesList;
}
