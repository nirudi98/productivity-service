package com.example.developerIQ.productivityservice.integration;

import com.example.developerIQ.productivityservice.dto.commit.Commits;
import com.example.developerIQ.productivityservice.dto.issue.IssueStatics;
import com.example.developerIQ.productivityservice.dto.issue.IssuesSprint;
import com.example.developerIQ.productivityservice.dto.pr.PullRequests;
import com.example.developerIQ.productivityservice.dto.productivity.PreviousProductivity;
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
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(locations = "classpath:application-test.properties")
public class PreviousProductivityControllerTests {

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

        ResponseEntity<PreviousProductivity> response = restTemplate.exchange
                ("/previous/sprint/display/overview?start=2023-01-01&end=2023-01-31",
                HttpMethod.GET,
                requestEntity,
                PreviousProductivity.class
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

        ResponseEntity<PreviousProductivity> response = restTemplate.exchange
                ("/previous/sprint/display/overview?startDate=2023-01-01&end=2023-01-31",
                HttpMethod.GET,
                requestEntity,
                PreviousProductivity.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
