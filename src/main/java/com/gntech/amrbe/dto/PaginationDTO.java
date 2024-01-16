package com.gntech.amrbe.dto;

import lombok.Data;

@Data
public class PaginationDTO {
    private Integer first;
    private Integer rows;
    private Long totalRecords;
}
