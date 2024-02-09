package vn.edu.hus.amr.service.impl;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.edu.hus.amr.dto.*;
import vn.edu.hus.amr.model.*;
import vn.edu.hus.amr.repository.*;
import vn.edu.hus.amr.service.AmrService;
import vn.edu.hus.amr.util.CommonUtils;
import vn.edu.hus.amr.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.edu.hus.amr.util.ExcelStyleUtil;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class AmrServiceImpl implements AmrService {
    private final AmrTreeRepository amrTreeRepository;
    private final AmrWordRepository amrWordRepository;
    private final UserRepository userRepository;
    private final AmrLabelRepository amrLabelRepository;

    private final UserParagraphRepository userParagraphRepository;
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

//            Map<Long, AmrWord> mapWordIdAmrNode = amrWords.stream().collect(Collectors.toMap(AmrWord::getWordId, word -> word));
//            amrWords = amrWords.stream().map(word -> {
//                if (word.getParentId() != null) {
//                    if (mapWordIdAmrNode.containsKey(word.getParentId())) {
//                        AmrWord parentNode = mapWordIdAmrNode.get(word.getParentId());
//                        word.setParentId(parentNode.getId());
//                    }
//                }
//                return word;
//            }).collect(Collectors.toList());
//            amrWordRepository.saveAll(amrWords);

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
        amrWord.setCorrefId(node.getCorrefId());
        amrWord.setCorrefPosition(node.getCorrefPosition());
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

    @Override
    public String export(String username) {
        AppUser appUser = userRepository.findByUsername(username);
        List<UserParagraph> userParagraphs = userParagraphRepository.findByUserId(appUser.getId());
        String paragraphPositions = userParagraphs.stream().map(userParagraph -> userParagraph.getDivId() + "/" + userParagraph.getParagraphId() + "/")
                .collect(Collectors.joining("|"));
        List<AmrDetailResponseDTO> listResponse = (List<AmrDetailResponseDTO>) amrWordRepository.getAmrDetailForExport(appUser.getId(), paragraphPositions).getContent();

        if (listResponse == null) {
            listResponse = new ArrayList<>();
        }

        return export(listResponse);
    }

    public String export(List<AmrDetailResponseDTO> listData) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet dataSheet = workbook.createSheet("data");

        CellStyle headerCellStyle = ExcelStyleUtil.getHeaderCellStyle(workbook);
        CellStyle stringCellStyle = ExcelStyleUtil.getStringCellStyle(workbook);
        CellStyle dateCellStyle = ExcelStyleUtil.getDateCellStyle(workbook);
        CellStyle numberCellStyle = ExcelStyleUtil.getNumberCellStyle(workbook);

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        int rowNum = 0;
        List<ExcelHeaderDTO> headerList = generateHeaderList();
        Row headerRow = dataSheet.createRow(rowNum);
        headerCellStyle.setWrapText(true);
        CommonUtils.setHeaderToRowList(workbook, headerList, headerRow, dataSheet, headerCellStyle);

        rowNum++;

        for (AmrDetailResponseDTO data : listData) {
            Row row = dataSheet.createRow(rowNum++);
            List<Object> dataObj = generateDataObject(data);
            CommonUtils.setDataToRowByList(dataObj, row, stringCellStyle, dateCellStyle, numberCellStyle, df);
        }

        String path = "./report_out/amr_data/";
        File dir = new File(path);

        if (!dir.exists()) {
            dir.mkdirs();
        }
        path += "AMR_TREE_" + CommonUtils.getStrDate(System.currentTimeMillis(), "ddMMyyyy_hhmmss") + ".xlsx";

        try {
            // write file
            FileOutputStream outputStream = new FileOutputStream(path);
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return path;
    }

    private List<ExcelHeaderDTO> generateHeaderList() {
        List<ExcelHeaderDTO> result = new ArrayList<>();
        result.add(new ExcelHeaderDTO("Sentence position", ExcelStyleUtil.MEDIUM_SIZE));
        result.add(new ExcelHeaderDTO("Word id", ExcelStyleUtil.MEDIUM_SIZE));
        result.add(new ExcelHeaderDTO("Word content", ExcelStyleUtil.MEDIUM_SIZE));
        result.add(new ExcelHeaderDTO("Pos label", ExcelStyleUtil.MEDIUM_SIZE));
        result.add(new ExcelHeaderDTO("Parent id", ExcelStyleUtil.MEDIUM_SIZE));
        result.add(new ExcelHeaderDTO("Amr label id", ExcelStyleUtil.MEDIUM_SIZE));
        result.add(new ExcelHeaderDTO("Amr label content", ExcelStyleUtil.MEDIUM_SIZE));
        result.add(new ExcelHeaderDTO("Word sense id", ExcelStyleUtil.MEDIUM_SIZE));
        result.add(new ExcelHeaderDTO("Word sense", ExcelStyleUtil.BIG_SIZE));
        result.add(new ExcelHeaderDTO("Word label", ExcelStyleUtil.MEDIUM_SIZE));
        return result;
    }

    private List<Object> generateDataObject(AmrDetailResponseDTO data) {
        List<Object> result = new ArrayList<>();
        result.add(data.getSentencePosition() != null ? data.getSentencePosition() : "");
        result.add(data.getWordId() != null ? data.getWordId() : "");
        result.add(data.getWordContent() != null ? data.getWordContent() : "");
        result.add(data.getPosLabel() != null ? data.getPosLabel() : "");
        result.add(data.getParentId() != null ? data.getParentId() : "");
        result.add(data.getAmrLabelId() != null ? data.getAmrLabelId() : "");
        result.add(data.getAmrLabelContent() != null ? data.getAmrLabelContent() : "");
        result.add(data.getWordSenseId() != null ? data.getWordSenseId() : "");
        result.add(data.getWordSense() != null ? data.getWordSense() : "");
        result.add(data.getWordLabel() != null ? data.getWordLabel() : "");
        return result;
    }
}
