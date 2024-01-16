package com.gntech.amrbe.dto;

import com.gntech.amrbe.model.PosLabel;
import lombok.Data;

@Data
public class SentenceDetailDTO {
    private Long id;
    private Long divId;
    private Long paragraphId;
    private Long sentenceId;
    private Long wordOrder;
    private String content;
    private String posLabel;
}
