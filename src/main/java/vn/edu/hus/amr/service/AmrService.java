package vn.edu.hus.amr.service;

import vn.edu.hus.amr.dto.AmrDetailRequestDTO;
import vn.edu.hus.amr.dto.ResponseDTO;

public interface AmrService {
    ResponseDTO getAmrDetail(String username, Long treeId);

    ResponseDTO saveOrUpdateAmrTree(String username, AmrDetailRequestDTO input);

    ResponseDTO getAmrLabels();
}
