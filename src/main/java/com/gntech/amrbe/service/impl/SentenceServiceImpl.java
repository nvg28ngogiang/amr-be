package com.gntech.amrbe.service.impl;

import com.gntech.amrbe.dto.FormResult;
import com.gntech.amrbe.dto.ResponseDTO;
import com.gntech.amrbe.model.AmrTree;
import com.gntech.amrbe.model.AppUser;
import com.gntech.amrbe.repository.AmrTreeRepository;
import com.gntech.amrbe.repository.SentenceRepository;
import com.gntech.amrbe.repository.UserRepository;
import com.gntech.amrbe.service.SentenceService;
import com.gntech.amrbe.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

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
            FormResult formResult = sentenceRepository.getSentenceDetail(username, divId, paragraphId, sentenceId);
            return new ResponseDTO(Constants.RESPONSE_STATUS.OK, Constants.STATUS_CODE.SUCCESS, "Success", formResult);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseDTO(Constants.RESPONSE_STATUS.ERROR, Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public ResponseDTO getAmrTreeOfSentence(String username, Long divId, Long paragraphId, Long sentenceId) {
        try {
            FormResult formResult = new FormResult();
            AppUser appUser = userRepository.findByUsername(username);
            String sentencePosition = divId + "/" + paragraphId + "/" + sentenceId;
            List<AmrTree> listResponse = amrTreeRepository.getByUserIdAndSentencePosition(appUser.getId(), sentencePosition);

            formResult.setListData(listResponse);
            formResult.setTotalRecords(Long.valueOf(listResponse.size()));
            return new ResponseDTO(Constants.RESPONSE_STATUS.OK, Constants.STATUS_CODE.SUCCESS, "Success", formResult);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseDTO(Constants.RESPONSE_STATUS.ERROR, Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }
}
