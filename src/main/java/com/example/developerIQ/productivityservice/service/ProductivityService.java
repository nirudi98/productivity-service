package com.example.developerIQ.productivityservice.service;

import com.example.developerIQ.productivityservice.dto.productivity.PreviousProductivity;
import com.example.developerIQ.productivityservice.dto.productivity.Productivity;
import org.springframework.stereotype.Service;

@Service
public interface ProductivityService {
    public Productivity retrieveOverview(String username, String start, String end);
    public PreviousProductivity previousProductivity(String start, String end);

}
