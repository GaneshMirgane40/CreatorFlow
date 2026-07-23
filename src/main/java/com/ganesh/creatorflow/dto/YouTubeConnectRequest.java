package com.ganesh.creatorflow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YouTubeConnectRequest {

    @NotBlank
    private String code;

}