package vn.edu.hus.amr.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.edu.hus.amr.dto.ResponseDTO;
import vn.edu.hus.amr.dto.WordSenseDTO;
import vn.edu.hus.amr.service.WordSenseService;
import lombok.RequiredArgsConstructor;
import vn.edu.hus.amr.util.Constants;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/word-sense")
public class WordSenseController {

    private final WordSenseService wordSenseService;

    @GetMapping
    public ResponseDTO getWordSenses(@RequestParam(name = "wordContent") String wordContent) {
        return wordSenseService.getWordSenses(wordContent);
    }

    @PostMapping
    public ResponseDTO saveWordSense(@RequestBody WordSenseDTO wordSense) {
        WordSenseDTO savedWordSense = wordSenseService.save(wordSense);
        return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, "Success", savedWordSense);
    }

    @DeleteMapping
    public ResponseDTO deleteWordSenses(@RequestBody List<Long> senseIds) {
        wordSenseService.deleteWordSensesByIdIn(senseIds);
        return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, "Success", null);
    }

}
