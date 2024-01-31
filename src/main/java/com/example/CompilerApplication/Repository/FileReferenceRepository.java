package com.example.CompilerApplication.Repository;

import com.example.CompilerApplication.Entity.FileReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileReferenceRepository extends JpaRepository<FileReference,Integer> {
}
