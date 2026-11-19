package com.studymate.studymate.document.entity;

public enum DocumentStatus {
    PROCESSING, //The PDF has been uploaded and is being extracted and indexed.
    COMPLETED, //Text extraction and embedding generation succeeded.
    FAILED //Processing encountered an error.
}
