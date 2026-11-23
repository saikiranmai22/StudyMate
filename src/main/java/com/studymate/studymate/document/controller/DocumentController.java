package com.studymate.studymate.document.controller;

import com.studymate.studymate.auth.security.CustomUserDetails;
import com.studymate.studymate.document.dto.DocumentResponse;
import com.studymate.studymate.document.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/documents")
public class DocumentController {
    private final DocumentService documentService;
    public DocumentController(DocumentService documentService){
        this.documentService=documentService;
    }

    @PostMapping(consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> uploadDocument(
            @PathVariable Long workspaceId,
            @RequestParam("file")MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails principal
            ){

        DocumentResponse response= documentService.uploadDocument(workspaceId, principal.getUser(), file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getDocuments(@PathVariable Long workspaceId, @AuthenticationPrincipal CustomUserDetails principal){
        List<DocumentResponse> documents= documentService.getDocuments(workspaceId,principal.getUser());
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentResponse> getDocument(
            @PathVariable Long workspaceId,
            @PathVariable Long documentId,
            @AuthenticationPrincipal CustomUserDetails principal
    ){
        DocumentResponse response= documentService.getDocument(workspaceId,documentId,principal.getUser());
        return ResponseEntity.ok(response);
    }
}
