package vn.edu.hus.amr.repository.custom;

import vn.edu.hus.amr.dto.FormResult;

public interface ParagraphRepositoryCustom {
    FormResult getParagraphPaging(String username, Integer first, Integer rows, Integer numOfWords);

    FormResult getAllSentenceOfParagraph(String username, Long divId, Long paragraphId);

    FormResult getAssingUsers(Long divId, Long paragraphId);
}
