package com.studymate.studymate.document.service;

import com.studymate.studymate.document.dto.ExtractedPage;
import com.studymate.studymate.document.entity.Document;
import com.studymate.studymate.document.entity.DocumentChunk;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentChunkingService {
    private static final int CHUNK_SIZE=1000;
    private static final int CHUNK_OVERLAP= 150;

    //This searches backwards from the proposed chunk ending to find the nearest whitespace.
    private int findLastWhitespace(String text, int start, int end){
        for(int i=end-1;i>start;i--){
            if(Character.isWhitespace(text.charAt(i))){ //We try to end at the nearest suitable whitespace instead.
                return i;
            }
        }
        return -1;
    }

    public List<DocumentChunk> createChunks(Document document, List<ExtractedPage> pages){
        List<DocumentChunk> chunks = new ArrayList<>();
        int chunkIndex=0;

        for(ExtractedPage page:pages){ //process each page- retain original pdf numbers
            String text = page.text();
            if(text ==null || text.isBlank()){ //skip empty pages
                continue;
            }
            text= text.trim();

            int start=0;
            while(start<text.length()) {
                int end = Math.min(start + CHUNK_SIZE, text.length());

                if (end < text.length()) {
                    int boundary = findLastWhitespace(text, start, end);
                    if (boundary > start + CHUNK_SIZE / 2) {//We are willing to split a word if finding a suitable space would make the chunk less than half its intended size.
                        end = boundary;
                    }
                }
                String chunkContent= text.substring(start,end).trim(); //extract the chunk
                if(!chunkContent.isBlank()){
                    //Since we process one page at a time, both page numbers are the same.
                    DocumentChunk chunk= new DocumentChunk(document,chunkIndex,page.pageNumber(),page.pageNumber(),chunkContent,null );
                    chunks.add(chunk);
                    chunkIndex++;
                }

                if (end>=text.length()){ //move to the next chunk
                    break;
                }

                start=Math.max(start+1, end-CHUNK_OVERLAP); //This is where overlap happens.
                //(1000-150) The next chunk begins at character 850
                //So characters 850–999 are repeated in the next chunk.
            }
        }
        return chunks;
    }
}
