package com.studymate.studymate.document.service;

import com.studymate.studymate.document.dto.ExtractedPage;
import com.studymate.studymate.document.dto.PdfExtractionResult;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfProcessingService {

    //A real PDF normally starts with:%PDF-
    //Someone could rename notes.txt to notes.pdf. Checking the extension alone would incorrectly accept it.
    private boolean hasPdfSignature(byte[] bytes){
        if(bytes.length<5){
            return false;
        }

        return bytes[0]=='%' && bytes[1] == 'P' && bytes[2] == 'D' && bytes[3] == 'F'
                && bytes[4] == '-';
    }

    public PdfExtractionResult extractText(byte[] pdfBytes){
        if(pdfBytes==null || pdfBytes.length==0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PDF file is empty");
        }

        if(!hasPdfSignature(pdfBytes)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid PDF file");
        }

        //PDDocument represents the opened PDF.
        try(PDDocument document= Loader.loadPDF(pdfBytes)){ //opening the pdf
            if(document.isEncrypted()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password-protected PDFs are not supported");
            }
            int pageCount=document.getNumberOfPages();
            if(pageCount==0){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"PDF contains no pages");
            }

            PDFTextStripper stripper= new PDFTextStripper();
            List<ExtractedPage> extractedPages= new ArrayList<>();
            boolean hasExtractableText=false;

            for(int page=1;page<=pageCount;page++){
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                String text=stripper.getText(document).trim();

                if(!text.isBlank()){
                    hasExtractableText=true;
                }
                extractedPages.add(new ExtractedPage(page,text));
            }

            if(!hasExtractableText){//no scanned pdfs
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No extractable text found. Scanned PDFs are not supported yet.");
            }
            return new PdfExtractionResult(pageCount,extractedPages);
        }catch (ResponseStatusException e){
            throw e;
        }catch (IOException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"PDF is corrupted, invalid, or cannot be processed");
        }
    }
}
