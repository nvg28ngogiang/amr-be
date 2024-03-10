package vn.edu.hus.amr.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.hus.amr.dto.AmrDetailRequestDTO;
import vn.edu.hus.amr.dto.ExportRequestDTO;
import vn.edu.hus.amr.dto.ResponseDTO;
import vn.edu.hus.amr.service.AmrService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import vn.edu.hus.amr.util.CommonUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequiredArgsConstructor
@Log4j2
public class AmrController {
    private final AmrService amrService;

    @GetMapping("/amr/{id}")
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

    @PostMapping("/amr/export/excel")
    public ResponseEntity<byte[]> exportExcel(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ExportRequestDTO input) {
        String targetFile = amrService.exportExcelFile(userDetails.getUsername(), input);

        FileInputStream inputStream = null;
        try {
            if (targetFile != null) {

                // Step 1: Read the content of this file
                File file = new File(targetFile);
                byte[] contentBytes = new byte[(int) file.length()];
                inputStream = new FileInputStream(file);
                inputStream.read(contentBytes);

                // Step 2: delete file
                if (file.delete()) {
                    System.out.println("File deleted successfully.");
                } else {
                    System.out.println("Failed to delete the file.");
                }

                return ResponseEntity.ok().headers(CommonUtils.buildFileResponseHeader(file.getName()))
                        .body(contentBytes);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    @PostMapping("/amr/export/document")
    public ResponseEntity<byte[]> exportDocument(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ExportRequestDTO input) {
        String path = amrService.exportDocumentFile(userDetails.getUsername(), input);

        FileInputStream inputStream = null;
        try {
            if (path != null) {
                File file = new File(path);
                byte[] contentBytes = new byte[(int) file.length()];
                inputStream = new FileInputStream(file);
                inputStream.read(contentBytes);

                if (file.delete()) {
                    System.out.println("File deleted successfully.");
                } else {
                    System.out.println("Failed to delete the file.");
                }

                return ResponseEntity.ok().headers(CommonUtils.buildFileResponseHeader(file.getName()))
                        .body(contentBytes);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    @GetMapping("/amr/statistic")
    public ResponseDTO statisticUsers() {
        return amrService.statisticUsers();
    }

    @PostMapping("/import")
    public ResponseDTO importInsert(@RequestParam MultipartFile file, @RequestBody Long importUserId) {
        return amrService.importInsert(file, importUserId);
    }

    @GetMapping("/import-template")
    public ResponseEntity<byte[]> importTemplate() {
        String path = amrService.importTemplate();

        FileInputStream inputStream = null;
        try {
            File file = new File(path);
            byte[] contentBytes = new byte[(int) file.length()];
//            inputStream = new FileInputStream(file);
//            inputStream.read(contentBytes);

            return ResponseEntity.ok().headers(CommonUtils.buildFileResponseHeader(file.getName()))
                    .body(contentBytes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }
}
