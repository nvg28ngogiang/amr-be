package com.gntech.amrbe.service;

import com.gntech.amrbe.dto.ResponseDTO;
import com.gntech.amrbe.repository.ParagraphRepository;

import javax.servlet.http.HttpServletRequest;

public interface ParagraphService {
    ResponseDTO getParagraphPagination(String username,
                                       Integer first,
                                       Integer rows,
                                       Integer numOfWords);

    ResponseDTO getAllSentenceOfParagraph(String username, Long divId, Long paragraphId);
}
