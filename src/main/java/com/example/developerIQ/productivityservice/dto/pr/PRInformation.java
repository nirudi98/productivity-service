package com.example.developerIQ.productivityservice.dto.pr;

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
public class PRInformation {

    private String id;
    private String url;
    private String state;
    private String name;
    private String open_since;
    private Boolean merged;
    private Double productivity_rate;
    private String comment;
}
