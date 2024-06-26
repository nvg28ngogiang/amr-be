package vn.edu.hus.amr.repository.custom;

import vn.edu.hus.amr.dto.FormResult;
import vn.edu.hus.amr.dto.SentenceDTO;

import java.util.List;

public interface ParagraphRepositoryCustom {
    FormResult getParagraphPaging(String username, Integer first, Integer rows, Integer numOfWords, Integer level);

//    FormResult getAllSentenceOfParagraph(String username, Long divId, Long paragraphId);

    FormResult getAssingUsers(Long divId, Long paragraphId);

    List<SentenceDTO> getAllSentenceOfUserHaveAmr(Long userId);
}
