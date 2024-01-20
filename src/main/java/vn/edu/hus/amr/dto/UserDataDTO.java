package vn.edu.hus.amr.dto;

import vn.edu.hus.amr.model.AppUserRole;
import lombok.Data;

import java.util.List;

@Data
public class UserDataDTO {
    private Long id;
    private String username;
    private String password;
    private List<AppUserRole> roles;
    private String token;
}
