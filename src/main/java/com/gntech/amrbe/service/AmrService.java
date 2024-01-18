package com.gntech.amrbe.service;

import com.gntech.amrbe.dto.AmrDetailRequestDTO;
import com.gntech.amrbe.dto.ResponseDTO;

public interface AmrService {
    ResponseDTO getAmrDetail(String username, Long treeId);

    ResponseDTO saveOrUpdateAmrTree(String username, AmrDetailRequestDTO input);

    ResponseDTO getAmrLabels();
}
