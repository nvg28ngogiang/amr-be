package vn.edu.hus.amr.service.impl;

import org.modelmapper.ModelMapper;
import vn.edu.hus.amr.dto.FormResult;
import vn.edu.hus.amr.dto.ResponseDTO;
import vn.edu.hus.amr.dto.WordSenseDTO;
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

    private final ModelMapper modelMapper;

    private final WordSenseRepository wordSenseRepository;

    @Override
    public ResponseDTO getWordSenses(String wordContent) {
        try {
            List<WordSense> listData = wordSenseRepository.getByWordContent(wordContent);
            FormResult formResult = new FormResult();
            formResult.setContent(listData);
            formResult.setTotalElements(Long.valueOf(listData.size()));
            return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, "Success", formResult);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.STATUS_CODE.ERROR, e.getMessage(), null);

        }
    }

    @Override
    public WordSenseDTO save(WordSenseDTO dto) {
        WordSense entity = new WordSense();
        entity.setId(dto.getId());
        entity.setWordContent(dto.getWordContent());
        entity.setSense(dto.getSense());
        entity.setExample(dto.getExample());
        entity = wordSenseRepository.save(entity);
        return modelMapper.map(entity, WordSenseDTO.class);
    }

    @Override
    public void deleteWordSensesByIdIn(List<Long> senseIds) {
        wordSenseRepository.deleteAllById(senseIds);
    }

}
