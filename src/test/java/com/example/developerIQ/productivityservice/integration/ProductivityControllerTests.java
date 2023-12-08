package com.example.developerIQ.productivityservice.integration;

import com.example.developerIQ.productivityservice.dto.commit.Commits;
import com.example.developerIQ.productivityservice.dto.issue.IssueStatics;
import com.example.developerIQ.productivityservice.dto.issue.IssuesSprint;
import com.example.developerIQ.productivityservice.dto.pr.PullRequests;
import com.example.developerIQ.productivityservice.dto.productivity.Productivity;
import com.example.developerIQ.productivityservice.entity.CommitEntity;
import com.example.developerIQ.productivityservice.entity.IssueEntity;
import com.example.developerIQ.productivityservice.entity.PullRequestEntity;
import com.example.developerIQ.productivityservice.repository.CommitRepository;
import com.example.developerIQ.productivityservice.repository.IssueRepository;
import com.example.developerIQ.productivityservice.repository.PullRequestRepository;
import com.example.developerIQ.productivityservice.service.impl.ValidateServiceImpl;
import com.example.developerIQ.productivityservice.utils.AuthenticateUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(locations = "classpath:application-test.properties")
public class ProductivityControllerTests {

    @Container
    @ServiceConnection
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:5.0");

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    private PullRequestRepository pullRequestRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private ValidateServiceImpl validateService;

    @Autowired
    private AuthenticateUser authenticateUser;


    @BeforeEach
    void setUp() {
        LocalDateTime started = LocalDateTime.parse("2023-10-01" +
                " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
        LocalDateTime ended = LocalDateTime.parse("2023-10-13" +
                " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

        List<CommitEntity> commitEntityList = List.of(new CommitEntity
                ("123456","author", "test",
                        "commit@gmail.com","this is test commit",started, ended));
        commitRepository.saveAll(commitEntityList);

        List<PullRequestEntity> pullRequestEntityList = List.of(new PullRequestEntity
                ("pre123","pre@github.com", "test",
                        "test PR","1",false, "user","open",started, ended, null));
        pullRequestRepository.saveAll(pullRequestEntityList);

        List<IssueEntity> issueEntityList = List.of(new IssueEntity
                ("issue_1234","issue one", "open",
                        "test","open issue from test",started, ended));
        issueRepository.saveAll(issueEntityList);
    }

    @Test
    public void testRetrieveProductivityOverview() {
        String validToken = authenticateUser.validateTokenAuthService();

        HttpHeaders headers = new HttpHeaders();
        String finalToken = "Bearer " + validToken;
        headers.set("Authorization", finalToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Productivity> response = restTemplate.exchange("/productivity/display/overview?username=test&startDate=2023-01-01&endDate=2023-01-31",
                HttpMethod.GET,
                requestEntity,
                Productivity.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testRetrieveProductivityOverview_exception() {

        HttpHeaders headers = new HttpHeaders();
        String finalToken = "1234";
        headers.set("Authorization", finalToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Productivity> response = restTemplate.exchange("/productivity/display/overview?username=test&startDate=2023-01-01&endDate=2023-01-31",
                HttpMethod.GET,
                requestEntity,
                Productivity.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testRetrievePROverview() {
        String validToken = authenticateUser.validateTokenAuthService();

        HttpHeaders headers = new HttpHeaders();
        String finalToken = "Bearer " + validToken;
        headers.set("Authorization", finalToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<PullRequests> response = restTemplate.exchange("/productivity/display/pull-request-productivity?username=test&startDate=2023-01-01&endDate=2023-01-31",
                HttpMethod.GET,
                requestEntity,
                PullRequests.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testRetrievePROverview_exception() {

        HttpHeaders headers = new HttpHeaders();
        String finalToken = "invalid_token";
        headers.set("Authorization", finalToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<PullRequests> response = restTemplate.exchange("/productivity/display/pull-request-productivity?username=test&startDate=2023-01-01&endDate=2023-01-31",
                HttpMethod.GET,
                requestEntity,
                PullRequests.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testRetrievePRStats() {
        String validToken = authenticateUser.validateTokenAuthService();

        HttpHeaders headers = new HttpHeaders();
        String finalToken = "Bearer " + validToken;
        headers.set("Authorization", finalToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<PullRequests> response = restTemplate.exchange("/productivity/display/pull-request-productivity/stats" +
                        "?username=test&startDate=2023-01-01&endDate=2023-01-31",
                HttpMethod.GET,
                requestEntity,
                PullRequests.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testRetrievePRStats_exception() {

        HttpHeaders headers = new HttpHeaders();
        String finalToken = "invalid_token";
        headers.set("Authorization", finalToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<PullRequests> response = restTemplate.exchange("/productivity/display/pull-request-productivity/stats" +
                        "?username=test&startDate=2023-01-01&endDate=2023-01-31",
                HttpMethod.GET,
                requestEntity,
                PullRequests.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testRetrieveIssuesOverview() {
        String validToken = authenticateUser.validateTokenAuthService();

        HttpHeaders headers = new HttpHeaders();
        String finalToken = "Bearer " + validToken;
        headers.set("Authorization", finalToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<IssuesSprint> response = restTemplate.exchange("/productivity/display/issues-productivity" +
                        "?username=test&startDate=2023-01-01&endDate=2023-01-31",
                HttpMethod.GET,
                requestEntity,
                IssuesSprint.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("issue one", Objects.requireNonNull(response.getBody()).getIssueInformationList().stream().toList().get(0).getTitle());

    }

    @Test
    public void testRetrieveIssuesOverview_exception() {

        HttpHeaders headers = new HttpHeaders();
        String finalToken = "invalid_token";
        headers.set("Authorization", finalToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<IssuesSprint> response = restTemplate.exchange("/productivity/display/issues-productivity" +
                        "?username=test&startDate=2023-01-01&endDate=2023-01-31",
                HttpMethod.GET,
                requestEntity,
                IssuesSprint.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testRetrieveIssuesStats() {
        String validToken = authenticateUser.validateTokenAuthService();

        HttpHeaders headers = new HttpHeaders();
        String finalToken = "Bearer " + validToken;
        headers.set("Authorization", finalToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<IssueStatics> response = restTemplate.exchange("/productivity/display/issues-productivity/stats" +
                        "?username=test&startDate=2023-01-01&endDate=2023-01-31",
                HttpMethod.GET,
                requestEntity,
                IssueStatics.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, Objects.requireNonNull(response.getBody()).getAll_issue_count());
    }

    @Test
    public void testRetrieveIssuesStats_exception() {

        HttpHeaders headers = new HttpHeaders();
        String finalToken = "invalid_token";
        headers.set("Authorization", finalToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<IssueStatics> response = restTemplate.exchange("/productivity/display/issues-productivity/stats" +
                        "?username=test&startDate=2023-01-01&endDate=2023-01-31",
                HttpMethod.GET,
                requestEntity,
                IssueStatics.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testRetrieveCommitsOverview() {
        String validToken = authenticateUser.validateTokenAuthService();

        HttpHeaders headers = new HttpHeaders();
        String finalToken = "Bearer " + validToken;
        headers.set("Authorization", finalToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Commits> response = restTemplate.exchange("/productivity/display/commits-productivity" +
                        "?username=test&startDate=2023-01-01&endDate=2023-01-31",
                HttpMethod.GET,
                requestEntity,
                Commits.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testRetrieveCommitsOverview_exception() {

        HttpHeaders headers = new HttpHeaders();
        String finalToken = "invalid_token";
        headers.set("Authorization", finalToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Commits> response = restTemplate.exchange("/productivity/display/commits-productivity" +
                        "?username=test&startDate=2023-01-01&endDate=2023-01-31",
                HttpMethod.GET,
                requestEntity,
                Commits.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

}
