package com.example.CompilerApplication.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupportedLanguageInfo {
    private String language;
    private String info;

}
