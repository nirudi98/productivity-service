package com.example.developerIQ.productivityservice.service.impl;

import com.example.developerIQ.productivityservice.dto.issue.IssueInformation;
import com.example.developerIQ.productivityservice.dto.issue.IssueStatics;
import com.example.developerIQ.productivityservice.dto.issue.IssuesSprint;
import com.example.developerIQ.productivityservice.entity.IssueEntity;
import com.example.developerIQ.productivityservice.repository.IssueRepository;
import com.example.developerIQ.productivityservice.service.IssueInfoService;
import com.example.developerIQ.productivityservice.utils.IssueInfoHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.developerIQ.productivityservice.utils.constants.Constants.ISSUE_OPEN_TEXT;
import static com.example.developerIQ.productivityservice.utils.constants.Constants.ISSUE_OPEN_TEXT_BEFORE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IssueInfoServiceImplTest {

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private IssueInfoHelper issueInfoHelper;

    @InjectMocks
    private IssueInfoServiceImpl issueInfoService;

    @Test
    void testRetrieveIssuesSummary() {
        String user = "testUser";
        String start = "2023-01-01";
        String end = "2023-01-10";

        LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
        LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

        List<IssueEntity> issueList = new ArrayList<>();
        issueList.add(generateIssueEntityMock());

        when(issueRepository.findIssuesByNameDate(eq(user), eq(started)))
                .thenReturn(issueList);
        when(issueInfoHelper.formatIssueResponse(any(), eq(ended), eq(started)))
                .thenReturn(generateIssueInfo("open", true));

        IssuesSprint result = issueInfoService.retrieveIssuesSummary(user, start, end);

        // Assert
        assertNotNull(result);
        assertEquals("Issue is opened within sprint, should provide update",
                result.getIssueInformationList().get(0).getComment());
        assertEquals(50.00,
                result.getIssueInformationList().get(0).getProductivity_rate());
    }

    @Test
    void testRetrieveIssuesSummary_notWithinRange() {
        String user = "testUser";
        String start = "2023-01-01";
        String end = "2023-01-10";

        LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
        LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

        List<IssueEntity> issueList = new ArrayList<>();
        issueList.add(generateIssueEntityMock());

        when(issueRepository.findIssuesByNameDate(eq(user), eq(started)))
                .thenReturn(issueList);
        when(issueInfoHelper.formatIssueResponse(any(), eq(ended), eq(started)))
                .thenReturn(generateIssueInfo("open", false));

        IssuesSprint result = issueInfoService.retrieveIssuesSummary(user, start, end);

        // Assert
        assertNotNull(result);
        assertEquals("Issue is opened before sprint and should provide reasons why not resolved yet",
                result.getIssueInformationList().get(0).getComment());
        assertEquals(0.00,
                result.getIssueInformationList().get(0).getProductivity_rate());
    }

    @Test
    void testRetrieveIssuesSummary_closed() {
        String user = "testUser";
        String start = "2023-01-01";
        String end = "2023-01-10";

        LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
        LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

        List<IssueEntity> issueList = new ArrayList<>();
        issueList.add(generateIssueEntityMock());

        when(issueRepository.findIssuesByNameDate(eq(user), eq(started)))
                .thenReturn(issueList);
        when(issueInfoHelper.formatIssueResponse(any(), eq(ended), eq(started)))
                .thenReturn(generateIssueInfo("closed", true));

        IssuesSprint result = issueInfoService.retrieveIssuesSummary(user, start, end);

        // Assert
        assertNotNull(result);
        assertEquals(true,
                result.getIssueInformationList().get(0).getIsClosed());
        assertEquals(100.00,
                result.getIssueInformationList().get(0).getProductivity_rate());
    }

    private IssueInformation generateIssueInfo(String state, boolean isWithinRange) {
        IssueInformation issueInformation = new IssueInformation();

        issueInformation.setId(UUID.randomUUID().toString());
        issueInformation.setName("test user");
        issueInformation.setTitle("test title");
        issueInformation.setState(state);
        issueInformation.setOpen_since("2013-02-05 01:28:20.000000");
        if(Objects.equals(state, "open")){
            issueInformation.setIsClosed(false);
        } else {
            issueInformation.setIsClosed(true);
            issueInformation.setProductivity_rate(100.00);
        }


        if(isWithinRange){
            issueInformation.setComment("Issue is opened within sprint, should provide update");
            issueInformation.setProductivity_rate(50.00);
        } else {
            issueInformation.setComment("Issue is opened before sprint and should provide reasons why not resolved yet");
            issueInformation.setProductivity_rate(0.00);
        }
        return issueInformation;
    }

//    public IssueInformation formatIssueResponse(IssueEntity issue, LocalDateTime endDate, LocalDateTime startDate) {
//        IssueInformation issueInformation = new IssueInformation();
//
//        issueInformation.setId(issue.getIssue_id());
//        issueInformation.setName(issue.getUsername());
//        issueInformation.setTitle(issue.getTitle());
//        issueInformation.setState(issue.getState());
//        issueInformation.setOpen_since(issue.getCreated_date().toString());
//
//        // issue success rate
//        // check if the closed date is present and status is closed
//        if(Objects.equals(issue.getState(), "closed")) {
//            // if status is closed, closed date exists
//            logger.info("Issue with id {}, opened by {} is closed", issue.getIssue_id(), issue.getUsername());
//            issueInformation.setIsClosed(true);
//            issueInformation.setProductivity_rate(100.00);
//        } else {
//            logger.info("PR with id {}, opened by {} is still opened", issue.getIssue_id(), issue.getUsername());
//            issueInformation.setIsClosed(false);
//
//            String text = calculateIssueProductivityRate(endDate, startDate, issue.getCreated_date(), issue);
//            double success_rate = (text.contains(ISSUE_OPEN_TEXT)) ? 50.00 : 0.00;
//            String comment = (success_rate > 49.00) ? "Issue is opened within sprint, should provide update" :
//                    "Issue is opened before sprint and should provide reasons why not resolved yet";
//            issueInformation.setComment(comment);
//            issueInformation.setProductivity_rate(success_rate);
//        }
//        return issueInformation;
//
//    }

//    private String calculateIssueProductivityRate(LocalDateTime endDate, LocalDateTime startDate, LocalDateTime createdDate, IssueEntity issue) {
//        // Check if the date is within the specified range
//        boolean isWithinRange = isWithinRange(createdDate, startDate, endDate);
//        String text;
//        // Output the result
//        if (isWithinRange) {
//            logger.info("Issue is opened within sprint {}", issue.getIssue_id());
//            return ISSUE_OPEN_TEXT;
//        } else {
//            logger.info("Issue is opened before sprint {}", issue.getIssue_id());
//            return ISSUE_OPEN_TEXT_BEFORE;
//        }
//    }

    @Test
    void testRetrieveIssueStatics() {
        // Arrange
        String user = "testUser";
        String start = "2023-12-01";
        String end = "2023-01-10";

        LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
        LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

        List<IssueEntity> issueList = new ArrayList<>();
        issueList.add(generateIssueEntityMock());

        List<IssueEntity> closedList = new ArrayList<>();
        closedList.add(generateCloseIssueEntityMock());

        when(issueRepository.findIssuesByNameState(eq(user), eq("open")))
                .thenReturn(issueList);
        when(issueRepository.findIssuesByNameState(eq(user), eq("closed")))
                .thenReturn(closedList);

        // Mock the behavior of issueInfoHelper for open issues map
        when(issueInfoHelper.generateOpenIssueMap(anyList(), eq(started), eq(ended)))
                .thenReturn(generateOpenMap(issueList));

//        // Mock the behavior of issueRepository for closed issues
//        when(issueRepository.findIssuesByNameState(eq(user), eq("closed")))
//                .thenReturn(Collections.singletonList(/* your expected Closed IssueEntity */));
//
//        // Mock the behavior of issueInfoHelper for closed issues map
//        when(issueInfoHelper.generateClosedIssueMap(anyList(), eq(ended), eq(started)))
//                .thenReturn(/* your expected closed issues map */);
//
//        // Mock the behavior of issueInfoHelper for generateStatics
//        when(issueInfoHelper.generateStatics(anyInt(), anyInt(), anyInt(), anyDouble(), anyDouble(), anyDouble(), anyMap(), anyMap()))
//                .thenReturn(/* your expected IssueStatics */);

        // Act
        IssueStatics result = issueInfoService.retrieveIssueStatics(user, start, end);

        // Assert
        assertNotNull(result);
        // Add more assertions based on the expected behavior and result
    }


    private Map<String, Integer> generateOpenMap(List<IssueEntity> openIssues) {
        Map<String, Integer> openMap = new HashMap<>();
        openMap.put("issue opened within sprint with start date", 1);
        return openMap;
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

    private IssueEntity generateCloseIssueEntityMock() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 1, 12, 0);
        IssueEntity entity = new IssueEntity();
        entity.setIssue_id(UUID.randomUUID().toString());
        entity.setTitle("test title");
        entity.setState("closed");

        entity.setDescription("test desc");
        entity.setUsername("test");
        entity.setCreated_date(dateTime);
        entity.setClosed_date(dateTime);

        return entity;
    }

}