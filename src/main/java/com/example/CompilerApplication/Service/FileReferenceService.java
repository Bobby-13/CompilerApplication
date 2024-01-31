package com.example.CompilerApplication.Service;

import com.example.CompilerApplication.Entity.FileReference;
import com.example.CompilerApplication.Repository.FileReferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileReferenceService {

    @Autowired
    private FileReferenceRepository repository;

    public boolean AddReference(String id, String jobID) {

        FileReference reference = FileReference.builder()
                .uid(id)
                .fileId(jobID)
                .build();

        FileReference save = repository.save(reference);

        return true;
    }
}
