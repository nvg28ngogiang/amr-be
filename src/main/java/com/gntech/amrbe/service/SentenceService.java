package com.gntech.amrbe.service;

import com.gntech.amrbe.dto.ResponseDTO;

public interface SentenceService {
    ResponseDTO getSentenceDetail(String username, Long divId, Long paragraphId, Long sentenceId);

    ResponseDTO getAmrTreeOfSentence(String username, Long divId, Long paragraphId, Long sentenceId);
}
