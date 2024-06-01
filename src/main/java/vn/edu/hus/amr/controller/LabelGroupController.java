package vn.edu.hus.amr.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.hus.amr.dto.ResponseDTO;
import vn.edu.hus.amr.model.LabelGroup;
import vn.edu.hus.amr.service.LabelGroupService;
import vn.edu.hus.amr.util.Constants;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/label-groups")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LabelGroupController {

    private final LabelGroupService labelGroupService;

    @GetMapping
    public ResponseDTO getAll() {
        log.info("Get all label groups");
        List<LabelGroup> labelGroups = labelGroupService.findAll();
        return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, "Success", labelGroups);
    }

}
