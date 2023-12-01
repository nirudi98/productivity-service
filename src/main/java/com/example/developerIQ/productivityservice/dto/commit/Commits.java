package com.example.developerIQ.productivityservice.dto.commit;

import com.example.developerIQ.productivityservice.dto.issue.IssueInformation;
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
public class Commits {

    private List<CommitInformation> commitInformtionList;
    private int total_commits_within_sprint;
    private int total_commits_overall;
}
