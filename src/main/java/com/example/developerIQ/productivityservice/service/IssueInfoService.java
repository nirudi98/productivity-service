package com.example.developerIQ.productivityservice.service;

import com.example.developerIQ.productivityservice.dto.issue.IssueStatics;
import com.example.developerIQ.productivityservice.dto.issue.IssuesSprint;
import com.example.developerIQ.productivityservice.dto.pr.PRStatics;
import org.springframework.stereotype.Service;

@Service
public interface IssueInfoService {
    public IssuesSprint retrieveIssuesSummary(String user, String start, String end);
    public IssueStatics retrieveIssueStatics(String user, String start, String end);
}
