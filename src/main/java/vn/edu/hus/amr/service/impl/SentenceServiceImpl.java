package vn.edu.hus.amr.service.impl;

import org.springframework.beans.factory.annotation.Value;
import vn.edu.hus.amr.dto.FormResult;
import vn.edu.hus.amr.dto.ResponseDTO;
import vn.edu.hus.amr.dto.projection.SentenceDetailDTO;
import vn.edu.hus.amr.model.AmrTree;
import vn.edu.hus.amr.model.AppUser;
import vn.edu.hus.amr.model.UserParagraph;
import vn.edu.hus.amr.repository.AmrTreeRepository;
import vn.edu.hus.amr.repository.SentenceRepository;
import vn.edu.hus.amr.repository.UserParagraphRepository;
import vn.edu.hus.amr.repository.UserRepository;
import vn.edu.hus.amr.service.SentenceService;
import vn.edu.hus.amr.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class SentenceServiceImpl implements SentenceService {
    private final SentenceRepository sentenceRepository;

    private final UserRepository userRepository;

    private final AmrTreeRepository amrTreeRepository;
    private final UserParagraphRepository userParagraphRepository;

    @Value("${sentence.status.min}")
    private Integer MIN_STATUS;

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

    @Override
    public ResponseDTO getLevelsAndStatus(String username, String sentencePosition) {
        try {
            String SENTENCE_REGEX = "(\\d+)/(\\d+)/(\\d+)";
            Pattern pattern = Pattern.compile(SENTENCE_REGEX);
            Matcher matcher = pattern.matcher(sentencePosition);

            if (matcher.matches()) {
                Long divId = Long.parseLong(matcher.group(1));
                Long paragraphId = Long.parseLong(matcher.group(2));
                Long sentenceId = Long.parseLong(matcher.group(3));
                AppUser appUser = userRepository.findByUsername(username);
                List<UserParagraph> userParagraphs = userParagraphRepository.findByUserIdAndDivIdAndParagraphId(appUser.getId(), divId, paragraphId);
                List<Long> levels = null;
                if (userParagraphs != null) {
                    levels = userParagraphs.stream().map(UserParagraph::getLevel).collect(Collectors.toList());
                }

                List<AmrTree> amrTrees = amrTreeRepository.findBySentencePosition(sentencePosition);
                Integer status = MIN_STATUS;

                if (amrTrees != null && !amrTrees.isEmpty()) {
                    status = amrTrees.get(0).getStatus();
                }

                Map<String, Object> mapData = new HashMap<>();
                mapData.put("levels", levels);
                mapData.put("status", status);

                return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, "Success", mapData);
            } else {
                return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, "Sentence position is invalid", null);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }
}
