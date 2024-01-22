package vn.edu.hus.amr.dto;

import lombok.Data;

import java.util.List;

@Data
public class AmrDetailRequestDTO {
    private Long divId;
    private Long paragraphId;
    private Long sentenceId;
    private Long amrTreeId;
    private String amrTreeName;

    private List<AmrNode> nodes;
}
