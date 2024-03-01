package vn.edu.hus.amr.dto;

import vn.edu.hus.amr.model.PosLabel;
import lombok.Data;

@Data
public class WordRequestDTO {
    private PosLabel posLabel;
}
