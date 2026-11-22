package com.studymate.studymate.workspace.dto;

public record WorkspaceResponse(
        Long id,
        String name,
        String description
) {
}
//Why not return the Workspace entity directly?
//the entity contains a relationship with User.
// We only want to expose the workspace information needed by the frontend, not the entire associated user object.
