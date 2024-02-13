package vn.edu.hus.amr.dto;

import lombok.Data;

@Data
public class AmrNode {
    private Long id;
    private Long parentId;
    private Long amrLabelId;
    private String wordLabel;

    private Long wordId;
    private Long wordSenseId;
    private String wordContent;
    private String posLabel;

    private Long correfId;
    private String correfPosition;

    public boolean isAdditionalWord() {
        return this.wordId < 0;
    }
}
