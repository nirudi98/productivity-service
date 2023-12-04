package com.example.developerIQ.productivityservice.service.impl;

import com.example.developerIQ.productivityservice.dto.issue.IssueInformation;
import com.example.developerIQ.productivityservice.dto.issue.IssueStatics;
import com.example.developerIQ.productivityservice.dto.issue.IssuesSprint;
import com.example.developerIQ.productivityservice.entity.IssueEntity;
import com.example.developerIQ.productivityservice.repository.IssueRepository;
import com.example.developerIQ.productivityservice.utils.IssueInfoHelper;
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
        assertEquals(50.00,
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

    @Test
    void testRetrieveIssueStatics() {
        String user = "testUser";
        String start = "2023-12-01";
        String end = "2023-12-10";

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

        when(issueInfoHelper.generateOpenIssueMap(anyList(), eq(started), eq(ended)))
                .thenReturn(generateOpenMap("before start and end of sprint"));

        when(issueInfoHelper.generateClosedIssueMap(anyList(), eq(ended), eq(started)))
                .thenReturn(generateCloseMap("opened and closed within"));

        when(issueInfoHelper.generateStatics(anyInt(), anyInt(), anyInt(), anyDouble(), anyDouble(), anyDouble(), anyMap(), anyMap()))
                .thenReturn(generateStatics());

        IssueStatics result = issueInfoService.retrieveIssueStatics(user, start, end);

        assertNotNull(result);
        assertEquals(2, result.getAll_issue_count());
        assertEquals(50.00, result.getClosed_as_percentage());
    }

    @Test
    void testRetrieveIssueStatics_differentCreatedDates() {
        String user = "testUser";
        String start = "2023-12-01";
        String end = "2023-12-10";

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

        when(issueInfoHelper.generateOpenIssueMap(anyList(), eq(started), eq(ended)))
                .thenReturn(generateOpenMap("in between"));

        when(issueInfoHelper.generateClosedIssueMap(anyList(), eq(ended), eq(started)))
                .thenReturn(generateCloseMap("closed after"));

        when(issueInfoHelper.generateStatics(anyInt(), anyInt(), anyInt(), anyDouble(), anyDouble(), anyDouble(), anyMap(), anyMap()))
                .thenReturn(generateStatics());

        IssueStatics result = issueInfoService.retrieveIssueStatics(user, start, end);

        assertNotNull(result);
        assertEquals(2, result.getAll_issue_count());
        assertEquals(50.00, result.getClosed_as_percentage());
    }


    private Map<String, Integer> generateOpenMap(String text) {
        Map<String, Integer> openMap = new HashMap<>();

        if(text.contains("before start and end")){
            openMap.put("issue opened before sprint with start date", 31);
            return openMap;
        } else if(text.contains("in between")){
            openMap.put("issue opened within sprint with start date", 1);
            return openMap;
        } else if(text.contains("after end")) {
            openMap.put("issue opened after sprint with start date", 4);
            return openMap;
        }
        return openMap;
    }

    private Map<String, Integer> generateCloseMap(String text) {
        Map<String, Integer> closeMap = new HashMap<>();

        if(text.contains("opened and closed within")){
            closeMap.put("issue opened and closed within sprint with start date", 4);
            return closeMap;
        } else if(text.contains("closed before")){
            closeMap.put("issue closed before sprint with start date", 2);
            return closeMap;
        } else if(text.contains("closed after")) {
            closeMap.put("issue closed after sprint with start date", 4);
            return closeMap;
        }
        return closeMap;
    }

    public IssueStatics generateStatics() {
        Map<String, Double> stat = new HashMap<>();
        stat.put(RATIO, 0.9);

        IssueStatics issueStatics = new IssueStatics();
        issueStatics.setAll_issue_count(2);
        issueStatics.setOpen_issues_count(1);
        issueStatics.setClosed_issues_count(1);
        issueStatics.setRatio(stat);

        issueStatics.setOpened_as_percentage(50.00);
        issueStatics.setClosed_as_percentage(50.00);
        issueStatics.setOpen_issue_period(null);
        issueStatics.setClosed_issue_period(null);
        return issueStatics;
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