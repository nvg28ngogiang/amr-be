package vn.edu.hus.amr.service;

import vn.edu.hus.amr.dto.ResponseDTO;
import vn.edu.hus.amr.model.AppUser;

public interface UserService {
    ResponseDTO login(String username, String password);
    String signup(AppUser appUser);

    ResponseDTO getUsers();

    ResponseDTO getUserInfo(String username);
}
