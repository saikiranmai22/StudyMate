package com.studymate.studymate.workspace.controller;

import com.studymate.studymate.auth.security.CustomUserDetails;
import com.studymate.studymate.workspace.dto.WorkspaceRequest;
import com.studymate.studymate.workspace.dto.WorkspaceResponse;
import com.studymate.studymate.workspace.service.WorkspaceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    public WorkspaceController(WorkspaceService workspaceService){
        this.workspaceService=workspaceService;
    }

    @PostMapping
    public ResponseEntity<WorkspaceResponse> createWorkspace(
            @Valid @RequestBody WorkspaceRequest request,
            @AuthenticationPrincipal CustomUserDetails principal //Give me the currently authenticated user's CustomUserDetails object.
            ){
        WorkspaceResponse response= workspaceService.createWorkspace(principal.getUser(),request); //principal.getUser():gives you the original User entity.

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<WorkspaceResponse>> getAllWorkspaces(@AuthenticationPrincipal CustomUserDetails principal){
        List<WorkspaceResponse> response= workspaceService.getAllWorkspaces(principal.getUser());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceResponse> getWorkspace(@PathVariable Long workspaceId, @AuthenticationPrincipal CustomUserDetails principal){
        WorkspaceResponse response= workspaceService.getWorkspace(workspaceId, principal.getUser());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceResponse> updateWorkspace(@PathVariable Long workspaceId, @Valid @RequestBody WorkspaceRequest request, @AuthenticationPrincipal CustomUserDetails principal){
        WorkspaceResponse response= workspaceService.updateWorkspace(workspaceId,principal.getUser(),request);
        return ResponseEntity.ok(response);
    }
}
