package com.gntech.amrbe.service.impl;

import com.gntech.amrbe.dto.FormResult;
import com.gntech.amrbe.dto.ResponseDTO;
import com.gntech.amrbe.repository.ParagraphRepository;
import com.gntech.amrbe.service.ParagraphService;
import com.gntech.amrbe.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class ParagraphServiceImpl implements ParagraphService {
    private final ParagraphRepository paragraphRepository;
    @Override
    public ResponseDTO getParagraphPagination(String username, Integer first, Integer rows, Integer numOfWords) {
        try {
            FormResult formResult = paragraphRepository.getParagraphPaging(username, first, rows, numOfWords);
            return new ResponseDTO(Constants.RESPONSE_STATUS.OK, Constants.STATUS_CODE.SUCCESS, "Success", formResult);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseDTO(Constants.RESPONSE_STATUS.ERROR, Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public ResponseDTO getAllSentenceOfParagraph(String username, Long divId, Long paragraphId) {
        try {
            FormResult formResult = paragraphRepository.getAllSentenceOfParagraph(username, divId, paragraphId);
            return new ResponseDTO(Constants.RESPONSE_STATUS.OK, Constants.STATUS_CODE.SUCCESS, "Success", formResult);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseDTO(Constants.RESPONSE_STATUS.ERROR, Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }
}
