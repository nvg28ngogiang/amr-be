package vn.edu.hus.amr.service;

import vn.edu.hus.amr.dto.ResponseDTO;
import vn.edu.hus.amr.dto.WordSenseDTO;

import java.util.List;

public interface WordSenseService {
    ResponseDTO getWordSenses(String wordContent);

    WordSenseDTO create(WordSenseDTO wordSense);

    void deleteWordSensesByIdIn(List<Long> senseIds);
}
