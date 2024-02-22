package vn.edu.hus.amr.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import vn.edu.hus.amr.dto.AmrDetailRequestDTO;
import vn.edu.hus.amr.dto.ResponseDTO;
import vn.edu.hus.amr.service.AmrService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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

    @GetMapping("/amrs/export")
    public ResponseEntity<byte[]> export(@AuthenticationPrincipal UserDetails userDetails) {
        String path = amrService.export(userDetails.getUsername());

        FileInputStream inputStream = null;
        try {
            if (path != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                File file = new File(path);
                byte[] contentBytes = new byte[(int) file.length()];
                inputStream = new FileInputStream(file);
                inputStream.read(contentBytes);

                if (file.delete()) {
                    System.out.println("File deleted successfully.");
                } else {
                    System.out.println("Failed to delete the file.");
                }
                headers.set("File", file.getName());
                headers.set("Content-Disposition", "attachment; filename=" + file.getName());
                headers.set("Access-Control-Expose-Headers", "File");

                return ResponseEntity.ok().headers(headers).body(contentBytes);
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

    @GetMapping("/amrs/export/document")
    public ResponseEntity<byte[]> exportDocument(@AuthenticationPrincipal UserDetails userDetails) {
        String path = amrService.exportDocumentFile(userDetails.getUsername());

        FileInputStream inputStream = null;
        try {
            if (path != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                File file = new File(path);
                byte[] contentBytes = new byte[(int) file.length()];
                inputStream = new FileInputStream(file);
                inputStream.read(contentBytes);

                if (file.delete()) {
                    System.out.println("File deleted successfully.");
                } else {
                    System.out.println("Failed to delete the file.");
                }
                headers.set("File", file.getName());
                headers.set("Content-Disposition", "attachment; filename=" + file.getName());
                headers.set("Access-Control-Expose-Headers", "File");

                return ResponseEntity.ok().headers(headers).body(contentBytes);
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

    @GetMapping("/statistic-users")
    public ResponseDTO statisticUsers() {
        return amrService.statisticUsers();
    }
}
