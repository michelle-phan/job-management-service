package com.taiger.search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@ApiModel("ResourceRef")
public class ResourceRefDto {

    @JsonProperty("uuid")
    @NotNull
    private UUID uuid;

    @JsonProperty("resource")
    @NotNull
    private String resource;

}
