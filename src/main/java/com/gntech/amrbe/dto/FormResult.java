package com.gntech.amrbe.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FormResult extends PaginationDTO {
    List<?> listData = new ArrayList<>();
}
