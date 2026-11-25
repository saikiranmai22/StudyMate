package com.studymate.studymate.document.service;

import com.studymate.studymate.document.dto.DocumentResponse;
import com.studymate.studymate.document.dto.PdfExtractionResult;
import com.studymate.studymate.document.entity.Document;
import com.studymate.studymate.document.entity.DocumentChunk;
import com.studymate.studymate.document.entity.DocumentStatus;
import com.studymate.studymate.document.repository.DocumentChunkRepository;
import com.studymate.studymate.document.repository.DocumentRepository;
import com.studymate.studymate.user.entity.User;
import com.studymate.studymate.workspace.entity.Workspace;
import com.studymate.studymate.workspace.repository.WorkspaceRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final WorkspaceRepository workspaceRepository;
    private final FileStorageService fileStorageService;
    private final PdfProcessingService pdfProcessingService;
    private final DocumentChunkingService documentChunkingService;
    private final DocumentChunkRepository documentChunkRepository;
    private final DocumentEmbeddingService documentEmbeddingService;

    private static final long MAX_FILE_SIZE= 20L * 1024*1024;

    public DocumentService(DocumentRepository documentRepository, WorkspaceRepository workspaceRepository, FileStorageService fileStorageService, PdfProcessingService pdfProcessingService,DocumentChunkingService documentChunkingService,
                           DocumentChunkRepository documentChunkRepository, DocumentEmbeddingService documentEmbeddingService) {
        this.documentRepository = documentRepository;
        this.workspaceRepository = workspaceRepository;
        this.fileStorageService = fileStorageService;
        this.pdfProcessingService=pdfProcessingService;
        this.documentChunkingService = documentChunkingService;
        this.documentChunkRepository = documentChunkRepository;
        this.documentEmbeddingService=documentEmbeddingService;
    }

    private void validatePdf(MultipartFile file){

        if(file==null || file.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Please upload a file");
        }

        if(file.getSize()>MAX_FILE_SIZE){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size cannot exceed 20MB");
        }

        String filename= file.getOriginalFilename();
        if(filename==null || !filename.toLowerCase().endsWith(".pdf")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PDF files are allowed");
        }

        String contentType= file.getContentType();
        if(contentType==null || !contentType.equalsIgnoreCase("application/pdf")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file type. Please upload a PDF");
        }
    }

    private DocumentResponse toResponse(Document document) {

        return new DocumentResponse(
                document.getId(),
                document.getOriginalFilename(),
                document.getFileSizeBytes(),
                document.getPageCount(),
                document.getStatus(),
                document.getCreatedAt()
        );
    }

    @Transactional
    public DocumentResponse uploadDocument(Long workspaceId, User owner, MultipartFile file){
        Workspace workspace= workspaceRepository.findByIdAndUser_Id(workspaceId, owner.getId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found"));

        validatePdf(file);

        try{
            byte[] pdfBytes=file.getBytes(); //Read uploaded file bytes
            PdfExtractionResult extractionResult=pdfProcessingService.extractText(pdfBytes); //Extract and validate PDF contents
            //If the PDF is corrupt or unsupported, we reject it before saving it to local storage or creating a database record. This avoids storing invalid uploads.

            //store the pdf
            String storageKey= fileStorageService.store(file);

            try{
                //create the document metadata
                Document document= new Document(workspace, file.getOriginalFilename(), storageKey, file.getSize(), DocumentStatus.PROCESSING);
                document.setPageCount(extractionResult.pageCount());

                Document savedDocument = documentRepository.save(document);
                List<DocumentChunk> chunks = documentChunkingService.createChunks(savedDocument, extractionResult.pages());

                // Generate embeddings for all chunks
                documentEmbeddingService.generateEmbeddings(savedDocument, chunks);
                documentChunkRepository.saveAll(chunks);
                // Save document's embedding metadata and COMPLETED status
                documentRepository.save(savedDocument);

                return toResponse(savedDocument);
            }catch (RuntimeException e){
                //If file storage succeeded but the database operation failed,
                // attempt to remove the stored file.
                try{
                    fileStorageService.delete(storageKey);
                }catch (IOException cleanupException){
                    e.addSuppressed(cleanupException);
                }
                throw e;
            }

        }catch (IOException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not read or store uploaded PDF", e);
        }
    }

    private Workspace findOwnedWorkspace(Long workspaceId, User ownner){
        return workspaceRepository.findByIdAndUser_Id(workspaceId,ownner.getId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found"));
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> getDocuments(Long workspaceId, User owner){
        Workspace workspace= findOwnedWorkspace(workspaceId, owner);

        return documentRepository.findAllByWorkspace_IdOrderByCreatedAtDesc(workspace.getId()).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DocumentResponse getDocument(Long workspaceId, Long documentId, User owner){
        Workspace workspace= findOwnedWorkspace(workspaceId, owner);
        //Verify that the document belongs to this workspace
        Document document=documentRepository.findByIdAndWorkspace_Id(documentId, workspace.getId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
        return toResponse(document);
    }
}
