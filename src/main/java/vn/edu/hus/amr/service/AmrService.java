package vn.edu.hus.amr.service;

import org.springframework.web.multipart.MultipartFile;
import vn.edu.hus.amr.dto.AmrDetailRequestDTO;
import vn.edu.hus.amr.dto.ExportRequestDTO;
import vn.edu.hus.amr.dto.ResponseDTO;

public interface AmrService {
    ResponseDTO getAmrDetail(String username, Long treeId);

    ResponseDTO saveOrUpdateAmrTree(String username, AmrDetailRequestDTO input);

    ResponseDTO getAmrLabels();

    String exportExcelFile(String username, ExportRequestDTO input);

    String exportDocumentFile(String username, ExportRequestDTO input);

    ResponseDTO statisticUsers();

    ResponseDTO importInsert(MultipartFile excelDataFile, Long importUserId, String username);

    String importTemplate();
}
