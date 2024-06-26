package vn.edu.hus.amr.service.impl;

import vn.edu.hus.amr.dto.*;
import vn.edu.hus.amr.dto.projection.SentenceDTO;
import vn.edu.hus.amr.model.AppUser;
import vn.edu.hus.amr.model.UserParagraph;
import vn.edu.hus.amr.model.Word;
import vn.edu.hus.amr.repository.ParagraphRepository;
import vn.edu.hus.amr.repository.UserParagraphRepository;
import vn.edu.hus.amr.repository.UserRepository;
import vn.edu.hus.amr.service.ParagraphService;
import vn.edu.hus.amr.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ParagraphServiceImpl implements ParagraphService {
    private final UserRepository userRepository;
    private final ParagraphRepository paragraphRepository;
    private final UserParagraphRepository userParagraphRepository;
    @Override
    public ResponseDTO getParagraphPagination(String username, Integer first, Integer rows, Integer numOfWords) {
        try {
            FormResult formResult = paragraphRepository.getParagraphPaging(username, first, rows, numOfWords);
            return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, "Success", formResult);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public ResponseDTO getParagraphPagination(Integer first, Integer rows, Integer numOfWords) {
        try {
            FormResult formResult = paragraphRepository.getParagraphPaging(null, first, rows, numOfWords);
            return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, "Success", formResult);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public ResponseDTO getAllSentenceOfParagraph(String username, Long divId, Long paragraphId) {
        try {
            FormResult formResult = new FormResult();
            List<SentenceDTO> sentences = paragraphRepository.getAllSentenceOfParagraph(username, divId, paragraphId);
            formResult.setContent(sentences);
            return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, "Success", formResult);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public ResponseDTO updatePosLabel(Long id, WordRequestDTO input) {
        try {
            Optional<Word> opWord = paragraphRepository.findById(id);
            if (opWord.isPresent()) {
                Word word = opWord.get();
                word.setPosLabel(String.valueOf(input.getPosLabel()));
                paragraphRepository.save(word);
                return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, "Update pos label successfully", word);
            } else {
                return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.STATUS_CODE.ERROR, "This word is not exist", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public ResponseDTO getAssignUsers(Long divId, Long paragraphId) {
        try {
            FormResult formResult = paragraphRepository.getAssingUsers(divId, paragraphId);
            return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, "Success", formResult);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ResponseDTO saveUserParagraph(UserParagraphDTO input) {
        try {
            FormResult formResult = paragraphRepository.getAssingUsers(input.getDivId(), input.getParagraphId());
            List<UserDataDTO> listAssignUsersDB = (List<UserDataDTO>) formResult.getContent();
            List<Long> listAssignUserIdDB = listAssignUsersDB.stream().map(UserDataDTO::getId).collect(Collectors.toList());
            List<UserDataDTO> listUserInput = input.getUsers();
            List<Long> listUserInputId = listUserInput.stream().map(UserDataDTO::getId).collect(Collectors.toList());

            List<Long> listUserIdDelete = listAssignUserIdDB.stream().filter(id -> !listUserInputId.contains(id)).collect(Collectors.toList());
            List<Long> listUserIdInsert = listUserInputId.stream().filter(id -> !listAssignUserIdDB.contains(id)).collect(Collectors.toList());

            // delete user paragraph
            UserParagraph entityDel;
            for (Long userId : listUserIdDelete) {
                entityDel = userParagraphRepository.getByDivIdAndParagraphIdAndUserId(input.getDivId(), input.getParagraphId(), userId);
                userParagraphRepository.delete(entityDel);
            }
            // insert user paragraph
            UserParagraph item;
            List<UserParagraph> listInsert = new ArrayList<>();
            for (Long userId : listUserIdInsert) {
                item = new UserParagraph();
                item.setDivId(input.getDivId());
                item.setParagraphId(input.getParagraphId());
                item.setUserId(userId);
                listInsert.add(item);
            }

            userParagraphRepository.saveAll(listInsert);

            FormResult formResult1 = paragraphRepository.getAssingUsers(input.getDivId(), input.getParagraphId());
            return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, "Update assign user successfully", formResult1);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ResponseDTO addAssignee(UserParagraphDTO input) {
        try {
            // check user exist in database
//            List<Long> userIds = input.getUsers().stream().map(UserDataDTO::getId).collect(Collectors.toList());
            List<Long> userIds = input.getUserIds();
            List<AppUser> existUsers = userRepository.findByIdIn(userIds);
            List<Long> existUserIds = existUsers.stream().map(AppUser::getId).collect(Collectors.toList());

            List<UserParagraph> listInsert = new ArrayList<>();
            // check user exist in user-paragraph
            for (ParagraphPosition paragraphPosition : input.getParagraphPositions()) {
                Long divId = paragraphPosition.getDivId();
                Long paragraphId = paragraphPosition.getParagraphId();
                List<UserParagraph> existUserParagraphs = userParagraphRepository.findByDivIdAndParagraphIdAndUserIdIn(divId, paragraphId, existUserIds);
                List<Long> existUserParaIds = existUserParagraphs.stream().map(UserParagraph::getUserId).collect(Collectors.toList());
                List<Long> notExistUserParagraphs = existUserIds.stream().filter(id -> !existUserParaIds.contains(id)).collect(Collectors.toList());

                // save list user
                UserParagraph item;
                for (Long userId : notExistUserParagraphs) {
                    item = new UserParagraph();
                    item.setDivId(divId);
                    item.setParagraphId(paragraphId);
                    item.setUserId(userId);
                    listInsert.add(item);
                }
            }

            userParagraphRepository.saveAll(listInsert);
//            FormResult formResult1 = paragraphRepository.getAssingUsers(input.getDivId(), input.getParagraphId());
            return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, String.format("Insert %d/%d users", listInsert.size(), userIds.size()), null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ResponseDTO deleteAssignee(UserParagraphDTO input) {
        try {
            // check user exist in user-paragraph
//            List<Long> userIds = input.getUsers().stream().map(UserDataDTO::getId).collect(Collectors.toList());
            List<Long> userIds = input.getUserIds();
            List<UserParagraph> existUserParagraphs = userParagraphRepository.findByDivIdAndParagraphIdAndUserIdIn(input.getDivId(), input.getParagraphId(), userIds);

            // delete list user
            userParagraphRepository.deleteAll(existUserParagraphs);
            FormResult formResult1 = paragraphRepository.getAssingUsers(input.getDivId(), input.getParagraphId());
            return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, String.format("Delete %d/%d users", existUserParagraphs.size(), userIds.size()), formResult1);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }
}
