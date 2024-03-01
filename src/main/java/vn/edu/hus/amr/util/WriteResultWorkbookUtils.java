package vn.edu.hus.amr.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;

public class WriteResultWorkbookUtils {
    private WriteResultWorkbookUtils() {
    }

    public static void addValidationSelectBox(XSSFWorkbook workbook, ArrayList<XSSFSheet> sheets, String name, String reference,
                                              int firstRow, int lastRow, int firstCol, int lastCol) {
        Name named = workbook.createName();
        named.setNameName(name);
        named.setRefersToFormula(reference);
        for (XSSFSheet sheet : sheets) {
            XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
            XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createFormulaListConstraint(name);
            CellRangeAddressList networkTypeRange = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
            XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, networkTypeRange);
            validation.setShowErrorBox(true);
            sheet.addValidationData(validation);
        }
    }

    public static String formatCell(Cell c) {
        if (c == null) {
            return null;
        } else if (c.getCellType() == Cell.CELL_TYPE_STRING) {
            return StringUtils.isNotNUll(c.getStringCellValue()) ? c.getStringCellValue().trim() : c.getStringCellValue();
        } else if (c.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            if (Double.valueOf(0).equals(c.getNumericCellValue() - Math.round(c.getNumericCellValue()))) {
                return String.valueOf((int) c.getNumericCellValue());
            } else {
                return String.valueOf(c.getNumericCellValue());
            }
        } else if (c.getCellType() == Cell.CELL_TYPE_BLANK) {
            return "";
        } else return null;
    }

    public static Workbook getWorkbookFromFile(MultipartFile readExcelDataFile) throws IOException {
        if (readExcelDataFile.getOriginalFilename().endsWith("xlsx")) {
            return new XSSFWorkbook(readExcelDataFile.getInputStream());
        } else if (readExcelDataFile.getOriginalFilename().endsWith("xls")) {
            return new HSSFWorkbook(readExcelDataFile.getInputStream());
        }
        return null;
    }
}
