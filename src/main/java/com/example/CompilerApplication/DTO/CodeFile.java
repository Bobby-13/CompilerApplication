package com.example.CompilerApplication.DTO;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CodeFile {
    private String fileName;
    private String filePath;
    private String jobID;
}
