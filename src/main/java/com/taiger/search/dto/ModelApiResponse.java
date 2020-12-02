package com.taiger.search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModelApiResponse {

    @ApiModelProperty(value = "Status code")
    @JsonProperty("code")
    private Integer code;

    @JsonProperty("type")
    @ApiModelProperty(value = "Error message")
    private String type;

}
