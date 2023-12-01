package com.example.developerIQ.productivityservice.dto.pr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PRStatics {

    private int all_pr_count;
    private int opened_pr_count;
    private int closed_pr_count;
    private Map<String, Double> ratio;
    private double opened_as_percentage;
    private double closed_as_percentage;
}
