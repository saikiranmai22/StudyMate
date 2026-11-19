package com.studymate.studymate.workspace.repository;

import com.studymate.studymate.workspace.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> { //Entity type (	Which entity/table this repository manages) and primary key type
    List<Workspace> findAllByUser_IdOrderByUpdatedAtDesc(Long userId);
    //Follow the user field and check its id property.

    Optional<Workspace> findByIdAndUser_Id(Long workspaceId, Long userId);

}
