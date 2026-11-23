package com.studymate.studymate.document.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService{
    private final Path storageLocation;
    public LocalFileStorageService(@Value("${app.storage.location}") String storageLocation){
        this.storageLocation= Paths.get(storageLocation).toAbsolutePath().normalize();
        //Paths.get() converts the string into a Path object
        //normalize() removes unnecessary path elements such as . and ...

        try{
            Files.createDirectories(this.storageLocation); //creates directory id it doesnt exist
        }catch(IOException e) {
            throw new RuntimeException("Could not initialise file storage", e);
        }
    }

    @Override
    public String store(MultipartFile file) throws IOException{
        String storageKey= UUID.randomUUID()+ ".pdf"; //Generates a unique filename
        //Two students might upload files with the same original filename, such as notes.pdf.
        //Using UUIDs prevents filename collisions.

        Path destination= storageLocation.resolve(storageKey).normalize();
        //Combines the storage directory and generated filename.

        if(!destination.startsWith(storageLocation)){
            throw new IOException("Invalid storage path");
        }

        Files.copy(file.getInputStream(), destination); //Copies the uploaded file into your storage directory.
        return storageKey; //The method returns the storage key, which you can save in your database.
    }

    //This is a security check.
    private Path resolvePath(String storageKey) throws IOException{
        if (storageKey == null ||
                !storageKey.matches("[a-fA-F0-9-]{36}\\.pdf")) {
            throw new IOException("Invalid storage key");
        }

        Path filePath= storageLocation.resolve(storageKey).normalize();
        //startsWith(storageLocation) ensures the resolved path stays inside your storage directory.
        if(!filePath.startsWith(storageLocation)){
            throw new IOException("Invalid storage key");
        }
        return filePath;
    }

    @Override
    public byte[] load(String storageKey) throws IOException{
        Path filePath=resolvePath(storageKey);
        return Files.readAllBytes(filePath); //Finds the file and reads its contents into a byte array.
    }

    @Override
    public void delete(String storageKey) throws IOException{
        Path filePath= resolvePath(storageKey);
        Files.deleteIfExists(filePath);
    }

}
