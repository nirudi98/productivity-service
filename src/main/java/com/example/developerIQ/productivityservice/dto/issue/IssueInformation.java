package com.example.developerIQ.productivityservice.dto.issue;

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
public class IssueInformation {

    private String id;
    private String title;
    private String state;
    private String name;
    private String open_since;
    private Boolean isClosed;
    private Double productivity_rate;
    private String comment;
}
