package vn.edu.hus.amr.service.impl;

import vn.edu.hus.amr.dto.FormResult;
import vn.edu.hus.amr.dto.ResponseDTO;
import vn.edu.hus.amr.model.WordSense;
import vn.edu.hus.amr.repository.WordSenseRepository;
import vn.edu.hus.amr.service.WordSenseService;
import vn.edu.hus.amr.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
@Log4j2
public class WordSenseServiceImpl implements WordSenseService {
    private final WordSenseRepository wordSenseRepository;

    @Override
    public ResponseDTO getWordSenses(String wordContent) {
        try {
            List<WordSense> listData = wordSenseRepository.getByWordContent(wordContent);
            FormResult formResult = new FormResult();
            formResult.setListData(listData);
            formResult.setTotalRecords(Long.valueOf(listData.size()));
            return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, "Success", formResult);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.STATUS_CODE.ERROR, e.getMessage(), null);

        }
    }
}
