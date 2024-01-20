package vn.edu.hus.amr.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FormResult extends PaginationDTO {
    List<?> content = new ArrayList<>();
}
