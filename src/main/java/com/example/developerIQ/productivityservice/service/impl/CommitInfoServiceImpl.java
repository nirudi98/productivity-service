package com.example.developerIQ.productivityservice.service.impl;

import com.example.developerIQ.productivityservice.dto.commit.CommitInformation;
import com.example.developerIQ.productivityservice.dto.commit.Commits;
import com.example.developerIQ.productivityservice.entity.CommitEntity;
import com.example.developerIQ.productivityservice.repository.CommitRepository;
import com.example.developerIQ.productivityservice.service.CommitInfoService;
import com.example.developerIQ.productivityservice.utils.CommitInfoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Service
public class CommitInfoServiceImpl implements CommitInfoService {

    private static final Logger logger = LoggerFactory.getLogger(CommitInfoServiceImpl.class);

    @Autowired
    private CommitInfoHelper commitInfoHelper;

    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Commits retrieveCommitsSummary(String user, String start, String end) {
        try{
            // retrieving commits opened by specific user from given start date

            // Parse the date string to LocalDateTime
            LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
            LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

            return fetchCommitsInSprint(user, started, ended);

        } catch(RuntimeException e){
            logger.error("commit data fetch error failed ", e);
            throw new RuntimeException();
        }
    }

    private Commits fetchCommitsInSprint(String username, LocalDateTime started, LocalDateTime ended) {
        List<CommitEntity> fetchedCommitsSprint = commitRepository.findBySprintDate(username, started, ended);
        List<CommitInformation> commitInformations = new ArrayList<>();

        if(fetchedCommitsSprint == null || fetchedCommitsSprint.isEmpty()) {
            logger.error("Commit information not found for this particular user {}",username);
            throw new NullPointerException("Commit information not found for this particular user");
        }

        for(CommitEntity commit: fetchedCommitsSprint) {
            CommitInformation info = commitInfoHelper.formatCommitWithinSprintResponse(
                    commit, ended, started);
            commitInformations.add(info);
        }

        List<CommitEntity> fetchedCommitsNotInSprint = commitRepository.findCommitsNotInSprintByDate(username, started, ended);
        if(fetchedCommitsNotInSprint == null || fetchedCommitsNotInSprint.isEmpty()) {
            logger.error("Commit information not found for this particular user before or after sprint {}",username);
            throw new NullPointerException("Commit information not found for this particular user {} before or after sprint "+ username);
        }

        for(CommitEntity commit: fetchedCommitsNotInSprint) {
            CommitInformation info = commitInfoHelper.formatCommitNotWithinSprintResponse(
                    commit, ended, started);
            commitInformations.add(info);
        }

        int total_commits_within_sprint = fetchedCommitsSprint.size();
        int overall_commits = fetchedCommitsNotInSprint.size() + fetchedCommitsSprint.size();

        Commits commits = new Commits();
        commits.setCommitInformtionList(commitInformations);
        commits.setTotal_commits_within_sprint(total_commits_within_sprint);
        commits.setTotal_commits_overall(overall_commits);
        return commits;
    }

}
