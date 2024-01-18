package com.gntech.amrbe.service;

import com.gntech.amrbe.dto.ResponseDTO;

public interface WordSenseService {
    ResponseDTO getWordSenses(String wordContent);
}
