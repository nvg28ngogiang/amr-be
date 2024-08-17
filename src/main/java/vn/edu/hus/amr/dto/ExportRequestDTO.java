package vn.edu.hus.amr.dto;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Data
public class ExportRequestDTO {
    private Integer role;
    private Integer status;
    private List<Long> userIds;

    public Integer getRole() {
        if (role == null) {
            // nguoi thuc hien
            role = 1;
        }
        return role;
    }

    public Integer getStatus() {
        if (status == null) {
            // da hoan thanh
            status = 3;
        }
        return status;
    }
}
