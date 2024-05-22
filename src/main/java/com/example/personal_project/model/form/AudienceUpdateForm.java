package com.example.personal_project.model.form;

import com.example.personal_project.model.Tag;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AudienceUpdateForm {
    //    private Long id;
    private String name;
    private String email;
    private String birthday;
    @Nullable
    @JsonProperty("tags")
    private List<Tag> tagList;
}
