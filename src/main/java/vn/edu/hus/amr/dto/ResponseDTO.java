package vn.edu.hus.amr.dto;

import lombok.Data;

@Data
public class ResponseDTO {
    private Integer status;
    private String errorCode;
    private String message;
    private Object data;
    private Object errors;

    public ResponseDTO() {
    }

    public ResponseDTO(Integer status, String errorCode, String message, Object data, Object errors) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
        this.errors = errors;
    }

    public ResponseDTO(Integer status, String errorCode, String message, Object data) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
    }
}
