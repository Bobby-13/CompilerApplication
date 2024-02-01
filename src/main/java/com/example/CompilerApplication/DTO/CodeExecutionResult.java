package com.example.CompilerApplication.DTO;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CodeExecutionResult {
    private String output;
    private String error;
//    private String language;
//    private String info;
    private String exeTime;
//    private String outputExt;
//    private String StorageCapacity;

}
