package com.example.CompilerApplication.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupportedLanguagesDTO {
    private List<SupportedLanguageInfo> supportedLanguages;

}
