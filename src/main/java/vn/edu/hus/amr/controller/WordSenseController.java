package vn.edu.hus.amr.controller;

import vn.edu.hus.amr.dto.ResponseDTO;
import vn.edu.hus.amr.service.WordSenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WordSenseController {
    private final WordSenseService wordSenseService;

    @GetMapping("/word-sense")
    public ResponseDTO getWordSenses(@RequestParam(name = "wordContent") String wordContent) {
        return wordSenseService.getWordSenses(wordContent);
    }
}
