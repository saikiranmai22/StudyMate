package com.studymate.studymate.workspace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//This defines what the frontend can send when creating or updating a workspace.
//DTOs let us control exactly which fields leave the backend.
public record WorkspaceRequest(
        @NotBlank(message = "Workspace name is required")
        @Size(max= 100, message = "Name cannot exceed 100 characters")
        String name,

        @Size(max = 500, message = "Name cannot exceed 500 characters")
        String description
) {

}
