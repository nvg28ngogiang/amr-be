package vn.edu.hus.amr.dto;

import lombok.Data;

@Data
public class StatisticUserDTO {
    private Long userId;
    private String username;
    private String name;
    private Long totalParagraph;
    private Long totalAmr;
    private Long totalSentence;
}
