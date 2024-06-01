package vn.edu.hus.amr.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WordSenseDTO {

    private Long id;

    private String wordContent;

    private String sense;

    private String example;

}
