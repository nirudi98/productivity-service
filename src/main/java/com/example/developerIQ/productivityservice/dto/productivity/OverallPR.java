package com.example.developerIQ.productivityservice.dto.productivity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OverallPR {

    private int total_opened_PR;
    private int total_closed_PR;

    private int total_opened_PR_within_sprint;
    private int total_closed_PR_within_sprint;
    private int total_opened_out_of_sprint;
}
