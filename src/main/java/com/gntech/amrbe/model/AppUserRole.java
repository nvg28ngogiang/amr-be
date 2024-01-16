package com.gntech.amrbe.model;

import org.springframework.security.core.GrantedAuthority;

public enum AppUserRole implements GrantedAuthority {
    ADMIN, STUDENT;

    @Override
    public String getAuthority() {
        return name();
    }
}
