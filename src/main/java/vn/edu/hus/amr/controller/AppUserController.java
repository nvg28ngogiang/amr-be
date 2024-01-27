package vn.edu.hus.amr.controller;

import vn.edu.hus.amr.dto.ResponseDTO;
import vn.edu.hus.amr.dto.UserDataDTO;
import vn.edu.hus.amr.model.AppUser;
import vn.edu.hus.amr.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/get-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDTO getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/info")
    public ResponseDTO getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getUserInfo(userDetails.getUsername());
    }
}
