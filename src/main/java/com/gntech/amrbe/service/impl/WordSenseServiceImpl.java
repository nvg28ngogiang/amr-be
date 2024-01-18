package com.gntech.amrbe.service.impl;

import com.gntech.amrbe.dto.ResponseDTO;
import com.gntech.amrbe.repository.WordSenseRepository;
import com.gntech.amrbe.service.WordSenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WordSenseServiceImpl implements WordSenseService {
    private final WordSenseRepository wordSenseRepository;

    @Override
    public ResponseDTO getWordSenses(String wordContent) {
        return null;
    }
}
