package vn.edu.hus.amr.controller;

import vn.edu.hus.amr.dto.ResponseDTO;
import vn.edu.hus.amr.dto.UserParagraphDTO;
import vn.edu.hus.amr.dto.WordRequestDTO;
import vn.edu.hus.amr.service.ParagraphService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paragraphs")
@RequiredArgsConstructor
public class ParagraphController {
    private final ParagraphService paragraphService;

    @GetMapping
    public ResponseDTO getParagraphPaging (
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "first") Integer first,
            @RequestParam(name = "rows") Integer rows,
            @RequestParam(name = "numOfWords") Integer numOfWords,
            @RequestParam(name = "level") Integer level
    ) {
        return paragraphService.getParagraphPagination(userDetails.getUsername(), first, rows, numOfWords, level);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDTO getAllParagraph(
            @RequestParam(name = "first") Integer first,
            @RequestParam(name = "rows") Integer rows,
            @RequestParam(name = "numOfWords") Integer numOfWords) {
        return paragraphService.getParagraphPagination(first, rows, numOfWords);
    }

    @GetMapping(params = {"divId", "paragraphId"})
    public ResponseDTO getAllSentenceOfParagraph(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestParam(name = "divId") Long divId,
                                                 @RequestParam(name = "paragraphId") Long paragraphId) {
        return paragraphService.getAllSentenceOfParagraph(userDetails.getUsername(), divId, paragraphId);
    }

    @PutMapping("/words/{id}/pos-label")
    public ResponseDTO updatePostLavel(@PathVariable("id") Long id,
                                       @RequestBody WordRequestDTO input) {
        return paragraphService.updatePosLabel(id, input);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/assign-users")
    public ResponseDTO getAssignUsers(@RequestParam(name = "divId") Long divId,
                                      @RequestParam(name = "paragraphId") Long paragraphId) {
        return paragraphService.getAssignUsers(divId, paragraphId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/assign-users")
    public ResponseDTO saveUserParagraph(@RequestBody UserParagraphDTO input) {
        return paragraphService.saveUserParagraph(input);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/assign-users/create")
    public ResponseDTO createAssignUsers(@RequestBody UserParagraphDTO input) {
        return paragraphService.addAssignee(input);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/assign-users/delete")
    public ResponseDTO deleteAssignusers(@RequestBody UserParagraphDTO input) {
        return paragraphService.deleteAssignee(input);
    }
}
