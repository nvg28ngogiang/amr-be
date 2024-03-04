package vn.edu.hus.amr.dto;

import lombok.Data;

@Data
public class ImportAmrRequestDTO {
    private String sentencePosition;
    private Long wordOrder;
    private Long parentWordOrder;
    private String amrLabelName;
    private Integer rowNum;
    private String wordLabel;

    private Long wordId;
    private Long parentId;
    private Long amrLabelId;
}
