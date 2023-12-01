package com.example.developerIQ.productivityservice.service.impl;

import com.example.developerIQ.productivityservice.dto.commit.CommitInformation;
import com.example.developerIQ.productivityservice.dto.commit.Commits;
import com.example.developerIQ.productivityservice.entity.CommitEntity;
import com.example.developerIQ.productivityservice.repository.CommitRepository;
import com.example.developerIQ.productivityservice.utils.CommitInfoHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommitInfoServiceImplTest {

    @Mock
    private CommitRepository commitRepository;

    @InjectMocks
    private CommitInfoServiceImpl commitsService;

    @Mock
    private CommitInfoHelper commitInfoHelper;

    @Test
    void testRetrieveCommitsSummary() {
        String user = "testUser";
        String start = "2023-01-01";
        String end = "2023-01-10";

        LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
        LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

        List<CommitEntity> inSprintList = new ArrayList<>();
        inSprintList.add(generateCommitEntity());

        List<CommitEntity> outSprintList = new ArrayList<>();
        outSprintList.add(generateCommitEntityNotInSprint());
        // Mock the behavior of fetchCommitsInSprint
        when(commitRepository.findBySprintDate(eq(user), eq(started), eq(ended)))
                .thenReturn(inSprintList);

        // Mock the behavior of fetchCommitsOutSprint
        when(commitRepository.findCommitsNotInSprintByDate(eq(user), eq(started), eq(ended)))
                .thenReturn(outSprintList);

        when(commitInfoHelper.formatCommitWithinSprintResponse(any(), any(), any()))
                .thenReturn(generateCommitInfo(start, end, true));

        // Act
        Commits result = commitsService.retrieveCommitsSummary(user, start, end);

        // Assert
        assertNotNull(result);
        assertEquals(2,result.getTotal_commits_overall());
    }

    @Test
    void testRetrieveCommitsSummary_inSprintEmpty() {
        String user = "testUser";
        String start = "2023-01-01";
        String end = "2023-01-10";

        LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
        LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

        List<CommitEntity> inSprintList = new ArrayList<>();
        List<CommitEntity> outSprintList = new ArrayList<>();
        outSprintList.add(generateCommitEntityNotInSprint());

        when(commitRepository.findBySprintDate(eq(user), eq(started), eq(ended)))
                .thenReturn(inSprintList);

        when(commitRepository.findCommitsNotInSprintByDate(eq(user), eq(started), eq(ended)))
                .thenReturn(outSprintList);

        when(commitInfoHelper.formatCommitNotWithinSprintResponse(any(), any(), any()))
                .thenReturn(generateCommitInfo(start, end, false));

        Commits result = commitsService.retrieveCommitsSummary(user, start, end);

        assertNotNull(result);
        assertEquals(1,result.getTotal_commits_overall());
    }

    @Test
    void testRetrieveCommitsSummary_outSprintEmpty() {
        String user = "testUser";
        String start = "2023-01-01";
        String end = "2023-01-10";

        LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
        LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

        List<CommitEntity> inSprintList = new ArrayList<>();
        inSprintList.add(generateCommitEntity());
        List<CommitEntity> outSprintList = new ArrayList<>();
        // Mock the behavior of fetchCommitsInSprint
        when(commitRepository.findBySprintDate(eq(user), eq(started), eq(ended)))
                .thenReturn(inSprintList);

        // Mock the behavior of fetchCommitsOutSprint
        when(commitRepository.findCommitsNotInSprintByDate(eq(user), eq(started), eq(ended)))
                .thenReturn(outSprintList);

        when(commitInfoHelper.formatCommitWithinSprintResponse(any(), any(), any()))
                .thenReturn(generateCommitInfo(start, end, true));

        // Act
        Commits result = commitsService.retrieveCommitsSummary(user, start, end);

        // Assert
        assertNotNull(result);
        assertEquals(1,result.getTotal_commits_overall());
    }

    @Test
    void testRetrieveCommitsSummary_bothEmpty() {
        String user = "testUser";
        String start = "2023-01-01";
        String end = "2023-01-10";

        LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
        LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

        List<CommitEntity> inSprintList = new ArrayList<>();
        List<CommitEntity> outSprintList = new ArrayList<>();

        when(commitRepository.findBySprintDate(eq(user), eq(started), eq(ended)))
                .thenReturn(inSprintList);
        when(commitRepository.findCommitsNotInSprintByDate(eq(user), eq(started), eq(ended)))
                .thenReturn(outSprintList);

        Commits result = commitsService.retrieveCommitsSummary(user, start, end);

        assertNotNull(result);
        assertEquals(0,result.getTotal_commits_overall());
    }

    @Test
    void testRetrieveCommitsSummaryWithException() {
        String user = "testUser";
        String start = "2023-01-01";
        String end = "2023-01-10";

        LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
        LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

        when(commitRepository.findBySprintDate(eq(user), eq(started), eq(ended)))
                .thenThrow(new RuntimeException("Mocked exception"));

        Commits result = commitsService.retrieveCommitsSummary(user, start, end);

        assertNotNull(result);
        assertEquals(0,result.getTotal_commits_overall());
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

    private CommitEntity generateCommitEntityNotInSprint() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 9, 1, 12, 0);
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

    private CommitInformation generateCommitInfo(String startDate, String endDate, boolean within) {
        CommitInformation commitInformation = new CommitInformation();
        String text = "Sprint " + startDate + " - " + endDate;
        Map<String, Boolean> withinSprintMap = new HashMap<>();

        commitInformation.setId(UUID.randomUUID().toString());
        commitInformation.setName("test");
        commitInformation.setMessage("commit msg");
        commitInformation.setCommitted_date("2023-10-18");
        withinSprintMap.put(text, within);

        commitInformation.setIs_committed_in_sprint(withinSprintMap);
        return commitInformation;
    }

}