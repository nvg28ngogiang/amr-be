package com.gntech.amrbe.repository.custom;

import com.gntech.amrbe.dto.FormResult;

public interface SentenceRepositoryCustom {

    FormResult getSentenceDetail(String username, Long divId, Long paragraphId, Long sentenceId);
}
