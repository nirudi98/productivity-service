package com.example.developerIQ.productivityservice.service.impl;

import com.example.developerIQ.productivityservice.dto.pr.PRInformation;
import com.example.developerIQ.productivityservice.dto.pr.PRStatics;
import com.example.developerIQ.productivityservice.dto.pr.PullRequests;
import com.example.developerIQ.productivityservice.entity.PullRequestEntity;
import com.example.developerIQ.productivityservice.repository.CommitRepository;
import com.example.developerIQ.productivityservice.repository.PullRequestRepository;
import com.example.developerIQ.productivityservice.utils.CommitInfoHelper;
import com.example.developerIQ.productivityservice.utils.PullRequestInfoHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.developerIQ.productivityservice.utils.constants.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PullRequestInfoServiceImplTest {

    @Mock
    private PullRequestRepository pullRequestRepository;

    @InjectMocks
    private PullRequestInfoServiceImpl pullRequestInfoService;

    @Mock
    private PullRequestInfoHelper pullRequestInfoHelper;

    @Test
    void retrievePullRequestSummary() {
        String user = "testUser";
        String start = "2023-01-01";
        String end = "2023-01-10";

        LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
        LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

        List<PullRequestEntity> prList = new ArrayList<>();
        prList.add(generatePREntityMock());

        when(pullRequestRepository.findPullRequestByNameDate(eq(user), eq(started)))
                .thenReturn(prList);

        when(pullRequestInfoHelper.formatPRResponse(any(), eq(ended), eq(started)))
                .thenReturn(generateMockPRInfo_merged());

        // Act
        PullRequests result = pullRequestInfoService.retrievePullRequestSummary(user, start, end);

        // Assert
        assertNotNull(result);
        assertEquals("100.0", result.getPullRequestList().get(0).getProductivity_rate().toString());
    }

    @Test
    void retrievePullRequestSummary_notMerged() {
        String user = "testUser";
        String start = "2023-01-01";
        String end = "2023-01-10";

        LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
        LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

        List<PullRequestEntity> prList = new ArrayList<>();
        prList.add(generatePREntityMock());

        when(pullRequestRepository.findPullRequestByNameDate(eq(user), eq(started)))
                .thenReturn(prList);

        when(pullRequestInfoHelper.formatPRResponse(any(), eq(ended), eq(started)))
                .thenReturn(generateMockPRInfo_notMerged(true));

        // Act
        PullRequests result = pullRequestInfoService.retrievePullRequestSummary(user, start, end);

        // Assert
        assertNotNull(result);
        assertEquals("50.0", result.getPullRequestList().get(0).getProductivity_rate().toString());
        assertEquals("PR is opened within sprint, should provide update",
                result.getPullRequestList().get(0).getComment());
    }

    @Test
    void retrievePullRequestSummary_notMergedNotWithinRange() {
        String user = "testUser";
        String start = "2023-01-01";
        String end = "2023-01-10";

        LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
        LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

        List<PullRequestEntity> prList = new ArrayList<>();
        prList.add(generatePREntityMock());

        when(pullRequestRepository.findPullRequestByNameDate(eq(user), eq(started)))
                .thenReturn(prList);

        when(pullRequestInfoHelper.formatPRResponse(any(), eq(ended), eq(started)))
                .thenReturn(generateMockPRInfo_notMerged(false));

        // Act
        PullRequests result = pullRequestInfoService.retrievePullRequestSummary(user, start, end);

        // Assert
        assertNotNull(result);
        assertEquals("0.0", result.getPullRequestList().get(0).getProductivity_rate().toString());
        assertEquals("PR is opened before sprint and should provide reasons",
                result.getPullRequestList().get(0).getComment());
    }

    @Test
    void retrievePullRequestStatics() {
        String user = "testUser";
        String start = "2023-01-01";
        String end = "2023-01-10";

        LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
        LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

        List<PullRequestEntity> prList = new ArrayList<>();
        prList.add(generatePREntityMock());

        when(pullRequestRepository.findPullRequestByNameDate(eq(user), eq(started)))
                .thenReturn(prList);

        when(pullRequestInfoHelper.generateStatics(anyInt(), anyInt(), anyInt(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(generateStatics(0.0));

        PRStatics result = pullRequestInfoService.retrievePullRequestStatics(user, start, end);

        assertNotNull(result);
        assertEquals(1,result.getAll_pr_count());
    }

    private PRStatics generateStatics(double ratio) {
        Map<String, Double> stat = new HashMap<>();
        stat.put(RATIO, ratio);

        PRStatics prStatics = new PRStatics();
        prStatics.setOpened_pr_count(1);
        prStatics.setClosed_pr_count(0);
        prStatics.setAll_pr_count(1);
        prStatics.setRatio(stat);

        prStatics.setOpened_as_percentage(100.00);
        prStatics.setClosed_as_percentage(0.0);
        return prStatics;
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

    private PRInformation generateMockPRInfo_merged() {
        PRInformation prInformation = new PRInformation();

        prInformation.setId(UUID.randomUUID().toString());
        prInformation.setName("test user");
        prInformation.setUrl("test_pr@gmail.com");
        prInformation.setState("open");
        prInformation.setOpen_since("2013-02-05 01:28:20.000000");
        prInformation.setMerged(true);
        prInformation.setProductivity_rate(100.00);

        return prInformation;
    }

    private PRInformation generateMockPRInfo_notMerged(boolean within) {
        PRInformation prInformation = new PRInformation();

        prInformation.setId(UUID.randomUUID().toString());
        prInformation.setName("test user");
        prInformation.setUrl("test_pr@gmail.com");
        prInformation.setState("open");
        prInformation.setOpen_since("2013-02-05 01:28:20.000000");
        prInformation.setMerged(false);


        // if within the range
        if(within){
            prInformation.setComment("PR is opened within sprint, should provide update");
            prInformation.setProductivity_rate(50.00);
        } else {
            prInformation.setComment("PR is opened before sprint and should provide reasons");
            prInformation.setProductivity_rate(0.00);
        }

        return prInformation;

    }
}