package vn.edu.hus.amr.service.impl;

import vn.edu.hus.amr.dto.FormResult;
import vn.edu.hus.amr.dto.ResponseDTO;
import vn.edu.hus.amr.dto.projection.SentenceDetailDTO;
import vn.edu.hus.amr.model.AmrTree;
import vn.edu.hus.amr.model.AppUser;
import vn.edu.hus.amr.repository.AmrTreeRepository;
import vn.edu.hus.amr.repository.SentenceRepository;
import vn.edu.hus.amr.repository.UserRepository;
import vn.edu.hus.amr.service.SentenceService;
import vn.edu.hus.amr.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class SentenceServiceImpl implements SentenceService {
    private final SentenceRepository sentenceRepository;

    private final UserRepository userRepository;

    private final AmrTreeRepository amrTreeRepository;
    @Override
    public ResponseDTO getSentenceDetail(String username, Long divId, Long paragraphId, Long sentenceId) {
        try {

            FormResult formResult = new FormResult();
            List<SentenceDetailDTO> sentenceDetailS = sentenceRepository.getSentenceDetail(username, divId, paragraphId, sentenceId);
            formResult.setContent(sentenceDetailS);
            return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, "Success", formResult);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public ResponseDTO getAmrTreeOfSentence(String username, Long divId, Long paragraphId, Long sentenceId) {
        try {
            FormResult formResult = new FormResult();
//            AppUser appUser = userRepository.findByUsername(username);
            String sentencePosition = AmrTree.createSentencePosition(divId, paragraphId, sentenceId);
            List<AmrTree> listResponse = amrTreeRepository.findBySentencePosition(sentencePosition);

            formResult.setContent(listResponse);
            formResult.setTotalElements(Long.valueOf(listResponse.size()));
            return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, "Success", formResult);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }
}
