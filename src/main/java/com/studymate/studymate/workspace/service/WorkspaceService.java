package com.studymate.studymate.workspace.service;

import com.studymate.studymate.user.entity.User;
import com.studymate.studymate.workspace.dto.WorkspaceRequest;
import com.studymate.studymate.workspace.dto.WorkspaceResponse;
import com.studymate.studymate.workspace.entity.Workspace;
import com.studymate.studymate.workspace.repository.WorkspaceRepository;

import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class WorkspaceService{
    private final WorkspaceRepository workspaceRepository;
    public WorkspaceService(WorkspaceRepository workspaceRepository){
        this.workspaceRepository=workspaceRepository;
    }

    //convert entity to dto
    //Your database entity may contain internal details such as the owner relationship. The frontend only needs the workspace's ID, name and description.
    private WorkspaceResponse toResponse(Workspace workspace){
        return new WorkspaceResponse(workspace.getId(), workspace.getName(), workspace.getDescription());
    }

    //create workspace
    @Transactional
    public WorkspaceResponse createWorkspace( //Receive the authenticated user (owner) and workspace details (request).
            User owner,
            WorkspaceRequest request
    ){
        //every workspace must belong to a user. The owner is used to populate the user_id foreign key in the workspaces table.
        Workspace workspace= new Workspace(owner,request.name().trim(), request.description());
        Workspace savedWorkspace= workspaceRepository.save(workspace);

        return toResponse(savedWorkspace);
    }

    @Transactional(readOnly= true)
    public List<WorkspaceResponse> getAllWorkspaces(User owner){
        List<Workspace> workspaces= workspaceRepository.findAllByUser_IdOrderByUpdatedAtDesc(owner.getId());
        return workspaces.stream().map(this::toResponse).toList(); //This converts every Workspace entity into a WorkspaceResponse.
    }

    //checks the ownership- important for security
    private Workspace findOwnedWorkspace(Long workspaceId, User owner){
        return workspaceRepository.findByIdAndUser_Id(workspaceId,owner.getId())
                .orElseThrow(()->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found"));
    }

    @Transactional(readOnly=true)
    public WorkspaceResponse getWorkspace(Long workspaceId, User owner){
        Workspace workspace = findOwnedWorkspace(workspaceId, owner);
        return toResponse(workspace);
    }

    //update workspace
    @Transactional
    public WorkspaceResponse updateWorkspace(Long workspaceId, User owner, WorkspaceRequest request){
        Workspace workspace= findOwnedWorkspace(workspaceId, owner);

        workspace.updateDetails(request.name().trim(), request.description());
        Workspace updatedWorkspace= workspaceRepository.save(workspace);
        return toResponse(updatedWorkspace);
    }
}
