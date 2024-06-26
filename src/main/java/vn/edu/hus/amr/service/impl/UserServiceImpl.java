package vn.edu.hus.amr.service.impl;

import vn.edu.hus.amr.dto.FormResult;
import vn.edu.hus.amr.dto.ResponseDTO;
import vn.edu.hus.amr.dto.UserDataDTO;
import vn.edu.hus.amr.exception.CustomException;
import vn.edu.hus.amr.model.AppUser;
import vn.edu.hus.amr.repository.UserRepository;
import vn.edu.hus.amr.security.JwtTokenProvider;
import vn.edu.hus.amr.service.UserService;
import vn.edu.hus.amr.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
            UserDataDTO userDataDTO = mapUserToResponseWithToken(appUser);

            return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, "success", userDataDTO);
        } catch (AuthenticationException e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO(HttpStatus.UNAUTHORIZED.value(), Constants.STATUS_CODE.ERROR, "Username or password is invalid", null);
        }
    }

    private UserDataDTO mapUserToResponseWithToken(AppUser appUser) {
        UserDataDTO userDataDTO = mapUserToResponseNoToken(appUser);
        userDataDTO.setToken("Bearer " + jwtTokenProvider.createToken(appUser.getUsername(), appUser.getRoles()));
        return userDataDTO;
    }

    private UserDataDTO mapUserToResponseNoToken(AppUser appUser) {
        UserDataDTO userDataDTO = new UserDataDTO();
        userDataDTO.setId(appUser.getId());
        userDataDTO.setUsername(appUser.getUsername());
        userDataDTO.setRoles(appUser.getRoles());
        userDataDTO.setName(appUser.getName());
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

    @Override
    public ResponseDTO getUsers() {
        try {
            List<AppUser> listData = userRepository.findAll();
            List<UserDataDTO> listDataResponse = listData.stream().map(
                    this::mapUserToResponseNoToken
            ).collect(Collectors.toList());

            FormResult formResult = new FormResult();
            formResult.setContent(listDataResponse);
            formResult.setTotalElements(Long.valueOf(listDataResponse.size()));
            return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, "Success", formResult);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public ResponseDTO getUserInfo(String username) {
        try {
            AppUser appUser = userRepository.findByUsername(username);
            UserDataDTO userDataDTO = mapUserToResponseNoToken(appUser);
            return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, "Success", userDataDTO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }
}
