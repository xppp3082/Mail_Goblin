package com.example.personal_project.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GenericResponse<T> {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("next_paging")
    public Integer nextPaging;
    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("total_paging")
    public Integer totalPaging;
    @JsonProperty("data")
    T data;

    public GenericResponse(T data) {
        this(data, null, null);
    }

    public GenericResponse(T data, Integer nextPaging, Integer totalPaging) {
        this.data = data;
        this.nextPaging = nextPaging;
        this.totalPaging = totalPaging;
    }

    public GenericResponse(T data, Integer nextPaging) {
        this.data = data;
        this.nextPaging = nextPaging;
    }
}
