package vn.edu.hus.amr.controller;

import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.hus.amr.dto.ResponseDTO;
import vn.edu.hus.amr.service.SentenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SentenceController {
    private final SentenceService sentenceService;

    @GetMapping("/sentences")
    public ResponseDTO getSentenceDetail(@AuthenticationPrincipal UserDetails userDetails,
                                         @RequestParam("divId") Long divId,
                                         @RequestParam("paragraphId") Long paragraphId,
                                         @RequestParam("sentenceId") Long sentenceId ) {
        return sentenceService.getSentenceDetail(userDetails.getUsername(), divId, paragraphId, sentenceId);
    }

    @GetMapping("/sentences/amrs")
    public ResponseDTO getListAmrOfSentence(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestParam("divId") Long divId,
                                            @RequestParam("paragraphId") Long paragraphId,
                                            @RequestParam("sentenceId") Long sentenceId ) {
        return sentenceService.getAmrTreeOfSentence(userDetails.getUsername(), divId, paragraphId, sentenceId);
    }
}
