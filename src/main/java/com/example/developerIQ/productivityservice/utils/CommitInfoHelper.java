package com.example.developerIQ.productivityservice.utils;

import com.example.developerIQ.productivityservice.dto.commit.CommitInformation;
import com.example.developerIQ.productivityservice.dto.pr.PRInformation;
import com.example.developerIQ.productivityservice.dto.pr.PRStatics;
import com.example.developerIQ.productivityservice.entity.CommitEntity;
import com.example.developerIQ.productivityservice.entity.PullRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static com.example.developerIQ.productivityservice.utils.constants.Constants.*;

@Service
public class CommitInfoHelper {
    private static final Logger logger = LoggerFactory.getLogger(CommitInfoHelper.class);
    public LocalDateTime getDateFromString(String date)
    {
        if(date == null) {
            logger.info("date is null");
            return null;
        }
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
    }


    public CommitInformation formatCommitWithinSprintResponse(CommitEntity commit, LocalDateTime endDate, LocalDateTime startDate) {
        CommitInformation commitInformation = new CommitInformation();
        String text = "Sprint " + startDate + " - " + endDate;
        Map<String, Boolean> withinSprintMap = new HashMap<>();

        commitInformation.setId(commit.getCommit_id());
        commitInformation.setName(commit.getCommitter_name());
        commitInformation.setMessage(commit.getMessage());
        commitInformation.setCommitted_date(commit.getCommitted_date().toString());
        withinSprintMap.put(text, true);

        commitInformation.setIs_committed_in_sprint(withinSprintMap);
        return commitInformation;
    }

    public CommitInformation formatCommitNotWithinSprintResponse(CommitEntity commit, LocalDateTime endDate, LocalDateTime startDate) {
        CommitInformation commitInformation = new CommitInformation();
        String text = "Sprint " + startDate + " - " + endDate;
        Map<String, Boolean> withinSprintMap = new HashMap<>();

        commitInformation.setId(commit.getCommit_id());
        commitInformation.setName(commit.getCommitter_name());
        commitInformation.setMessage(commit.getMessage());
        commitInformation.setCommitted_date(commit.getCommitted_date().toString());
        withinSprintMap.put(text, false);

        commitInformation.setIs_committed_in_sprint(withinSprintMap);
        return commitInformation;
    }

    public PRInformation formatCommitResponse(PullRequestEntity pr, LocalDateTime endDate, LocalDateTime startDate) {
        PRInformation prInformation = new PRInformation();

        prInformation.setId(pr.getId());
        prInformation.setName(pr.getUsername());
        prInformation.setUrl(pr.getUrl());
        prInformation.setState(pr.getStatus());
        prInformation.setOpen_since(pr.getCreated_date().toString());

        // pull request success rate
        // check if the closed date is present and status is closed
        if(pr.getIs_merged()) {
          logger.info("PR with id {}, opened by {} is merged", pr.getId(), pr.getUsername());
          prInformation.setMerged(true);
          prInformation.setProductivity_rate(100.00);
        } else {
          logger.info("PR with id {}, opened by {} is not merged yet", pr.getId(), pr.getUsername());
          prInformation.setMerged(false);

          String text = calculatePRProductivityRate(endDate, startDate, pr.getCreated_date(), pr);
          double success_rate = (text.contains(PULL_REQUEST_OPEN_TEXT)) ? 50.00 : 0.00;
          String comment = (success_rate > 49.00) ? "PR is opened within sprint, should provide update" : "PR is opened before sprint and should provide reasons";
          prInformation.setComment(comment);
          prInformation.setProductivity_rate(success_rate);
        }
        return prInformation;

    }

    private String calculatePRProductivityRate(LocalDateTime endDate, LocalDateTime startDate, LocalDateTime createdDate, PullRequestEntity pr) {
        // Check if the date is within the specified range
        boolean isWithinRange = isWithinRange(createdDate, startDate, endDate);
        String text;
        // Output the result
        if (isWithinRange) {
            logger.info("PR is opened within sprint {}", pr.getId());
            return PULL_REQUEST_OPEN_TEXT;
        } else {
            logger.info("PR is opened before sprint {}", pr.getId());
            return PULL_REQUEST_OPEN_TEXT_BEFORE;
        }
    }

    private static boolean isWithinRange(LocalDateTime dateToCheck, LocalDateTime startDate, LocalDateTime endDate) {
        return (dateToCheck.isEqual(startDate) || dateToCheck.isAfter(startDate)) && (dateToCheck.isEqual(endDate) || dateToCheck.isBefore(endDate));
    }

    public PRStatics generateStatics(int allPRCount, int openedCount, int closedCount, double open_percentage, double close_percentage, double ratio) {
        Map<String, Double> stat = new HashMap<>();
        stat.put(RATIO, ratio);

        PRStatics prStatics = new PRStatics();
        prStatics.setOpened_pr_count(openedCount);
        prStatics.setClosed_pr_count(closedCount);
        prStatics.setAll_pr_count(allPRCount);
        prStatics.setRatio(stat);

        prStatics.setOpened_as_percentage(open_percentage);
        prStatics.setClosed_as_percentage(close_percentage);
        return prStatics;
    }
}
