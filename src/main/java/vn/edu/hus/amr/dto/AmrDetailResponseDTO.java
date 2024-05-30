package vn.edu.hus.amr.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AmrDetailResponseDTO {
    private Long id;
    private Long treeId;
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
    private Long parentOrder;
    private Long correfId;
    private String correfPosition;

    private Boolean isAdditional;
    private String username;
    private Date updateTime;
    private String englishSense;

    public void changeToAdditionalWord() {
        this.wordId = -this.wordId;
    }

    public void changeAdditionalParent() {
        this.parentId = -this.parentId;
    }
}
