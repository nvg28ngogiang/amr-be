package vn.edu.hus.amr.service.impl;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.hus.amr.dto.*;
import vn.edu.hus.amr.model.*;
import vn.edu.hus.amr.repository.*;
import vn.edu.hus.amr.service.AmrService;
import vn.edu.hus.amr.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
            log.error(e.getMessage(), e);
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

            // get list tree to remove
            List<AmrTree> existAmrTrees = amrTreeRepository.findBySentencePosition(sentencePosition);

            List<Long> deleteTreeIds = new ArrayList<>();
            if (existAmrTrees != null && !existAmrTrees.isEmpty()) {
                deleteTreeIds.addAll(existAmrTrees.stream().map(AmrTree::getId).filter(Objects::nonNull).collect(Collectors.toList()));
            }

            if (existAmrTrees != null && !existAmrTrees.isEmpty()) {
                existAmrTrees = existAmrTrees.stream().filter(tree -> !Objects.equals(tree.getId(), amrTree.getId())).collect(Collectors.toList());
            }


            // remove all amr tree that have sentence position
            for (AmrTree existTree : existAmrTrees) {
                amrTreeRepository.delete(existTree);
            }

            amrTreeRepository.save(amrTree);

            // delete all additional word belong to amr tree
            if (input.getAmrTreeId() != null) {
                List<Word> additionalWords = wordRepository.findAdditionalWordByTreeId(input.getAmrTreeId());
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

            // if exist duplicate word, save them as a additional word
            List<AmrNode> duplicateAmrNodes = getDuplicateNodes(input.getNodes());
            if (duplicateAmrNodes != null && !duplicateAmrNodes.isEmpty()) {
                Word duplicateWord;
                for (AmrNode node : duplicateAmrNodes) {
                    duplicateWord = createDuplicateWord(input.getDivId(), input.getParagraphId(), input.getSentenceId(), node);
                    if (duplicateWord != null) {
                        wordRepository.save(duplicateWord);
                        List<AmrNode> childNodes = getChildNodes(input.getNodes(), node.getWordId());
                        for (AmrNode childNode : childNodes) {
                            childNode.setParentId(duplicateWord.getId());
                        }
                        node.setWordId(duplicateWord.getId());
                    }
                }
            }


            // remove exist amr word
            for (Long deleteId : deleteTreeIds) {
                deleteListExistAmrWord(deleteId);
            }

            // create new list amr word
            List<AmrWord> amrWords = createListAmrWord(input.getNodes(), amrTree.getId());

            // save list amr word
            amrWordRepository.saveAll(amrWords);

            return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, "Save success", amrTree);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
        word.setIsAdditional(true);
        return word;
    }

    private Word createDuplicateWord(Long divId, Long paragraphId, Long sentenceId, AmrNode node) {
        Word duplicateWord = new Word();
        duplicateWord.setDivId(divId);
        duplicateWord.setParagraphId(paragraphId);
        duplicateWord.setSentenceId(sentenceId);

        if (node.getCorrefId() != null) {
            Optional<Word> referenceWordOpt = wordRepository.findById(node.getCorrefId());
            if (referenceWordOpt.isPresent()) {
                Word referenceWord = referenceWordOpt.get();
                duplicateWord.setContent(referenceWord.getContent());
                duplicateWord.setPosLabel(referenceWord.getPosLabel());
                duplicateWord.setIsAdditional(true);
                return duplicateWord;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private List<AmrNode> getChildNodes(List<AmrNode> nodes, Long parentId) {
        return nodes.stream().filter(node -> parentId.equals(node.getParentId())).collect(Collectors.toList());
    }

    private List<AmrNode> getAdditionalNodes(List<AmrNode> amrNodes) {
        return amrNodes.stream().filter(AmrNode::isAdditionalWord).collect(Collectors.toList());
    }

    private List<AmrNode> getDuplicateNodes(List<AmrNode> amrNodes) {
        return amrNodes.stream().filter(AmrNode::isDuplicateWord).collect(Collectors.toList());
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
        if (amrWord.getParentId() == null && StringUtils.isNotNUll(node.getEnglishSense())) {
            amrWord.setEnglishSense(node.getEnglishSense());
        }
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
            log.error(e.getMessage(), e);
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
//        List<WriterThread> threads = new ArrayList<>();
//        for (AppUser exportUser : exportUsers) {
//            threads.add(new WriterThread(exportUser, targetDirectory));
//        }
//        for (WriterThread thread : threads) {
//            thread.start();
//        }

        for (AppUser exportUser : exportUsers) {
            List<AmrDetailResponseDTO> listResponse = (List<AmrDetailResponseDTO>) amrWordRepository.getAmrDetailForExport(exportUser.getId()).getContent();
            if (listResponse == null) {
                listResponse = new ArrayList<>();
            }

            writeDataToExcelFile(listResponse, targetDirectory, exportUser.getUsername());
        }
    }

//    class WriterThread extends Thread {
//        AppUser exportUser;
//        String targetDirectory;
//        public WriterThread(AppUser exportUser, String targetDirectory) {
//            this.exportUser = exportUser;
//            this.targetDirectory = targetDirectory;
//        }
//
//        @Override
//        public void run() {
//            List<AmrDetailResponseDTO> listResponse = (List<AmrDetailResponseDTO>) amrWordRepository.getAmrDetailForExport(exportUser.getId()).getContent();
//            if (listResponse == null) {
//                listResponse = new ArrayList<>();
//            }
//
//            writeDataToExcelFile(listResponse, targetDirectory, exportUser.getUsername());
//        }
//    }

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

    @Override
    public ResponseDTO importInsert(MultipartFile excelDataFile, Long importUserId, String username) {
        Workbook workbook;
        try {
            workbook = WriteResultWorkbookUtils.getWorkbookFromFile(excelDataFile);
            workbook.setMissingCellPolicy(Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

            Set<String> errorSentences = new HashSet<>();
            List<AmrDetailRequestDTO> amrDetailRequestDTOS = getDataFromRowImport(workbook.getSheetAt(0), workbook.getSheetAt(0).getLastRowNum(), errorSentences);

            AppUser appUser;
            if (importUserId != null) {
                appUser = userRepository.getById(importUserId);
            } else {
                appUser = userRepository.findByUsername(username);
            }

            int cntSucces = 0;
            for (AmrDetailRequestDTO dto : amrDetailRequestDTOS) {
                ResponseDTO responseDTO = saveOrUpdateAmrTree(appUser.getUsername(), dto);
                if (HttpStatus.INTERNAL_SERVER_ERROR.value() == responseDTO.getStatus()) {
                    errorSentences.add(dto.getSentencePosition());
                }
                if (HttpStatus.OK.value() == responseDTO.getStatus()) {
                    cntSucces++;
                }
            }

            return new ResponseDTO(HttpStatus.OK.value(), Constants.STATUS_CODE.SUCCESS, cntSucces + " Success", errorSentences);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.STATUS_CODE.ERROR, e.getMessage(), null);
        }
    }

    private List<AmrDetailRequestDTO> getDataFromRowImport(Sheet dataSheet, Integer totalRows, Set<String> errorSentences) {
        // get list ImportAmrRequestDTO from data row
        List<ImportAmrRequestDTO> importData = new ArrayList<>();
        Set<String> sentencePositions = new HashSet<>();
        final int START_ROW = 1;
        for (int i = START_ROW; i <= totalRows; i++) {
            Row row = dataSheet.getRow(i);
            if (row != null) {
                ImportAmrRequestDTO data = getDataFromRowImport(row);
                if (data != null) {
                    data.setRowNum(i);
                    importData.add(data);
                    sentencePositions.add(data.getSentencePosition());
                }
            }
        }

        // get list amr label
        List<AmrLabel> amrLabels = amrLabelRepository.findAll();

        // get list word from list sentence import
        sentencePositions.remove(null);
        List<Word> wordDBs = wordRepository.findBySentencePositions(new ArrayList<>(sentencePositions));
        Map<String, List<Word>> mapWordDB = new HashMap<>();
        List<Word> mapWordList;
        for (Word word : wordDBs) {
            if (mapWordDB.containsKey(word.getSentencePosition())) {
                mapWordList = mapWordDB.get(word.getSentencePosition());
                mapWordList.add(word);
            } else {
                mapWordList = new ArrayList<>();
                mapWordList.add(word);
                mapWordDB.put(word.getSentencePosition(), mapWordList);
            }
        }

        for (ImportAmrRequestDTO importWord : importData) {
            if (mapWordDB.containsKey(importWord.getSentencePosition())) {
                mapWordList = mapWordDB.get(importWord.getSentencePosition());
                boolean isWord = false;
                for (Word word : mapWordList) {
                    if (Objects.equals(word.getWordOrder(), importWord.getWordOrder() )) {
                        isWord = true;
                        importWord.setWordId(word.getId());
                    }
                    if (importWord.getParentWordOrder() != null && importWord.getParentWordOrder().equals(word.getWordOrder())) {
                        importWord.setParentId(word.getId());
                    }
                }
                if (!isWord) {
                    errorSentences.add(importWord.getSentencePosition());
                }

                if (importWord.getAmrLabelName() != null && !"".equals(importWord.getAmrLabelName().trim())) {
                    boolean isAmrLabel = false;
                    for (AmrLabel amrLabel : amrLabels) {
                        if (amrLabel.getName().equalsIgnoreCase(importWord.getAmrLabelName())) {
                            isAmrLabel = true;
                            importWord.setAmrLabelId(amrLabel.getId());
                        }
                    }
                    if (!isAmrLabel) {
                        errorSentences.add(importWord.getSentencePosition());
                    }
                }
            } else {
                errorSentences.add(importWord.getSentencePosition());
            }
        }

        Map<String, List<ImportAmrRequestDTO>> mapImport = new HashMap<>();
        List<ImportAmrRequestDTO> listImport;
        for (ImportAmrRequestDTO importWord : importData) {
            if (!errorSentences.contains(importWord.getSentencePosition())) {
                if (mapImport.containsKey(importWord.getSentencePosition())) {
                    listImport = mapImport.get(importWord.getSentencePosition());
                    listImport.add(importWord);
                } else {
                    listImport = new ArrayList<>();
                    listImport.add(importWord);
                    mapImport.put(importWord.getSentencePosition(), listImport);
                }
            }
        }

        List<AmrDetailRequestDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<ImportAmrRequestDTO>> entry : mapImport.entrySet()) {
            String sentencePosition = entry.getKey();
            AmrDetailRequestDTO amrDetailRequestDTO = new AmrDetailRequestDTO();
            amrDetailRequestDTO.convertSentencePosition(sentencePosition);
            amrDetailRequestDTO.setNodes(convertToAmrNode(entry.getValue()));
            result.add(amrDetailRequestDTO);
        }

        return result;
    }

    private List<AmrNode> convertToAmrNode(List<ImportAmrRequestDTO> importList) {
        return importList.stream().map(item -> {
            AmrNode amrNode = new AmrNode();
            amrNode.setWordId(item.getWordId());
            amrNode.setParentId(item.getParentId());
            amrNode.setAmrLabelId(item.getAmrLabelId());
            amrNode.setWordLabel(item.getWordLabel());
            return amrNode;
        }).collect(Collectors.toList());
    }

    private ImportAmrRequestDTO getDataFromRowImport(Row row) {
        ImportAmrRequestDTO data = new ImportAmrRequestDTO();
        if (row != null) {
            int column = 0;
            String sentencePosition = WriteResultWorkbookUtils.formatCell(row.getCell(column++));
            String wordOrderStr = WriteResultWorkbookUtils.formatCell(row.getCell(column++));
            String parentWordOrderStr = WriteResultWorkbookUtils.formatCell(row.getCell(column++));
            String amrLabelName = WriteResultWorkbookUtils.formatCell(row.getCell(column++));
            String wordLabel = WriteResultWorkbookUtils.formatCell(row.getCell(column));

            if (StringUtils.isNotNUll(sentencePosition)) {
                data.setSentencePosition(sentencePosition);
                if (!CommonUtils.isNumber(wordOrderStr)) {
                    return null;
                } else {
                    data.setWordOrder(Long.parseLong(wordOrderStr));
                }

                if (parentWordOrderStr != null && !"".equals(parentWordOrderStr.trim())) {
                    if (!CommonUtils.isNumber(parentWordOrderStr)) {
                        return null;
                    } else {
                        data.setParentWordOrder(Long.parseLong(parentWordOrderStr));
                    }
                }
                data.setAmrLabelName(amrLabelName);
                data.setWordLabel(wordLabel);
            }
        }

        return data;
    }

    @Override
    public String importTemplate() {
        final String PATH = "./import_file/amr_template.xlsx";

        File file = new File(PATH);

        if (!file.exists()) {
            // create file
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet dataSheet = workbook.createSheet("data");

            CellStyle headerCellStyle = ExcelStyleUtil.getHeaderCellStyle(workbook);

            int rowNum = 0;
            List<ExcelHeaderDTO> headerList = generateHeaderTemplate();
            Row headerRow = dataSheet.createRow(rowNum);
            headerCellStyle.setWrapText(true);
            CommonUtils.setHeaderToRowList(workbook, headerList, headerRow, dataSheet, headerCellStyle);

            try {
                // write file
                boolean isCreated = file.createNewFile();
                if (isCreated) {
                    FileOutputStream outputStream = new FileOutputStream(PATH);
                    workbook.write(outputStream);
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }

        return PATH;
    }

    private List<ExcelHeaderDTO> generateHeaderTemplate() {
        List<ExcelHeaderDTO> result = new ArrayList<>();
        result.add(new ExcelHeaderDTO("Sentence position", ExcelStyleUtil.MEDIUM_SIZE));
        result.add(new ExcelHeaderDTO("Word order", ExcelStyleUtil.MEDIUM_SIZE));
        result.add(new ExcelHeaderDTO("Parent word order", ExcelStyleUtil.MEDIUM_SIZE));
        result.add(new ExcelHeaderDTO("Amr label", ExcelStyleUtil.MEDIUM_SIZE));
        result.add(new ExcelHeaderDTO("Word label", ExcelStyleUtil.MEDIUM_SIZE));
        return result;
    }
}
