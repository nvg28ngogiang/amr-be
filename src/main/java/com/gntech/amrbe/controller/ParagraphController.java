package com.gntech.amrbe.controller;

import com.gntech.amrbe.dto.ResponseDTO;
import com.gntech.amrbe.service.ParagraphService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paragraphs")
@RequiredArgsConstructor
public class ParagraphController {
    private final ParagraphService paragraphService;
    @GetMapping
    public ResponseDTO getParagraphPagin(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "first") Integer first,
            @RequestParam(name = "rows") Integer rows,
            @RequestParam(name = "numOfWords") Integer numOfWords
            ) {
        return paragraphService.getParagraphPagination(userDetails.getUsername(), first, rows, numOfWords);
    }

    @GetMapping("/{divId}/{paragraphId}")
    public ResponseDTO getAllSentenceOfParagraph(@AuthenticationPrincipal UserDetails userDetails,
                                                 @PathVariable("divId") Long divId,
                                                 @PathVariable("paragraphId") Long paragraphId) {
        return paragraphService.getAllSentenceOfParagraph(userDetails.getUsername(), divId, paragraphId);
    }
}
