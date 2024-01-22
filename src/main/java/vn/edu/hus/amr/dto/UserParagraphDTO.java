package vn.edu.hus.amr.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserParagraphDTO {
    private Long divId;
    private Long paragraphId;
    private List<UserDataDTO> users;
}
