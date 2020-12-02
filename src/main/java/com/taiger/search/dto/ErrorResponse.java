package com.taiger.search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {

    @ApiModelProperty(value = "Status code")
    @JsonProperty("code")
    private Integer code;

    @JsonProperty("type")
    @ApiModelProperty(value = "Error message")
    private String type;

    @JsonProperty("message")
    @ApiModelProperty(value = "Error message")
    private String message;

    @JsonProperty("debug")
    @ApiModelProperty(value = "Details of the error")
    private String debug;

    @JsonProperty("timestamp")
    @ApiModelProperty(value = "When was the error.")
    private Date timestamp;

    @JsonProperty("path")
    @ApiModelProperty(value = "Where was the error.")
    private String path;
}
