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
        try {
            List<CommitEntity> fetchedCommitsSprint = fetchCommitsInSprintHelper(username, started, ended);
            List<CommitEntity> fetchedCommitsOutSprint = fetchCommitsOutSprintHelper(username, started, ended);
            List<CommitInformation> commitInformations = new ArrayList<>();

            int total_commits_within_sprint_null = 0; int total_overall_null = 0;

            if(fetchedCommitsSprint == null || fetchedCommitsSprint.isEmpty()) {
                System.out.println("in of sprint is empty ");
                if(fetchedCommitsOutSprint == null || fetchedCommitsOutSprint.isEmpty()) {
                    System.out.println("out of sprint is empty ");
                    System.out.println("both empty ");
                    Commits commits = new Commits();
                    commits.setCommitInformtionList(null);
                    commits.setTotal_commits_within_sprint(total_commits_within_sprint_null);
                    commits.setTotal_commits_overall(total_overall_null);
                    System.out.println("commit " + commits.getTotal_commits_overall() + " " + commits.getTotal_commits_within_sprint() + " " +
                            commits.getCommitInformtionList());
                    return commits;
                } else {
                    System.out.println("out of sprint is not empty but in sprint empty ");
                    int overall_commits = fetchedCommitsOutSprint.size();
                    List<CommitInformation> commitInformations1 = new ArrayList<>();
                    for(CommitEntity commit: fetchedCommitsOutSprint) {
                        CommitInformation info = commitInfoHelper.formatCommitNotWithinSprintResponse(
                        commit, ended, started);
                        commitInformations1.add(info);
                    }

                    Commits commits = new Commits();
                    commits.setCommitInformtionList(commitInformations1);
                    commits.setTotal_commits_within_sprint(total_commits_within_sprint_null);
                    commits.setTotal_commits_overall(overall_commits);
                    System.out.println("commit " + commits.getTotal_commits_overall() + " " + commits.getTotal_commits_within_sprint() + " " +
                            commits.getCommitInformtionList());
                    return commits;
                }
            }

            System.out.println("in sprint not empty ");
            for(CommitEntity commit: fetchedCommitsSprint) {
                CommitInformation info = commitInfoHelper.formatCommitWithinSprintResponse(
                        commit, ended, started);
                commitInformations.add(info);
            }
            System.out.println("commit info list " + commitInformations.size());

            if(fetchedCommitsOutSprint == null || fetchedCommitsOutSprint.isEmpty()) {
                System.out.println("out of sprint is empty but in is not empty");
                int overall_commits = fetchedCommitsSprint.size();

                Commits commits = new Commits();
                commits.setCommitInformtionList(commitInformations);
                commits.setTotal_commits_within_sprint(fetchedCommitsSprint.size());
                commits.setTotal_commits_overall(overall_commits);
                System.out.println("commit " + commits.getTotal_commits_overall() + " " + commits.getTotal_commits_within_sprint() + " " +
                        commits.getCommitInformtionList());
                return commits;
            } else {
                System.out.println("out of sprint not empty and in spring not empty both there ");
                int overall_commits = fetchedCommitsSprint.size() + fetchedCommitsOutSprint.size();

                List<CommitInformation> commitInformations2 = new ArrayList<>();
                List<CommitInformation> commitInformationsAll = new ArrayList<>(commitInformations);

                for(CommitEntity commit: fetchedCommitsOutSprint) {
                    CommitInformation info = commitInfoHelper.formatCommitWithinSprintResponse(
                            commit, ended, started);
                    commitInformations2.add(info);
                }
                commitInformationsAll.addAll(commitInformations2);

                Commits commits = new Commits();
                commits.setCommitInformtionList(commitInformationsAll);
                commits.setTotal_commits_within_sprint(fetchedCommitsSprint.size());
                commits.setTotal_commits_overall(overall_commits);
                System.out.println("commit " + commits.getTotal_commits_overall() + " " + commits.getTotal_commits_within_sprint() + " " +
                        commits.getCommitInformtionList());
                return commits;
            }
        } catch (RuntimeException e) {
            logger.error("Error occurred while processing commits productivity");
            return null;
        }

    }

    private List<CommitEntity> fetchCommitsInSprintHelper(String username, LocalDateTime started, LocalDateTime ended) {
        try {
            List<CommitEntity> fetchedCommitsSprint = commitRepository.findBySprintDate(username, started, ended);
            System.out.println("db params " + username + " " + started + " " +ended);

            System.out.println("size of commits in spring " + fetchedCommitsSprint.size());

            if(fetchedCommitsSprint == null || fetchedCommitsSprint.isEmpty()) {
                logger.error("Commit information not found for this particular user {}",username);
                throw new NullPointerException("Commit information not found for this particular user");
            }
            return fetchedCommitsSprint;


        } catch (RuntimeException e) {
            logger.error("Error occurred while processing commits productivity");
            return null;
        }

    }

    private List<CommitEntity> fetchCommitsOutSprintHelper(String username, LocalDateTime started, LocalDateTime ended) {
        try {
            List<CommitEntity> fetchedCommitsNotInSprint = commitRepository.findCommitsNotInSprintByDate(username, started, ended);
            System.out.println("size of commits in spring out" + fetchedCommitsNotInSprint.size());

            if(fetchedCommitsNotInSprint == null || fetchedCommitsNotInSprint.isEmpty()) {
                logger.error("Commit information not found for this particular user before or after sprint {}",username);
                throw new NullPointerException("Commit information not found for this particular user {} before or after sprint "+ username);
            }

            return fetchedCommitsNotInSprint;
        } catch (RuntimeException e) {
            logger.error("Error occurred while processing commits productivity");
            return null;
        }

    }

}
