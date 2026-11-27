package com.studymate.studymate.note.service;

import com.studymate.studymate.conversation.entity.Message;
import com.studymate.studymate.conversation.entity.MessageRole;
import com.studymate.studymate.conversation.repository.MessageRepository;
import com.studymate.studymate.note.dto.CreateNoteRequest;
import com.studymate.studymate.note.dto.NoteResponse;
import com.studymate.studymate.note.dto.UpdateNoteRequest;
import com.studymate.studymate.note.entity.Note;
import com.studymate.studymate.note.repository.NoteRepository;
import com.studymate.studymate.user.entity.User;
import com.studymate.studymate.workspace.entity.Workspace;
import com.studymate.studymate.workspace.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final WorkspaceRepository workspaceRepository;
    private final MessageRepository messageRepository;

    public NoteService(NoteRepository noteRepository, WorkspaceRepository workspaceRepository, MessageRepository messageRepository) {
        this.noteRepository = noteRepository;
        this.workspaceRepository = workspaceRepository;
        this.messageRepository = messageRepository;
    }

    private NoteResponse toResponse(Note note){
        Long sourceMessageId = note.getSourceMessage()==null? null: note.getSourceMessage().getId();

        return new NoteResponse(
                note.getId(),
                note.getWorkspace().getId(),
                sourceMessageId,
                note.getTitle(),
                note.getContent(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }

    //manual note creation
    public NoteResponse createNote(Long workspaceID, User user, CreateNoteRequest request){
        Workspace workspace= workspaceRepository.findByIdAndUser_Id(workspaceID,user.getId()).orElseThrow(()-> new IllegalStateException("Workspace not found"));
        Note note= new Note(workspace,request.title(),request.content(),null);
        note=noteRepository.save(note);
        return toResponse(note);
    }

    @Transactional
    public NoteResponse savedAiResponse(Long messageId, User user, String title){
        Message message= messageRepository.findByIdAndConversation_Workspace_User_Id(messageId,user.getId()).orElseThrow(()-> new IllegalStateException("Message not found"));

        if(message.getRole() != MessageRole.ASSISTANT){
            throw new IllegalStateException("Only assisstant messages can be saved as notes");
        }
        Workspace workspace= message.getConversation().getWorkspace();

        Note note= new Note(workspace,title,message.getContent(),message); //source message
        note= noteRepository.save(note);
        return toResponse(note);
    }

    //get all notes from workspace id
    @Transactional(readOnly = true)
    public List<NoteResponse> getNotes(Long workspaceId, User user){
        workspaceRepository.findByIdAndUser_Id(workspaceId, user.getId()).orElseThrow(()-> new IllegalStateException("WOrkspace not found"));
        return noteRepository.findAllByWorkspace_IdOrderByUpdatedAtDesc(workspaceId)
                .stream().map(this::toResponse).toList();
    }

    //get a particular note from note id
    @Transactional(readOnly = true)
    public NoteResponse getNote(Long noteId, User user){
        Note note= noteRepository.findByIdAndWorkspace_User_Id(noteId, user.getId()).orElseThrow(()-> new IllegalStateException("Note not found"));
        return toResponse(note);
    }

    @Transactional(readOnly = true)
    public NoteResponse updateNote(Long noteId, User user, UpdateNoteRequest request){
        Note note= noteRepository.findByIdAndWorkspace_User_Id(noteId, user.getId()).orElseThrow(()-> new IllegalStateException("Note not found"));
        note.update(request.title(), request.content());

        return toResponse(note);
    }

    @Transactional
    public void deleteNote(Long noteId, User user){
        Note note= noteRepository.findByIdAndWorkspace_User_Id(noteId, user.getId()).orElseThrow(()-> new IllegalStateException("Note not found"));
        noteRepository.delete(note);
    }
}
