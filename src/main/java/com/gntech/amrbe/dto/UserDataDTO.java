package com.gntech.amrbe.dto;

import com.gntech.amrbe.model.AppUserRole;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class UserDataDTO {
    private Long id;
    private String username;
    private String password;
    private List<AppUserRole> roles;
    private String token;
}
