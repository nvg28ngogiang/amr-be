package vn.edu.hus.amr.service.impl;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
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
    private final WordRepository wordRepository;
    private final ParagraphRepository paragraphRepository;

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
            amrTree.setUpdateTime(new Date());

            AppUser appUser = userRepository.findByUsername(username);
            amrTree.setUserId(appUser.getId());

            amrTreeRepository.save(amrTree);

            // delete all additional word belong to amr tree
            if (input.getAmrTreeId() != null) {
                List<Word> additionalWords = wordRepository.findByTreeId(input.getAmrTreeId());
                if (additionalWords != null && !additionalWords.isEmpty()) {
                    wordRepository.deleteAll(additionalWords);
                }
            }

            // if exist additional word, save them into word table
            List<AmrNode> additionalAmrNodes = getAdditionalNodes(input.getNodes());
            if (additionalAmrNodes != null && !additionalAmrNodes.isEmpty()) {
                Word newWord;
                for (AmrNode node : additionalAmrNodes) {
                    newWord = createNewWordFromNode(input.getDivId(), input.getParagraphId(), input.getSentenceId(), node);
                    wordRepository.save(newWord);
                    List<AmrNode> childNodes = getChildNodes(input.getNodes(), node.getWordId());
                    for (AmrNode childNode : childNodes) {
                        childNode.setParentId(newWord.getId());
                    }
                    node.setWordId(newWord.getId());
                }
            }

            // remove exist amr word
            deleteListExistAmrWord(input.getAmrTreeId());

            // create new list amr word
            List<AmrWord> amrWords = createListAmrWord(input.getNodes(), amrTree.getId());

            // save list amr word
            amrWordRepository.saveAll(amrWords);

            return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, "Save success", null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }

    private Word createNewWordFromNode(Long divId, Long paragraphId, Long sentenceId, AmrNode node) {
        Word word = new Word();
        word.setDivId(divId);
        word.setParagraphId(paragraphId);
        word.setSentenceId(sentenceId);
        word.setPosLabel(node.getPosLabel());
        word.setContent(node.getWordContent());
        word.setAdditional(true);
        return word;
    }

    private List<AmrNode> getChildNodes(List<AmrNode> nodes, Long parentId) {
        return nodes.stream().filter(node -> parentId.equals(node.getParentId())).collect(Collectors.toList());
    }

    private List<AmrNode> getAdditionalNodes(List<AmrNode> amrNodes) {
        return amrNodes.stream().filter(AmrNode::isAdditionalWord).collect(Collectors.toList());
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
    public ResponseDTO statisticUsers() {
        try {
            FormResult formResult = amrWordRepository.statisticUsers();

            return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, "Success", formResult);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public String exportExcelFile(String username, ExportRequestDTO input) {
        AppUser appUser = userRepository.findByUsername(username);

        List<Long> exportUserIds = input.getUserIds();

        List<AppUser> exportUsers = new ArrayList<>();
        if (exportUserIds == null) {
            if (appUser.getRoles().contains(AppUserRole.ADMIN)) {
                exportUsers.addAll(userRepository.findAll());
            } else {
                exportUsers.add(appUser);
            }
        } else {
            exportUsers.addAll(userRepository.findByIdIn(exportUserIds));
        }

        String targetDirectory = "./report_out/amr_excel_data_" + CommonUtils.getStrDate(System.currentTimeMillis(), "ddMMyyyy_HHmmss") + "/";
        writeDataToExcelDirectory(targetDirectory, exportUsers);

        // zip this target directory to file
        String targetZipFile = "./report_out/" + "AMR_TREE_" + CommonUtils.getStrDate(System.currentTimeMillis(), "ddMMyyyy_HHmmss") + ".zip";
        try {
            CommonUtils.zipDirectory(targetDirectory, targetZipFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // delete target directory
        CommonUtils.deleteDirectory(new File(targetDirectory));
        return targetZipFile;
    }

    private void writeDataToExcelDirectory(String targetDirectory, List<AppUser> exportUsers) {
        for (AppUser exportUser : exportUsers) {
            List<AmrDetailResponseDTO> listResponse = (List<AmrDetailResponseDTO>) amrWordRepository.getAmrDetailForExport(exportUser.getId()).getContent();
            if (listResponse == null) {
                listResponse = new ArrayList<>();
            }

            writeDataToExcelFile(listResponse, targetDirectory, exportUser.getUsername());
        }
    }

    private void writeDataToExcelFile(List<AmrDetailResponseDTO> listData, String dirPath, String username) {
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

        String path = dirPath;
        File dir = new File(path);

        if (!dir.exists()) {
            dir.mkdirs();
        }
        path += username + ".xlsx";

        try {
            // write file
            FileOutputStream outputStream = new FileOutputStream(path);
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
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

    @Override
    public String exportDocumentFile(String username, ExportRequestDTO input) {

        AppUser appUser = userRepository.findByUsername(username);

        List<Long> exportUserIds = input.getUserIds();

        List<AppUser> exportUsers = new ArrayList<>();
        if (exportUserIds == null) {
            if (appUser.getRoles().contains(AppUserRole.ADMIN)) {
                exportUsers.addAll(userRepository.findAll());
            } else {
                exportUsers.add(appUser);
            }
        } else {
            exportUsers.addAll(userRepository.findByIdIn(exportUserIds));
        }

        String targetDirectory = "./report_out/amr_doc_data_" + CommonUtils.getStrDate(System.currentTimeMillis(), "ddMMyyyy_HHmmss") + "/";
        writeDataToDocDirectory(targetDirectory, exportUsers);

        // zip this target directory to file
        String targetZipFile = "./report_out/" + "AMR_TREE_" + CommonUtils.getStrDate(System.currentTimeMillis(), "ddMMyyyy_HHmmss") + ".zip";
        try {
            CommonUtils.zipDirectory(targetDirectory, targetZipFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // delete target directory
        CommonUtils.deleteDirectory(new File(targetDirectory));
        return targetZipFile;
    }

    private void writeDataToDocDirectory(String targetDirectory, List<AppUser> exportUsers) {
        for (AppUser exportUser : exportUsers) {
            List<SentenceDTO> sentenceDTOs = paragraphRepository.getAllSentenceOfUserHaveAmr(exportUser.getId());
            List<AmrDetailResponseDTO> allNodes = (List<AmrDetailResponseDTO>) amrWordRepository.getAmrDetailForExport(exportUser.getId()).getContent();
            List<SentenceAndAMRTree> sentenceAndAMRTrees = createSentenceAndAmrTrees(sentenceDTOs, allNodes);
            XWPFDocument doc = new XWPFDocument();
            writeInDocFile(doc, sentenceAndAMRTrees, exportUser.getUsername());

            String path = targetDirectory;
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            path += exportUser.getUsername() + ".docx";
            try {
                // write file
                FileOutputStream outputStream = new FileOutputStream(path);
                doc.write(outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void writeInDocFile(XWPFDocument doc, List<SentenceAndAMRTree> sentenceAndAMRTrees, String exportUsername) {
        SentenceDTO sentenceDTO;
        for (int i = 0; i < sentenceAndAMRTrees.size(); i++) {
            SentenceAndAMRTree sentenceAndAMRTree = sentenceAndAMRTrees.get(i);
            XWPFParagraph p1 = doc.createParagraph();
            XWPFRun r1 = p1.createRun();
            r1.setFontSize(16);
            // first line
            sentenceDTO = sentenceAndAMRTree.getSentence();
            r1.setText("::id " + sentenceDTO.getDivId() + "-" + sentenceDTO.getParagraphId() + "-" + sentenceDTO.getSentenceId() + " ");
            r1.setText("::date " + CommonUtils.getStrDateTime(sentenceAndAMRTree.getLastUpdateTime()) + " ");
            r1.setText("::annotator " + exportUsername);
            r1.addCarriageReturn();
            // second line
            r1.setText("::snt ");
            r1.setText(sentenceAndAMRTree.getSentence().getContent());
            r1.addCarriageReturn();

            XWPFParagraph p2 = doc.createParagraph();
            XWPFRun r2 = p2.createRun();
            r2.setFontSize(14);

            String amrTest;
            String[] lines;
            for (int j = 0; j < sentenceAndAMRTree.getAmrTrees().size(); j++) {
                AmrTreeResponseDTO amrTree = sentenceAndAMRTree.getAmrTrees().get(j);
//                r2.setText(" + Cây AMR " + (j+1) + ":");
//                r2.addCarriageReturn();
                amrTest = amrTree.getAmrText().toString();
                lines = amrTest.split("\n");
                r2.setText(lines[0]);
                for (int k = 1; k < lines.length; k++) {
                    r2.addCarriageReturn();
                    r2.setText(lines[k]);
                }
                r2.addCarriageReturn();
                r2.addCarriageReturn();
            }
        }
    }

    private List<SentenceAndAMRTree> createSentenceAndAmrTrees(List<SentenceDTO> sentences, List<AmrDetailResponseDTO> allNodes) {
        List<SentenceAndAMRTree> result = new ArrayList<>();
        for (SentenceDTO sentence : sentences) {
            result.add(new SentenceAndAMRTree(sentence));
        }

        Map<Long, List<AmrDetailResponseDTO>> mapNode = new HashMap<>();

        List<AmrDetailResponseDTO> listNode;
        for (AmrDetailResponseDTO node : allNodes) {
            if (mapNode.containsKey(node.getTreeId())) {
                listNode = mapNode.get(node.getTreeId());
                listNode.add(node);
            } else {
                listNode = new ArrayList<>();
                listNode.add(node);
                mapNode.put(node.getTreeId(), listNode);
            }
        }

        Map<String, List<AmrTreeResponseDTO>> mapTree = new HashMap<>();
        List<AmrTreeResponseDTO> listTree;
        for (Map.Entry<Long, List<AmrDetailResponseDTO>> entry : mapNode.entrySet()) {
            AmrTreeResponseDTO amrTree = new AmrTreeResponseDTO(entry.getValue());

            if (mapTree.containsKey(amrTree.getSentencePosition())) {
                listTree = mapTree.get(amrTree.getSentencePosition());
                listTree.add(amrTree);
            } else {
                listTree = new ArrayList<>();
                listTree.add(amrTree);
                mapTree.put(amrTree.getSentencePosition(), listTree);
            }
        }

        SentenceDTO sentence;
        String sentencePosition;
        Date lastUpdateTime;
        for (SentenceAndAMRTree item : result) {
            sentence = item.getSentence();
            sentencePosition = String.format("d%sp%ss%s", sentence.getDivId(), sentence.getParagraphId(), sentence.getSentenceId());
            if (mapTree.containsKey(sentencePosition)) {
                listTree = mapTree.get(sentencePosition);
                item.setAmrTrees(listTree);
                lastUpdateTime = null;
                for (AmrTreeResponseDTO tree : listTree) {
                    if (tree.getUpdateTime() != null) {
                        lastUpdateTime = lastUpdateTime != null ? (lastUpdateTime.after(tree.getUpdateTime()) ? lastUpdateTime : tree.getUpdateTime()) : tree.getUpdateTime();
                    }
                }
                if (lastUpdateTime != null) {
                    item.setLastUpdateTime(lastUpdateTime);
                }
            }
        }

        return result;
    }
}
