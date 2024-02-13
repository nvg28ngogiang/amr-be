package vn.edu.hus.amr.dto;

import lombok.Data;

@Data
public class AmrDetailResponseDTO {
    private Long id;
    private Long wordId;
    private Long parentId;
    private String wordContent;
    private Long amrLabelId;
    private String amrLabelContent;
    private String wordLabel;
    private Long wordSenseId;

    private String posLabel;
    private String wordSense;

    private String sentencePosition;

    private Long wordOrder;
    private Long correfId;
    private String correfPosition;

    private Boolean isAdditional;

    public void changeToAdditionalWord() {
        this.wordId = -this.wordId;
    }

    public void changeAdditionalParent() {
        this.parentId = -this.parentId;
    }
}
