package com.example.developerIQ.productivityservice.service.impl;

import com.example.developerIQ.productivityservice.dto.productivity.*;
import com.example.developerIQ.productivityservice.entity.CommitEntity;
import com.example.developerIQ.productivityservice.entity.IssueEntity;
import com.example.developerIQ.productivityservice.entity.PullRequestEntity;
import com.example.developerIQ.productivityservice.repository.CommitRepository;
import com.example.developerIQ.productivityservice.repository.IssueRepository;
import com.example.developerIQ.productivityservice.repository.PullRequestRepository;
import com.example.developerIQ.productivityservice.utils.ProductivityInfoHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductivityServiceImplTest {

    @Mock
    private PullRequestRepository pullRequestRepository;

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private CommitRepository commitRepository;

    @Mock
    private ProductivityInfoHelper productivityInfoHelper;

    @InjectMocks
    private ProductivityServiceImpl productivityInfoService;

    @Test
    void testRetrieveOverview() {
        String username = "testUser";
        String start = "2023-01-01";
        String end = "2023-01-10";

        LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
        LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

        List<PullRequestEntity> prList = new ArrayList<>();
        prList.add(generatePREntityMock());

        List<CommitEntity> commitList = new ArrayList<>();
        commitList.add(generateCommitEntity());

        List<IssueEntity> issueList = new ArrayList<>();
        issueList.add(generateIssueEntityMock());

        when(pullRequestRepository.findPullRequestByName(eq(username)))
                .thenReturn(prList);

        when(issueRepository.findIssuesByName(eq(username)))
                .thenReturn(issueList);

        when(commitRepository.findCommitByName(eq(username)))
                .thenReturn(commitList);

        when(productivityInfoHelper.formatOverallPRResponse(anyList(), eq(started), eq(ended)))
                .thenReturn(generatePRResponse());

        when(productivityInfoHelper.formatOverallIssueResponse(anyList(), eq(started), eq(ended)))
                .thenReturn(generateIssueResponse());

        when(productivityInfoHelper.formatOverallCommitResponse(anyList(), eq(started), eq(ended)))
                .thenReturn(generateCommitResponse());

        Productivity result = productivityInfoService.retrieveOverview(username, start, end);

        assertNotNull(result);
        assertEquals(5, result.getProductivity().getIssuesList().getTotal_issues_closed());
        assertEquals(2, result.getProductivity().getPrList().getTotal_closed_PR());
        assertEquals(20.00, result.getProductivity().getCommitsList().getCommit_rate_within_sprint());
    }

    @Test
    void testPreviousProductivity() {
        String start = "2023-01-01";
        String end = "2023-01-10";

        LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
        LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

        List<PullRequestEntity> prList = new ArrayList<>();
        prList.add(generatePREntityMock());

        List<CommitEntity> commitList = new ArrayList<>();
        commitList.add(generateCommitEntity());

        List<IssueEntity> issueList = new ArrayList<>();
        issueList.add(generateIssueEntityMock());

        when(pullRequestRepository.findPullRequestByDate(eq(started), eq(ended)))
                .thenReturn(prList);

        when(issueRepository.findIssuesByDate(eq(started), eq(ended), eq("open")))
                .thenReturn(issueList);

        when(issueRepository.findIssuesByDate(eq(started), eq(ended), eq("closed")))
                .thenReturn(issueList);

        when(commitRepository.findCommitByDate(eq(started), eq(ended)))
                .thenReturn(commitList);

        PreviousProductivity result = productivityInfoService.previousProductivity(start, end);

        assertNotNull(result);
    }

    public OverallPR generatePRResponse() {
        OverallPR overallPR = new OverallPR();

        overallPR.setTotal_closed_PR(2);
        overallPR.setTotal_opened_PR(5);
        overallPR.setTotal_opened_PR_within_sprint(3);
        overallPR.setTotal_closed_PR_within_sprint(1);
        overallPR.setTotal_opened_out_of_sprint(1);

        return overallPR;

    }

    public OverallIssues generateIssueResponse() {
        OverallIssues overallIssues = new OverallIssues();

        overallIssues.setTotal_issues_opened(4);
        overallIssues.setTotal_issues_closed(5);
        overallIssues.setTotal_issues_opened_within_sprint(2);
        overallIssues.setTotal_issues_closed_within_sprint(1);
        overallIssues.setTotal_issues_opened_out_sprint(2);

        return overallIssues;

    }

    public OverallCommits generateCommitResponse() {
        OverallCommits overallCommits = new OverallCommits();

        overallCommits.setTotal_commits_opened(16);
        overallCommits.setTotal_commits_opened_within_sprint(10);
        overallCommits.setTotal_commits_opened_out_sprint(6);
        overallCommits.setCommit_rate_within_sprint(20.00);

        return overallCommits;

    }


    private PullRequestEntity generatePREntityMock() {
        String created_date = "2023-11-01";
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 1, 12, 0);
        PullRequestEntity pr = new PullRequestEntity();
        pr.setId(UUID.randomUUID().toString());
        pr.setUrl("test.com");
        pr.setMerged_date(null);
        pr.setClosed_date(null);
        pr.setCreated_date(dateTime);
        pr.setUsername("test user");
        pr.setTitle("test pr");
        pr.setIs_merged(false);

        return pr;
    }

    private CommitEntity generateCommitEntity() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 10, 1, 12, 0);
        CommitEntity entity = new CommitEntity();
        entity.setCommit_id(UUID.randomUUID().toString());
        entity.setAuthor_name("test name");
        entity.setAuthored_date(dateTime);

        entity.setCommitter_name("test one");
        entity.setCommitted_date(dateTime);
        entity.setUrl("test.com");
        entity.setMessage("test msg");

        return entity;
    }

    private IssueEntity generateIssueEntityMock() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 2, 0, 0);
        IssueEntity entity = new IssueEntity();
        entity.setIssue_id(UUID.randomUUID().toString());
        entity.setTitle("test title");
        entity.setState("open");

        entity.setDescription("test desc");
        entity.setUsername("test");
        entity.setCreated_date(dateTime);
        entity.setClosed_date(dateTime);

        return entity;
    }

}