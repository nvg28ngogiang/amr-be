package com.gntech.amrbe.repository.custom;

import com.gntech.amrbe.dto.FormResult;

public interface ParagraphRepositoryCustom {
    FormResult getParagraphPaging(String username, Integer first, Integer rows, Integer numOfWords);

    FormResult getAllSentenceOfParagraph(String username, Long divId, Long paragraphId);
}
