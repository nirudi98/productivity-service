package com.example.developerIQ.productivityservice.controller;

import com.example.developerIQ.productivityservice.dto.commit.Commits;
import com.example.developerIQ.productivityservice.dto.issue.IssueStatics;
import com.example.developerIQ.productivityservice.dto.issue.IssuesSprint;
import com.example.developerIQ.productivityservice.dto.pr.PRStatics;
import com.example.developerIQ.productivityservice.dto.pr.PullRequests;
import com.example.developerIQ.productivityservice.dto.productivity.Productivity;
import com.example.developerIQ.productivityservice.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/productivity")
public class ProductivityController {

    @Autowired
    private ProductivityService productivityService;

    @Autowired
    private PullRequestInfoService pullRequestInfoService;

    @Autowired
    private IssueInfoService issueInfoService;

    @Autowired
    private CommitInfoService commitInfoService;

    @Autowired
    private ValidateService validateService;

    @GetMapping(value = "/display/overview")
    public ResponseEntity<Productivity> retrieveProductivityOverview(@RequestParam String username,
                                                                     @RequestParam String startDate,
                                                                     @RequestParam String endDate,
                                                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        String token = authorizationHeader;

        if(token!= null && token.startsWith("Bearer ")){
            System.out.println("inside here");
            token = token.substring(7);
            System.out.println("token " +token);
        }
        if(validateService.validateToken(token)){
            return ResponseEntity.ok(productivityService.retrieveOverview(username, startDate, endDate));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping(value = "/display/pull-request-productivity")
    public ResponseEntity<PullRequests> retrievePROverview(@RequestParam String username,
                                                           @RequestParam String startDate,
                                                           @RequestParam String endDate,
                                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        String token = authorizationHeader;

        if(token!= null && token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        if(validateService.validateToken(token)){
            return ResponseEntity.ok(pullRequestInfoService.retrievePullRequestSummary(username, startDate, endDate));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping(value = "/display/pull-request-productivity/stats")
    public ResponseEntity<PRStatics> retrievePRStats(@RequestParam String username,
                                                     @RequestParam String startDate,
                                                     @RequestParam String endDate,
                                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        String token = authorizationHeader;

        if(token!= null && token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        if(validateService.validateToken(token)){
            return ResponseEntity.ok(pullRequestInfoService.retrievePullRequestStatics(username, startDate, endDate));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping(value = "/display/issues-productivity")
    public ResponseEntity<IssuesSprint> retrieveIssuesOverview(@RequestParam String username,
                                                               @RequestParam String startDate,
                                                               @RequestParam String endDate,
                                                               @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        String token = authorizationHeader;

        if(token!= null && token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        if(validateService.validateToken(token)){
            return ResponseEntity.ok(issueInfoService.retrieveIssuesSummary(username, startDate, endDate));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping(value = "/display/issues-productivity/stats")
    public ResponseEntity<IssueStatics> retrieveIssuesStats(@RequestParam String username,
                                                            @RequestParam String startDate,
                                                            @RequestParam String endDate,
                                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        String token = authorizationHeader;

        if(token!= null && token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        if(validateService.validateToken(token)){
            return ResponseEntity.ok(issueInfoService.retrieveIssueStatics(username, startDate, endDate));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping(value = "/display/commits-productivity")
    public ResponseEntity<Commits> retrieveCommitsOverview(@RequestParam String username,
                                                           @RequestParam String startDate,
                                                           @RequestParam String endDate,
                                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        String token = authorizationHeader;

        if(token!= null && token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        if(validateService.validateToken(token)){
            return ResponseEntity.ok(commitInfoService.retrieveCommitsSummary(username, startDate, endDate));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
