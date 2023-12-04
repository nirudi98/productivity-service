package com.example.developerIQ.productivityservice.service.impl;

import com.example.developerIQ.productivityservice.dto.issue.IssueInformation;
import com.example.developerIQ.productivityservice.dto.issue.IssueStatics;
import com.example.developerIQ.productivityservice.dto.issue.IssuesSprint;
import com.example.developerIQ.productivityservice.dto.pr.PRInformation;
import com.example.developerIQ.productivityservice.dto.pr.PRStatics;
import com.example.developerIQ.productivityservice.dto.pr.PullRequests;
import com.example.developerIQ.productivityservice.entity.IssueEntity;
import com.example.developerIQ.productivityservice.entity.PullRequestEntity;
import com.example.developerIQ.productivityservice.repository.IssueRepository;
import com.example.developerIQ.productivityservice.service.IssueInfoService;
import com.example.developerIQ.productivityservice.utils.IssueInfoHelper;
import com.example.developerIQ.productivityservice.utils.PullRequestInfoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.developerIQ.productivityservice.utils.constants.Constants.RATIO;

@Service
public class IssueInfoServiceImpl implements IssueInfoService {

    private static final Logger logger = LoggerFactory.getLogger(IssueInfoServiceImpl.class);

    @Autowired
    private IssueInfoHelper issueInfoHelper;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public IssuesSprint retrieveIssuesSummary(String user, String start, String end) {
        try{
            // retrieving all issues opened and closed by specific user from given start date

            // Parse the date string to LocalDateTime
            LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
            LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

            // issues opened or closed within sprint start
            List<IssueEntity> fetchedIssues = issueRepository.findIssuesByNameDate(user, started);
            List<IssueInformation> issueInformations = new ArrayList<>();
            IssuesSprint issues_in_sprint = new IssuesSprint();

            if(fetchedIssues == null || fetchedIssues.isEmpty()) {
                logger.error("Issues information not found for this particular user {}",user);
                throw new NullPointerException("Issues information not found for this particular user");
            }

            for(IssueEntity issue: fetchedIssues) {
                IssueInformation info = issueInfoHelper.formatIssueResponse(
                        issue, ended, started);
                issueInformations.add(info);
            }

            issues_in_sprint.setIssueInformationList(issueInformations);
            return issues_in_sprint;

        } catch(RuntimeException e){
            logger.error("issues data fetch error failed ", e);
            throw new RuntimeException();
        }
    }

    @Override
    public IssueStatics retrieveIssueStatics(String user, String start, String end) {
        try{
            // retrieving issue statics for a specific user from given start date

            // Parse the date string to LocalDateTime
            LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
            LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

            // open issues
            List<IssueEntity> fetchedOpenIssues = issueRepository.findIssuesByNameState(user, "open");
            // close issues
            List<IssueEntity> fetchedClosedIssues = issueRepository.findIssuesByNameState(user, "closed");

            if(fetchedOpenIssues == null || fetchedOpenIssues.isEmpty()) {

                if(fetchedClosedIssues == null || fetchedClosedIssues.isEmpty()) {
                    return issueInfoHelper.generateStatics(0, 0, 0,
                            0, 0, 0, null, null);

                } else {
                    int closed_issue_count = fetchedClosedIssues.size();
                    Map<String, Integer> closed_issues_map = issueInfoHelper.generateClosedIssueMap(fetchedClosedIssues, ended, started);

                    // get opened:closed ratio
                    double ratio = calculateRatio(0, closed_issue_count);

                    // opened percentage
                    double open_percentage = calculatePercentage(0, closed_issue_count);
                    // closed percentage
                    double close_percentage = calculatePercentage(closed_issue_count, closed_issue_count);

                    return issueInfoHelper.generateStatics(closed_issue_count, 0, closed_issue_count,
                            open_percentage, close_percentage, ratio, closed_issues_map, null);
                }
            } else {

                int open_issue_count = fetchedOpenIssues.size();
                Map<String, Integer> open_issues_map = issueInfoHelper.generateOpenIssueMap(fetchedOpenIssues, started, ended);

                if(fetchedClosedIssues == null || fetchedClosedIssues.isEmpty()) {

                    // get opened:closed ratio
                    double ratio = calculateRatio(open_issue_count, 0);
                    // opened percentage
                    double open_percentage = calculatePercentage(open_issue_count, open_issue_count);
                    // closed percentage
                    double close_percentage = calculatePercentage(0, open_issue_count);

                    return issueInfoHelper.generateStatics(open_issue_count, open_issue_count, 0,
                            open_percentage, close_percentage, ratio, null, open_issues_map);

                } else {

                    int closed_issue_count = fetchedOpenIssues.size();
                    Map<String, Integer> closed_issues_map = issueInfoHelper.generateClosedIssueMap(fetchedClosedIssues, ended, started);

                    int total_count = open_issue_count + closed_issue_count;
                    // get opened:closed ratio
                    double ratio = calculateRatio(open_issue_count, closed_issue_count);
                    // opened percentage
                    double open_percentage = calculatePercentage(open_issue_count, open_issue_count);
                    // closed percentage
                    double close_percentage = calculatePercentage(closed_issue_count, open_issue_count);

                    return issueInfoHelper.generateStatics(total_count, open_issue_count, closed_issue_count,
                            open_percentage, close_percentage, ratio, closed_issues_map, open_issues_map);
                }
            }

        } catch(RuntimeException e){
            logger.error("issue data fetch error failed ", e);
            throw new RuntimeException();
        }
    }

    private static double calculateRatio(int openedCount, int closedCount) {
        // Avoid division by zero
        if (closedCount == 0) {
            if (openedCount == 0) {
                // Handle the case when both counts are zero
                logger.info("Both openedCount and closedCount are zero.");
                return Double.NaN; // Not a Number
            } else {
                // Handle the case when only closedCount is zero
                logger.info("closedCount is zero.");
                return Double.POSITIVE_INFINITY; // Positive infinity
            }
        }
        // Calculate the ratio
        return (double) openedCount / closedCount;
    }

    private static double calculatePercentage(int count, int allPRCount) {
        // Avoid division by zero
        if (allPRCount == 0) {
            logger.info("Error: allPRCount is zero. Percentage is undefined.");
            return Double.NaN; // Not a Number
        }

        // Calculate the percentage
        return (double) count / allPRCount * 100;
    }

}
