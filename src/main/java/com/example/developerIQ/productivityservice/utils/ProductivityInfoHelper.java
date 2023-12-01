package com.example.developerIQ.productivityservice.utils;

import com.example.developerIQ.productivityservice.dto.pr.PRInformation;
import com.example.developerIQ.productivityservice.dto.pr.PRStatics;
import com.example.developerIQ.productivityservice.dto.productivity.OverallCommits;
import com.example.developerIQ.productivityservice.dto.productivity.OverallIssues;
import com.example.developerIQ.productivityservice.dto.productivity.OverallPR;
import com.example.developerIQ.productivityservice.entity.CommitEntity;
import com.example.developerIQ.productivityservice.entity.IssueEntity;
import com.example.developerIQ.productivityservice.entity.PullRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.developerIQ.productivityservice.utils.constants.Constants.*;

@Service
public class ProductivityInfoHelper {
    private static final Logger logger = LoggerFactory.getLogger(ProductivityInfoHelper.class);

    public OverallPR formatOverallPRResponse(List<PullRequestEntity> entityList, LocalDateTime start, LocalDateTime end) {
        OverallPR overallPR = new OverallPR();
        int total_opened_within_sprint = 0;
        int total_opened_out_of_sprint = 0;
        int total_opened_closed_within_sprint = 0;
        int total_opened = 0;
        int total_closed = 0;

        for(PullRequestEntity pr: entityList) {
            if(!pr.getCreated_date().isBefore(start) && pr.getCreated_date().isBefore(end.plusSeconds(1))) {
                total_opened_within_sprint++;
                if(pr.getIs_merged()){
                    total_opened_closed_within_sprint++;
                }
            } else {
                total_opened_out_of_sprint++;
            }
        }

        total_opened = (int) entityList.stream().filter(entity -> "open".equalsIgnoreCase(entity.getStatus())).count();
        total_closed = (int) entityList.stream().filter(entity -> entity.getClosed_date() != null ||
                entity.getMerged_date() != null || entity.getIs_merged()).count();


        overallPR.setTotal_closed_PR(total_closed);
        overallPR.setTotal_opened_PR(total_opened);
        overallPR.setTotal_opened_PR_within_sprint(total_opened_within_sprint);
        overallPR.setTotal_closed_PR_within_sprint(total_opened_closed_within_sprint);
        overallPR.setTotal_opened_out_of_sprint(total_opened_out_of_sprint);

        return overallPR;

    }

    public OverallIssues formatOverallIssueResponse(List<IssueEntity> entityList, LocalDateTime start, LocalDateTime end) {
        OverallIssues overallIssues = new OverallIssues();

        int total_issues_opened = 0;
        int total_issues_closed = 0;
        int total_issues_opened_within_sprint = 0;
        int total_issues_opened_closed_within_sprint = 0;
        int total_issues_opened_out_sprint = 0;

        for(IssueEntity issue: entityList) {
            if(!issue.getCreated_date().isBefore(start) && issue.getCreated_date().isBefore(end.plusSeconds(1))) {
                total_issues_opened_within_sprint++;
                if(Objects.equals(issue.getState(), "closed") && issue.getClosed_date().isBefore(end.plusSeconds(1))){
                    total_issues_opened_closed_within_sprint++;
                }
            } else {
                total_issues_opened_out_sprint++;
            }
        }

        total_issues_opened = (int) entityList.stream().filter(entity -> "open".equalsIgnoreCase(entity.getState())).count();
        total_issues_closed = (int) entityList.stream().filter(entity -> entity.getClosed_date() != null ||
                Objects.equals(entity.getState(), "closed")).count();


        overallIssues.setTotal_issues_opened(total_issues_opened);
        overallIssues.setTotal_issues_closed(total_issues_closed);
        overallIssues.setTotal_issues_opened_within_sprint(total_issues_opened_within_sprint);
        overallIssues.setTotal_issues_closed_within_sprint(total_issues_opened_closed_within_sprint);
        overallIssues.setTotal_issues_opened_out_sprint(total_issues_opened_out_sprint);

        return overallIssues;

    }

    public OverallCommits formatOverallCommitResponse(List<CommitEntity> entityList, LocalDateTime start, LocalDateTime end) {
        OverallCommits overallCommits = new OverallCommits();
        List<CommitEntity> within_sprint_commits = new ArrayList<>();

        int total_commits_opened = 0;
        int total_commits_opened_within_sprint = 0;
        int total_commits_opened_out_sprint = 0;
        double commit_rate_within_sprint = 0.0;

        for(CommitEntity commit: entityList) {
            if(!commit.getCommitted_date().isBefore(start) && commit.getCommitted_date().isBefore(end.plusSeconds(1))) {
                total_commits_opened_within_sprint++;
                within_sprint_commits.add(commit);
            } else {
                total_commits_opened_out_sprint++;
            }
        }

        total_commits_opened = entityList.size();
        double commitRate = calculateCommitRate(within_sprint_commits, start, end);


        overallCommits.setTotal_commits_opened(total_commits_opened);
        overallCommits.setTotal_commits_opened_within_sprint(total_commits_opened_within_sprint);
        overallCommits.setTotal_commits_opened_out_sprint(total_commits_opened_out_sprint);
        overallCommits.setCommit_rate_within_sprint(commitRate);

        return overallCommits;

    }

    public double calculateCommitRate(List<CommitEntity> commits, LocalDateTime sprintStartDate, LocalDateTime sprintEndDate) {
        long totalCommits = commits.size();
        long sprintDurationInDays = calculateDurationInDays(sprintStartDate, sprintEndDate);

        // Calculate the commit rate (commits per day)
        return totalCommits / (double) sprintDurationInDays;
    }

    private long calculateDurationInDays(LocalDateTime startDate, LocalDateTime endDate) {
        // Assuming the input dates are valid and endDate is after startDate
        return java.time.Duration.between(startDate, endDate).toDays();
    }
}
