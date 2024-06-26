package vn.edu.hus.amr.service;

import vn.edu.hus.amr.dto.ResponseDTO;
import vn.edu.hus.amr.dto.UserParagraphDTO;
import vn.edu.hus.amr.dto.WordRequestDTO;

public interface ParagraphService {
    ResponseDTO getParagraphPagination(String username,
                                       Integer first,
                                       Integer rows,
                                       Integer numOfWords,
                                       Integer level);

    ResponseDTO getParagraphPagination(Integer first,
                                       Integer rows,
                                       Integer numOfWords);

    ResponseDTO getAllSentenceOfParagraph(String username, Long divId, Long paragraphId, Integer status);

    ResponseDTO updatePosLabel(Long id, WordRequestDTO input);

    ResponseDTO getAssignUsers(Long divId, Long paragraphId);

    ResponseDTO saveUserParagraph(UserParagraphDTO input);

    ResponseDTO addAssignee(UserParagraphDTO input);

    ResponseDTO deleteAssignee(UserParagraphDTO input);
}
