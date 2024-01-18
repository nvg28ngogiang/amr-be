package com.gntech.amrbe.controller;

import com.gntech.amrbe.dto.AmrDetailRequestDTO;
import com.gntech.amrbe.dto.ResponseDTO;
import com.gntech.amrbe.service.AmrService;
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

    @GetMapping("/amr-labels")
    public ResponseDTO getAmrLabels() {
        return amrService.getAmrLabels();
    }
}
