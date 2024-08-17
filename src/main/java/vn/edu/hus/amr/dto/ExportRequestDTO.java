package vn.edu.hus.amr.dto;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Data
public class ExportRequestDTO {
    private Integer role;
    private Integer status;
    private List<Long> userIds;
}
