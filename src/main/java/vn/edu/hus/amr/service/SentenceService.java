package vn.edu.hus.amr.service;

import vn.edu.hus.amr.dto.ResponseDTO;

public interface SentenceService {
    ResponseDTO getSentenceDetail(String username, Long divId, Long paragraphId, Long sentenceId);

    ResponseDTO getAmrTreeOfSentence(String username, Long divId, Long paragraphId, Long sentenceId);
}
