package com.studymate.studymate.note.controller;

import com.studymate.studymate.auth.security.CustomUserDetails;
import com.studymate.studymate.note.dto.CreateNoteRequest;
import com.studymate.studymate.note.dto.NoteResponse;
import com.studymate.studymate.note.dto.UpdateNoteRequest;
import com.studymate.studymate.note.service.NoteService;
import jakarta.validation.Valid;
import org.aspectj.weaver.ast.Not;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService){
        this.noteService=noteService;
    }

    @PostMapping("/workspaces/{workspaceId}/notes")
    public NoteResponse createNote(@PathVariable Long workspaceId, @AuthenticationPrincipal CustomUserDetails principal, @Valid @RequestBody CreateNoteRequest request){
        return noteService.createNote(workspaceId, principal.getUser(),request);
    }

    @PostMapping("/messages/{messageId}/save-note")
    public NoteResponse saveAiResponse(@PathVariable Long messageId, @AuthenticationPrincipal CustomUserDetails principal, @RequestParam String title){
        return noteService.savedAiResponse(messageId, principal.getUser(),title);
    }

    @GetMapping("/workspaces/{workspaceId}/notes")
    public List<NoteResponse> getNotes(@PathVariable Long workspaceId, @AuthenticationPrincipal CustomUserDetails principal){
        return noteService.getNotes(workspaceId,principal.getUser());
    }

    @GetMapping("/notes/{noteId}")
    public NoteResponse getNote(@PathVariable Long noteId, @AuthenticationPrincipal CustomUserDetails principal){
        return noteService.getNote(noteId, principal.getUser());
    }

    @PutMapping("/notes/{noteId}")
    public NoteResponse updateNote(@PathVariable Long noteId, @AuthenticationPrincipal CustomUserDetails principal, @Valid @RequestBody UpdateNoteRequest request ){
        return noteService.updateNote(noteId, principal.getUser(),request);
    }

    @DeleteMapping("/notes/{noteId}")
    public void deleteNote(@PathVariable Long noteId, @AuthenticationPrincipal CustomUserDetails principal){
        noteService.deleteNote(noteId,principal.getUser());
    }
}
