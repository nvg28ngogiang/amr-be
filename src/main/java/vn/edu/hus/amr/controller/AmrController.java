package vn.edu.hus.amr.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import vn.edu.hus.amr.dto.AmrDetailRequestDTO;
import vn.edu.hus.amr.dto.ResponseDTO;
import vn.edu.hus.amr.service.AmrService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AmrController {
    private final AmrService amrService;

    @GetMapping("/amrs/{id}")
    public ResponseDTO getAmrDetail(@AuthenticationPrincipal UserDetails userDetails,
                                    @PathVariable (name = "id") Long id) {
        return amrService.getAmrDetail(userDetails.getUsername(), id);
    }

    @PutMapping("/amr")
    public ResponseDTO saveOrUpdateAmrTree(@AuthenticationPrincipal UserDetails userDetails,
                                           @RequestBody AmrDetailRequestDTO input) {
        return amrService.saveOrUpdateAmrTree(userDetails.getUsername(), input);
    }

    @GetMapping("/amr/labels")
    public ResponseDTO getAmrLabels() {
        return amrService.getAmrLabels();
    }
}
