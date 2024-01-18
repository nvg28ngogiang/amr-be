package com.gntech.amrbe.service.impl;

import com.gntech.amrbe.dto.AmrDetailRequestDTO;
import com.gntech.amrbe.dto.AmrNode;
import com.gntech.amrbe.dto.FormResult;
import com.gntech.amrbe.dto.ResponseDTO;
import com.gntech.amrbe.model.AmrLabel;
import com.gntech.amrbe.model.AmrTree;
import com.gntech.amrbe.model.AmrWord;
import com.gntech.amrbe.repository.AmrLabelRepository;
import com.gntech.amrbe.repository.AmrTreeRepository;
import com.gntech.amrbe.repository.AmrWordRepository;
import com.gntech.amrbe.service.AmrService;
import com.gntech.amrbe.util.CommonUtils;
import com.gntech.amrbe.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class AmrServiceImpl implements AmrService {
    private final AmrTreeRepository amrTreeRepository;
    private final AmrWordRepository amrWordRepository;

    private final AmrLabelRepository amrLabelRepository;
    @Override
    public ResponseDTO getAmrDetail(String username, Long treeId) {
        try {
            FormResult formResult = amrWordRepository.getAmrDetail(username, treeId);
            return new ResponseDTO(Constants.RESPONSE_STATUS.OK, Constants.STATUS_CODE.SUCCESS, "Success", formResult);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseDTO(Constants.RESPONSE_STATUS.ERROR, Constants.STATUS_CODE.ERROR, e.getMessage(), null);
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
            String sentencePosition = CommonUtils.createSentencePosition(
                    input.getDivId(),
                    input.getParagraphId(),
                    input.getSentenceId()
            );
            amrTree.setSentencePosition(sentencePosition);

            amrTreeRepository.save(amrTree);

            List<AmrWord> amrWords = createListAmrWord(input.getNodes(), amrTree.getId());

            amrWordRepository.saveAll(amrWords);
            return new ResponseDTO(Constants.RESPONSE_STATUS.OK, Constants.STATUS_CODE.SUCCESS, "Save success", null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseDTO(Constants.RESPONSE_STATUS.ERROR, Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }

    private List<AmrWord> createListAmrWord(List<AmrNode> amrNodes, Long treeId) {
        return amrNodes.stream().map(this::mapNodeRequestToAmrWordEntity)
                .peek(amrWord -> amrWord.setTreeId(treeId)).collect(Collectors.toList());
    }

    private AmrWord mapNodeRequestToAmrWordEntity(AmrNode node) {
        AmrWord amrWord = new AmrWord();
        amrWord.setId(node.getId());
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
            formResult.setListData(listData);
            formResult.setTotalRecords(Long.valueOf(listData.size()));

            return new ResponseDTO(Constants.RESPONSE_STATUS.OK, Constants.STATUS_CODE.SUCCESS, "Success", formResult);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseDTO(Constants.RESPONSE_STATUS.ERROR, Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }
}
