package com.gntech.amrbe.dto;

import lombok.Data;

@Data
public class SentenceDTO {
    private Long divId;
    private Long paragraphId;
    private Long sentenceId;
    private String content;
}
