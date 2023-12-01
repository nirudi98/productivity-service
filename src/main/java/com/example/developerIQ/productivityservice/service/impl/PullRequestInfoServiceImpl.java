package com.example.developerIQ.productivityservice.service.impl;

import com.example.developerIQ.productivityservice.dto.pr.PRInformation;
import com.example.developerIQ.productivityservice.dto.pr.PRStatics;
import com.example.developerIQ.productivityservice.dto.pr.PullRequests;
import com.example.developerIQ.productivityservice.entity.PullRequestEntity;
import com.example.developerIQ.productivityservice.repository.PullRequestRepository;
import com.example.developerIQ.productivityservice.service.PullRequestInfoService;
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
import java.util.Objects;

import static com.example.developerIQ.productivityservice.utils.constants.Constants.RATIO;

@Service
public class PullRequestInfoServiceImpl implements PullRequestInfoService {

    private static final Logger logger = LoggerFactory.getLogger(PullRequestInfoServiceImpl.class);

    @Autowired
    private PullRequestInfoHelper pullRequestInfoHelper;

    @Autowired
    private PullRequestRepository pullRequestRepository;

//    @Autowired
//    private CommitRepository commitRepository;
//
//    @Autowired
//    private IssueRepository issueRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public PullRequests retrievePullRequestSummary(String user, String start, String end) {
        try{
            // retrieving pull requests opened by specific user from given start date

            // Parse the date string to LocalDateTime
            LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
            LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

            List<PullRequestEntity> fetchedPR = pullRequestRepository.findPullRequestByNameDate(user, started);
            List<PRInformation> prInformations = new ArrayList<>();
            PullRequests pullRequests = new PullRequests();

            if(fetchedPR == null || fetchedPR.isEmpty()) {
                logger.error("Pull request information not found for this particular user {}",user);
                throw new NullPointerException("Pull request information not found for this particular user");
            }

            for(PullRequestEntity pr: fetchedPR) {
                PRInformation info = pullRequestInfoHelper.formatPRResponse(
                        pr, ended, started);
                prInformations.add(info);
            }

            pullRequests.setPullRequestList(prInformations);
            return pullRequests;

        } catch(RuntimeException e){
            logger.error("pull requests data fetch error failed ", e);
            throw new RuntimeException();
        }
    }

    @Override
    public PRStatics retrievePullRequestStatics(String user, String start, String end) {
        try{
            // retrieving pull requests statics for a specific user from given start date

            // Parse the date string to LocalDateTime
            LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
            LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

            List<PullRequestEntity> fetchedPR = pullRequestRepository.findPullRequestByNameDate(user, started);

            // get all PR count
            if(fetchedPR == null || fetchedPR.isEmpty()) {
                logger.error("Pull request information not found for this particular user {}",user);
                throw new NullPointerException("Pull request information not found for this particular user");
            }
            int allPRCount = fetchedPR.size();

            // open and closed PR count
            int openedCount = 0;
            int closedCount = 0;
            for(PullRequestEntity pr: fetchedPR) {
                if(Objects.equals(pr.getStatus(), "open")) {
                    openedCount++;
                }
                if(pr.getIs_merged()) {
                    closedCount++;
                }
            }

            // get opened:closed ratio
            double ratio = calculateRatio(openedCount, closedCount);
            String text = RATIO;

            // opened percentage
            double open_percentage = calculatePercentage(openedCount, allPRCount);
            // closed percentage
            double close_percentage = calculatePercentage(closedCount, allPRCount);

            return pullRequestInfoHelper.generateStatics(allPRCount, openedCount, closedCount, open_percentage, close_percentage, ratio);

        } catch(RuntimeException e){
            logger.error("pull requests data fetch error failed ", e);
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

//    @Override
//    public String savePullRequests() {
//        try{
//            // fetch PR details
//            List<PullRequestEntity> pullRequestEntityList = githubService.fetchPullRequests();
//            if(pullRequestEntityList == null || pullRequestEntityList.isEmpty()) {
//                throw new NullPointerException("final PR Entity list is null and saving failed");
//            }
//            for(PullRequestEntity pr: pullRequestEntityList) {
//                pullRequestRepository.save(pr);
//            }
//            return "Pull Requests Saved Successfully";
//
//        } catch(RuntimeException e){
//            logger.error("pull requests save failed ", e);
//            throw new RuntimeException();
//        }
//    }
//
//    @Override
//    public String saveCommits() {
//        try{
//            // fetch Commit details
//            List<CommitEntity> commitEntityList = githubService.fetchCommits();
//            if(commitEntityList == null || commitEntityList.isEmpty()) {
//                throw new NullPointerException("final Commit Entity list is null and saving failed");
//            }
//            for(CommitEntity commit: commitEntityList) {
//                commitRepository.save(commit);
//            }
//            return "Commits Saved Successfully";
//
//        } catch(RuntimeException e){
//            logger.error("commits saved failed ", e);
//            throw new RuntimeException();
//        }
//    }
//
//    @Override
//    public String saveIssues() {
//        try{
//            // fetch Open Issues details
//            List<IssueEntity> openIssueEntityList = githubService.fetchOpenIssues();
//            // fetch Closed Issues details
//            List<IssueEntity> closeIssueEntityList = githubService.fetchCloseIssues();
//
//            List<IssueEntity> allIssues = new ArrayList<>();
//            allIssues.addAll(openIssueEntityList);
//            allIssues.addAll(closeIssueEntityList);
//
//            if(allIssues.isEmpty()) {
//                throw new NullPointerException("final Issue Entity list is null and saving failed");
//            }
//            for(IssueEntity issue: allIssues) {
//                issueRepository.save(issue);
//            }
//            return "Issues Saved Successfully";
//
//        } catch(RuntimeException e){
//            logger.error("issue saving failed ", e);
//            throw new RuntimeException();
//        }
//    }

}
