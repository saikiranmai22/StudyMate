package com.studymate.studymate.note.repository;

import com.studymate.studymate.note.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findAllByWorkspace_IdOrderByUpdatedAtDesc(Long workspaceId);
    //Retrieve all notes in a workspace, most recently edited first.

    Optional<Note> findByIdAndWorkspace_Id(Long noteId, Long workspaceId);
    //Retrieve a particular note only if it belongs to the specified workspace.
}
