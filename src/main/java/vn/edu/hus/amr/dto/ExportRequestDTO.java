package vn.edu.hus.amr.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExportRequestDTO {
    private List<Long> userIds;
}
