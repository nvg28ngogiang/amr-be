package vn.edu.hus.amr.service.impl;

import vn.edu.hus.amr.dto.AmrDetailRequestDTO;
import vn.edu.hus.amr.dto.AmrNode;
import vn.edu.hus.amr.dto.FormResult;
import vn.edu.hus.amr.dto.ResponseDTO;
import vn.edu.hus.amr.model.AmrLabel;
import vn.edu.hus.amr.model.AmrTree;
import vn.edu.hus.amr.model.AmrWord;
import vn.edu.hus.amr.model.AppUser;
import vn.edu.hus.amr.repository.AmrLabelRepository;
import vn.edu.hus.amr.repository.AmrTreeRepository;
import vn.edu.hus.amr.repository.AmrWordRepository;
import vn.edu.hus.amr.repository.UserRepository;
import vn.edu.hus.amr.service.AmrService;
import vn.edu.hus.amr.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class AmrServiceImpl implements AmrService {
    private final AmrTreeRepository amrTreeRepository;
    private final AmrWordRepository amrWordRepository;
    private final UserRepository userRepository;
    private final AmrLabelRepository amrLabelRepository;
    @Override
    public ResponseDTO getAmrDetail(String username, Long treeId) {
        try {
            FormResult formResult = amrWordRepository.getAmrDetail(username, treeId);
            return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, "Success", formResult);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ResponseDTO saveOrUpdateAmrTree(String username, AmrDetailRequestDTO input) {
        try {
            AmrTree amrTree;
            if (input.getAmrTreeId() == null) {
                // create new amr tree
                amrTree = new AmrTree();
            } else {
                // update tree
                Optional<AmrTree> opAmrTree = amrTreeRepository.findById(input.getAmrTreeId());
                amrTree = opAmrTree.orElseGet(AmrTree::new);
            }

            amrTree.setName(input.getAmrTreeName());
            String sentencePosition = AmrTree.createSentencePosition(
                    input.getDivId(),
                    input.getParagraphId(),
                    input.getSentenceId()
            );
            amrTree.setSentencePosition(sentencePosition);

            AppUser appUser = userRepository.findByUsername(username);
            amrTree.setUserId(appUser.getId());

            amrTreeRepository.save(amrTree);

            deleteListExistAmrWord(input.getAmrTreeId());

            List<AmrWord> amrWords = createListAmrWord(input.getNodes(), amrTree.getId());

            amrWordRepository.saveAll(amrWords);

            Map<Long, AmrWord> mapWordIdAmrNode = amrWords.stream().collect(Collectors.toMap(AmrWord::getWordId, word -> word));
            // set parent id to amr word
            amrWords = amrWords.stream().map(word -> {
                if (word.getParentId() != null) {
                    if (mapWordIdAmrNode.containsKey(word.getParentId())) {
                        AmrWord parentNode = mapWordIdAmrNode.get(word.getParentId());
                        word.setParentId(parentNode.getId());
                    }
                }
                return word;
            }).collect(Collectors.toList());
            amrWordRepository.saveAll(amrWords);

            return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, "Save success", null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }

    private void deleteListExistAmrWord(Long treeId) {
        if (treeId != null) {
            amrWordRepository.deleteByTreeId(treeId);
        }
    }

    private List<AmrWord> createListAmrWord(List<AmrNode> amrNodes, Long treeId) {
        return amrNodes.stream().map(this::mapNodeRequestToAmrWordEntity)
                .peek(amrWord -> amrWord.setTreeId(treeId)).collect(Collectors.toList());
    }

    private AmrWord mapNodeRequestToAmrWordEntity(AmrNode node) {
        AmrWord amrWord = new AmrWord();
        amrWord.setWordId(node.getWordId());
        amrWord.setParentId(node.getParentId());
        amrWord.setWordLabel(node.getWordLabel());
        amrWord.setAmrLabelId(node.getAmrLabelId());
        amrWord.setWordSenseId(node.getWordSenseId());
        return amrWord;
    }

    @Override
    public ResponseDTO getAmrLabels() {
        try {
            FormResult formResult = new FormResult();
            List<AmrLabel> listData = amrLabelRepository.findAll();
            formResult.setContent(listData);
            formResult.setTotalElements(Long.valueOf(listData.size()));

            return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, "Success", formResult);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }
}
