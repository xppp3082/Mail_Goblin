package com.example.personal_project.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Campaign extends ReadObject {
    private Long id;
    @JsonProperty("template_id")
    private Long templateId;
    @JsonProperty("tag_id")
    private Long tagId;
    @JsonProperty("automation_id")
    private Long automationId;
    @JsonProperty("execute_status")
    private String executeStatus;
    @JsonProperty("tag")
    private String tagName;
}
