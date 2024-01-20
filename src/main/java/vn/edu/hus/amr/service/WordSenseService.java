package vn.edu.hus.amr.service;

import vn.edu.hus.amr.dto.ResponseDTO;

public interface WordSenseService {
    ResponseDTO getWordSenses(String wordContent);
}
