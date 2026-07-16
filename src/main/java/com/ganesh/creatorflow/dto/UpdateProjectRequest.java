package com.ganesh.creatorflow.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class UpdateProjectRequest {
    
    @NotBlank(message = "Title must not be blank")
    @Size(min = 3, max = 100, message = "Title length must be between 3 and 100 characters")
    private String title;
    
    @NotBlank(message = "Description must not be blank")
    @Size(min = 10, max = 1000, message = "Description length must be between 10 and 1000 characters")
    private String description;
}
