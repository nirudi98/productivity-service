package com.example.developerIQ.productivityservice.dto.commit;

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
public class CommitInformation {

    private String id;
    private String name;
    private String committed_date;
    private String message;
    private Map<String, Boolean> is_committed_in_sprint;

}
