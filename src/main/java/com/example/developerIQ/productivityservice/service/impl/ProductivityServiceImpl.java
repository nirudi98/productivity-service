package com.example.developerIQ.productivityservice.service.impl;

import com.example.developerIQ.productivityservice.dto.productivity.*;
import com.example.developerIQ.productivityservice.entity.CommitEntity;
import com.example.developerIQ.productivityservice.entity.IssueEntity;
import com.example.developerIQ.productivityservice.entity.PullRequestEntity;
import com.example.developerIQ.productivityservice.repository.CommitRepository;
import com.example.developerIQ.productivityservice.repository.IssueRepository;
import com.example.developerIQ.productivityservice.repository.PullRequestRepository;
import com.example.developerIQ.productivityservice.service.ProductivityService;
import com.example.developerIQ.productivityservice.utils.ProductivityInfoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ProductivityServiceImpl implements ProductivityService {

    private static final Logger logger = LoggerFactory.getLogger(ProductivityServiceImpl.class);

    @Autowired
    private ProductivityInfoHelper productivityInfoHelper;
    @Autowired
    private PullRequestRepository pullRequestRepository;

    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Productivity retrieveOverview(String username, String start, String end) {
        try{
            Productivity productivity = new Productivity();
            ProductivityInformation productivityInformation = new ProductivityInformation();

            LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
            LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

            List<PullRequestEntity> prList = pullRequestRepository.findPullRequestByName(username);
            List<IssueEntity> issueList = issueRepository.findIssuesByName(username);
            List<CommitEntity> commitList = commitRepository.findCommitByName(username);

            if(prList == null || prList.isEmpty()) {
                logger.error("PR Overall info not found for this particular user {}",username);
            }

            OverallPR info = productivityInfoHelper.formatOverallPRResponse(prList, started, ended);
            productivityInformation.setPrList(info);


            if(issueList == null || issueList.isEmpty()) {
                logger.error("Issue Overall info not found for this particular user {}",username);
            }

            OverallIssues issue_info = productivityInfoHelper.formatOverallIssueResponse(issueList, started, ended);
            productivityInformation.setIssuesList(issue_info);

            if(commitList == null || commitList.isEmpty()) {
                logger.error("Commit Overall info not found for this particular user {}",username);
            }

            OverallCommits commit_info = productivityInfoHelper.formatOverallCommitResponse(commitList, started, ended);
            productivityInformation.setCommitsList(commit_info);

            productivity.setProductivity(productivityInformation);
            logger.info("Retrieved overall info for this particular user {} is found",username);
            return productivity;

        } catch(NullPointerException e) {
            Productivity productivity = new Productivity();
            logger.error("unable to retrieve overview for this user {}",username);
            return productivity;
        }
    }

    @Override
    public PreviousProductivity previousProductivity(String start, String end) {
        LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
        LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

        // fetch all PR opened within last sprint
        List<PullRequestEntity> previousPR = pullRequestRepository.findPullRequestByDate(started, ended);
        int previous_pr_count = (previousPR == null || previousPR.isEmpty()) ? 0 : previousPR.size();

        // fetch all issues opened within last sprint
        List<IssueEntity> previousOpenIssues = issueRepository.findIssuesByDate(started, ended, "open");
        int previous_open_issue_count = (previousOpenIssues == null || previousOpenIssues.isEmpty()) ?
                0 : previousOpenIssues.size();

        // fetch all issues closed within last sprint
        List<IssueEntity> previousClosedIssues = issueRepository.findIssuesByDate(started, ended, "closed");
        int previous_closed_issue_count = (previousClosedIssues == null || previousClosedIssues.isEmpty()) ?
                0 : previousClosedIssues.size();

        // fetch all PR opened within last sprint
        List<CommitEntity> previousCommit = commitRepository.findCommitByDate(started, ended);
        int previous_commit_count = (previousCommit == null || previousCommit.isEmpty()) ?
                0 : previousCommit.size();

        PreviousProductivity previousProductivity = new PreviousProductivity();
        previousProductivity.setPrevious_pr_count(previous_pr_count);
        previousProductivity.setPrevious_commit_count(previous_commit_count);
        previousProductivity.setPrevious_open_issue_count(previous_open_issue_count);
        previousProductivity.setPrevious_closed_issue_count(previous_closed_issue_count);

        return previousProductivity;
    }

}
