package com.gntech.amrbe.controller;

import com.gntech.amrbe.dto.ResponseDTO;
import com.gntech.amrbe.dto.UserDataDTO;
import com.gntech.amrbe.model.AppUser;
import com.gntech.amrbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class AppUserController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    @PostMapping("/login")
    public ResponseDTO login(@RequestBody UserDataDTO userDataDTO) {
        return userService.login(userDataDTO.getUsername(), userDataDTO.getPassword());
    }

    @PostMapping("/signup")
    public String singup(@RequestBody AppUser appUser) {
        return userService.signup(appUser);
    }

    @GetMapping("/test")
    public String test(@AuthenticationPrincipal UserDetails userDetails) {
        return userDetails.getUsername();
    }
}
