package com.gntech.amrbe.service;

import com.gntech.amrbe.dto.ResponseDTO;
import com.gntech.amrbe.model.AppUser;

public interface UserService {
    ResponseDTO login(String username, String password);
    String signup(AppUser appUser);
}
