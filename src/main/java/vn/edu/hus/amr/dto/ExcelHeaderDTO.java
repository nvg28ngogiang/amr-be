package vn.edu.hus.amr.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExcelHeaderDTO {
    private String name;
    private int size;
    private String comment;
    private String titleName;
    private Integer index;

    public ExcelHeaderDTO(String name, int size) {
        this.name = name;
        this.size = size;
    }

}
