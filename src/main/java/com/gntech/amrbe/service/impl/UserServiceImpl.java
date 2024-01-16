package com.gntech.amrbe.service.impl;

import com.gntech.amrbe.dto.ResponseDTO;
import com.gntech.amrbe.dto.UserDataDTO;
import com.gntech.amrbe.exception.CustomException;
import com.gntech.amrbe.model.AppUser;
import com.gntech.amrbe.repository.UserRepository;
import com.gntech.amrbe.security.JwtTokenProvider;
import com.gntech.amrbe.service.UserService;
import com.gntech.amrbe.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public ResponseDTO login(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            AppUser appUser = userRepository.findByUsername(username);
            UserDataDTO userDataDTO = mapUserToResponse(appUser);

            return new ResponseDTO(Constants.RESPONSE_STATUS.OK, Constants.STATUS_CODE.SUCCESS, "success", userDataDTO);
        } catch (AuthenticationException e) {
            return new ResponseDTO(Constants.RESPONSE_STATUS.ERROR, Constants.STATUS_CODE.ERROR, "Username or password is invalid", null);
        }
    }

    private UserDataDTO mapUserToResponse(AppUser appUser) {
        UserDataDTO userDataDTO = new UserDataDTO();
        userDataDTO.setId(appUser.getId());
        userDataDTO.setUsername(appUser.getUsername());
        userDataDTO.setRoles(appUser.getRoles());
        userDataDTO.setToken("Bearer " + jwtTokenProvider.createToken(appUser.getUsername(), appUser.getRoles()));

        return userDataDTO;
    }
    public String signup(AppUser appUser) {
        if (!userRepository.existsByUsername(appUser.getUsername())) {
            appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
            userRepository.save(appUser);
            return jwtTokenProvider.createToken(appUser.getUsername(), appUser.getRoles());
        } else {
            throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
