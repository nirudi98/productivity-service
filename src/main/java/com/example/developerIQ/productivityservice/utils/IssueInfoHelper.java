package com.example.developerIQ.productivityservice.utils;

import com.example.developerIQ.productivityservice.dto.issue.IssueInformation;
import com.example.developerIQ.productivityservice.dto.issue.IssueStatics;
import com.example.developerIQ.productivityservice.dto.pr.PRInformation;
import com.example.developerIQ.productivityservice.dto.pr.PRStatics;
import com.example.developerIQ.productivityservice.entity.IssueEntity;
import com.example.developerIQ.productivityservice.entity.PullRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.developerIQ.productivityservice.utils.constants.Constants.*;

@Service
public class IssueInfoHelper {
    private static final Logger logger = LoggerFactory.getLogger(IssueInfoHelper.class);

    public IssueInformation formatIssueResponse(IssueEntity issue, LocalDateTime endDate, LocalDateTime startDate) {
        IssueInformation issueInformation = new IssueInformation();

        issueInformation.setId(issue.getIssue_id());
        issueInformation.setName(issue.getUsername());
        issueInformation.setTitle(issue.getTitle());
        issueInformation.setState(issue.getState());
        issueInformation.setOpen_since(issue.getCreated_date().toString());

        // issue success rate
        // check if the closed date is present and status is closed
        if(Objects.equals(issue.getState(), "closed")) {
            // if status is closed, closed date exists
          logger.info("Issue with id {}, opened by {} is closed", issue.getIssue_id(), issue.getUsername());
          issueInformation.setIsClosed(true);
          issueInformation.setProductivity_rate(100.00);
        } else {
          logger.info("PR with id {}, opened by {} is still opened", issue.getIssue_id(), issue.getUsername());
          issueInformation.setIsClosed(false);

          String text = calculateIssueProductivityRate(endDate, startDate, issue.getCreated_date(), issue);
          double success_rate = (text.contains(ISSUE_OPEN_TEXT)) ? 50.00 : 0.00;
          String comment = (success_rate > 49.00) ? "Issue is opened within sprint, should provide update" :
                  "Issue is opened before sprint and should provide reasons why not resolved yet";
          issueInformation.setComment(comment);
          issueInformation.setProductivity_rate(success_rate);
        }
        return issueInformation;

    }

    private String calculateIssueProductivityRate(LocalDateTime endDate, LocalDateTime startDate, LocalDateTime createdDate, IssueEntity issue) {
        // Check if the date is within the specified range
        boolean isWithinRange = isWithinRange(createdDate, startDate, endDate);
        String text;
        // Output the result
        if (isWithinRange) {
            logger.info("Issue is opened within sprint {}", issue.getIssue_id());
            return ISSUE_OPEN_TEXT;
        } else {
            logger.info("Issue is opened before sprint {}", issue.getIssue_id());
            return ISSUE_OPEN_TEXT_BEFORE;
        }
    }

    private static boolean isWithinRange(LocalDateTime dateToCheck, LocalDateTime startDate, LocalDateTime endDate) {
        return (dateToCheck.isEqual(startDate) || dateToCheck.isAfter(startDate)) && (dateToCheck.isEqual(endDate) || dateToCheck.isBefore(endDate));
    }

    public Map<String, Integer> generateOpenIssueMap(List<IssueEntity> openIssues, LocalDateTime start, LocalDateTime ended) {
        Map<String, Integer> openMap = new HashMap<>();
        String text;

        for(IssueEntity i: openIssues) {
            LocalDateTime opened_date = i.getCreated_date();
            Duration duration = Duration.between(start, opened_date);
            int days = (int) Math.abs(duration.toDays());

            if(!opened_date.isBefore(start) && opened_date.isBefore(ended.plusSeconds(1))) {
                text = "issue opened within sprint with start date " + start.toString() + " and has been open for ";
                openMap.put(text, days);
            } else if (opened_date.isBefore(start)) {
                text = "issue opened before sprint with start date " + start.toString() + " and has been open for ";
                openMap.put(text, days);
            } else if (opened_date.isAfter(ended)) {
                text = "issue opened after sprint with start date " + start.toString() + " ended has been open for";
                openMap.put(text, days);
            }
        }
        return openMap;
    }

    public Map<String, Integer> generateClosedIssueMap(List<IssueEntity> closeIssues, LocalDateTime end, LocalDateTime start) {
        Map<String, Integer> closeMap = new HashMap<>();
        String text;

        for(IssueEntity i: closeIssues) {
            LocalDateTime closed_date = i.getClosed_date();
            LocalDateTime started_date = i.getCreated_date();
            Duration duration = Duration.between(closed_date, end);
            int days = (int) Math.abs(duration.toDays());

            if(!closed_date.isBefore(start) && closed_date.isBefore(end.plusSeconds(1)) &&
                    (!started_date.isBefore(start) && started_date.isBefore(end.plusSeconds(1)))) {
                text = "issue opened and closed within sprint with start date " + start.toString() + " and has been closed for ";
                closeMap.put(text, days);
            } else if (closed_date.isBefore(start)) {
                text = "issue closed before sprint with start date " + start.toString() + " and has been closed for ";
                closeMap.put(text, days);
            } else if (closed_date.isAfter(end)) {
                text = "issue closed after sprint with start date " + start.toString() + " ended has been closed for";
                closeMap.put(text, days);
            }
        }
        return closeMap;
    }

    public IssueStatics generateStatics(int allCount, int openedCount, int closedCount, double open_percentage,
                                     double close_percentage,
                                     double ratio,
                                     Map<String, Integer> closeIssue,
                                     Map<String, Integer> openIssue) {
        Map<String, Double> stat = new HashMap<>();
        stat.put(RATIO, ratio);

        IssueStatics issueStatics = new IssueStatics();
        issueStatics.setAll_issue_count(allCount);
        issueStatics.setOpen_issues_count(openedCount);
        issueStatics.setClosed_issues_count(closedCount);
        issueStatics.setRatio(stat);

        issueStatics.setOpened_as_percentage(open_percentage);
        issueStatics.setClosed_as_percentage(close_percentage);
        issueStatics.setOpen_issue_period(openIssue);
        issueStatics.setClosed_issue_period(closeIssue);
        return issueStatics;
    }
}
