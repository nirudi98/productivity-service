package com.example.developerIQ.productivityservice.service;

import com.example.developerIQ.productivityservice.dto.commit.Commits;
import com.example.developerIQ.productivityservice.dto.pr.PullRequests;
import org.springframework.stereotype.Service;

@Service
public interface CommitInfoService {
    public Commits retrieveCommitsSummary(String user, String start, String end);
}
