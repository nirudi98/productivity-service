package com.example.developerIQ.productivityservice.service;

import com.example.developerIQ.productivityservice.dto.pr.PRStatics;
import com.example.developerIQ.productivityservice.dto.pr.PullRequests;
import org.springframework.stereotype.Service;

@Service
public interface PullRequestInfoService {
    public PullRequests retrievePullRequestSummary(String user, String start, String end);
    public PRStatics retrievePullRequestStatics(String user, String start, String end);
}
