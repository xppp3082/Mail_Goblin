package com.example.personal_project.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Audience {
    private Long id;
    private String name;
    private String email;
    private String birthday;
    @JsonProperty("audience_uuid")
    private String audienceUUID;
    @JsonProperty("mailcount")
    private Integer mailCount;
    @JsonProperty("opencount")
    private Integer openCount;
    @JsonProperty("clickcount")
    private Integer clickCount;
    @JsonProperty("create_time")
    private Timestamp createTime;
    @JsonProperty("company_id")
    private Long companyId;
    @Nullable
    private String tagName;
}
