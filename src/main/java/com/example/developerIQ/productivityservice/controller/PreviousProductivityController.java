package com.example.developerIQ.productivityservice.controller;

import com.example.developerIQ.productivityservice.dto.commit.Commits;
import com.example.developerIQ.productivityservice.dto.issue.IssueStatics;
import com.example.developerIQ.productivityservice.dto.issue.IssuesSprint;
import com.example.developerIQ.productivityservice.dto.pr.PRStatics;
import com.example.developerIQ.productivityservice.dto.pr.PullRequests;
import com.example.developerIQ.productivityservice.dto.productivity.PreviousProductivity;
import com.example.developerIQ.productivityservice.dto.productivity.PreviousProductivityRequestBody;
import com.example.developerIQ.productivityservice.dto.productivity.Productivity;
import com.example.developerIQ.productivityservice.service.CommitInfoService;
import com.example.developerIQ.productivityservice.service.IssueInfoService;
import com.example.developerIQ.productivityservice.service.ProductivityService;
import com.example.developerIQ.productivityservice.service.PullRequestInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/previous/sprint")
public class PreviousProductivityController {

    @Autowired
    private ProductivityService productivityService;

    @Autowired
    private PullRequestInfoService pullRequestInfoService;

    @Autowired
    private IssueInfoService issueInfoService;

    @Autowired
    private CommitInfoService commitInfoService;

    @GetMapping(value = "/display/overview")
    public ResponseEntity<PreviousProductivity> retrievePreviousProductivityOverview(@RequestParam String start, @RequestParam String end){
        return ResponseEntity.ok(productivityService.previousProductivity(start, end));
    }

    @GetMapping("/health-check")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.status(HttpStatus.OK).body("Health Check OK");
    }

}
