package vn.edu.hus.amr.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelStyleUtil {
    private static CellStyle cellStyle;

    public static final int SMALL_SIZE = 10 * 256;
    public static final int MEDIUM_SIZE = 20 * 256;
    public static final int BIG_SIZE = 40 * 256;

    private ExcelStyleUtil() {

    }

    /**
     * Set border
     *
     * @param cellStyle
     */
    public static void getBorderStyle(XSSFCellStyle cellStyle) {
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
    }

    public static void getBorderStyle(CellStyle cellStyle) {
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
    }

    /**
     * Set font style of title
     *
     * @param workbook
     * @return
     */
    public static XSSFFont getTitleFontStyle(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        // font.setFontHeightInPoints((short) 30);
        font.setFontName("Times New Roman");
        font.setBold(true);
        return font;
    }

    /**
     * Set font of header
     *
     * @param workbook
     * @return
     */
    public static XSSFFont getHeaderFontStyle(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        font.setFontName("Times New Roman");
        font.setBold(true);
        return font;
    }

    /**
     * Set font of data
     *
     * @param workbook
     * @return
     */
    public static XSSFFont getDataFontStyle(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        font.setFontName("Times New Roman");
        return font;
    }

    /**
     * Set style for text
     *
     * @param workbook
     * @param horizontalAlignment
     * @param verticalAlignment
     * @param textFont
     * @param fontSize
     * @param textStyleExcel
     * @return
     */
    public static XSSFCellStyle getTextCellStyle(
            XSSFWorkbook workbook,
            HorizontalAlignment horizontalAlignment,
            VerticalAlignment verticalAlignment,
            String textFont,
            int fontSize,
            TextStyleExcel textStyleExcel) {

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(horizontalAlignment);
        cellStyle.setVerticalAlignment(verticalAlignment);
        cellStyle.setFont(getTextFontStyle(workbook, textFont, fontSize, textStyleExcel));
        return cellStyle;
    }


    /**
     * Set text font
     *
     * @param workbook
     * @param textFont
     * @param fontSize
     * @param textStyleExcel
     * @return
     */
    public static XSSFFont getTextFontStyle(
            XSSFWorkbook workbook,
            String textFont,
            int fontSize,
            TextStyleExcel textStyleExcel) {

        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) fontSize);
        font.setFontName(textFont);
        if (TextStyleExcel.BOLD == textStyleExcel) {
            font.setBold(true);
        } else if (TextStyleExcel.ITALIC == textStyleExcel) {
            font.setItalic(true);
        } else if (TextStyleExcel.UNDERLINE == textStyleExcel) {
            font.setItalic(true);
        }
        return font;
    }

    /**
     * Set table style
     *
     * @param workbook
     * @return
     */
    public static XSSFCellStyle getCellTableStyle(
            XSSFWorkbook workbook,
            HorizontalAlignment horizontalAlignment,
            VerticalAlignment verticalAlignment,
            boolean isRotation,
            String textFont,
            int fontSize,
            TextStyleExcel textStyleExcel) {

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        getBorderStyle(cellStyle);
        cellStyle.setAlignment(horizontalAlignment);
        cellStyle.setVerticalAlignment(verticalAlignment);
        if (isRotation) {
            cellStyle.setRotation((short) 90);
        }
        cellStyle.setFont(getTextFontStyle(workbook, textFont, fontSize, textStyleExcel));
        return cellStyle;
    }

    /**
     * Set border cell merge
     *
     * @param cellRangeAddress
     * @param sheet
     */
    @SuppressWarnings("deprecation")
    public static void setBorderMergeStyle(CellRangeAddress cellRangeAddress, Sheet sheet) {
        RegionUtil.setBorderTop(BorderStyle.THIN, cellRangeAddress, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, cellRangeAddress, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddress, sheet);
    }

    public static Cell getOrCreateCell(Sheet sheet, int rowIdx, int colIdx) {
        Row row = sheet.getRow(rowIdx);
        if (row == null) {
            row = sheet.createRow(rowIdx);
        }

        Cell cell = row.getCell(colIdx);
        if (cell == null) {
            cell = row.createCell(colIdx);
        }

        return cell;
    }

    public static void addComment(Workbook workbook, Sheet sheet, int rowIdx, int colIdx, String author, String commentText) {
        CreationHelper factory = workbook.getCreationHelper();
        //get an existing cell or create it otherwise:
        Cell cell = getOrCreateCell(sheet, rowIdx, colIdx);

        ClientAnchor anchor = factory.createClientAnchor();
        //i found it useful to show the comment box at the bottom right corner
        anchor.setCol1(cell.getColumnIndex() + 1); //the box of the comment starts at this given column...
        anchor.setCol2(cell.getColumnIndex() + 3); //...and ends at that given column
        anchor.setRow1(rowIdx + 1); //one row below the cell...
        anchor.setRow2(rowIdx + 5); //...and 4 rows high

        Drawing drawing = sheet.createDrawingPatriarch();
        Comment comment = drawing.createCellComment(anchor);
        //set the comment text and author
        comment.setString(factory.createRichTextString(commentText));
        comment.setAuthor(author);

        cell.setCellComment(comment);
    }

    public static Font getFontStyle(Workbook workbook, boolean isBold, short size, short color) {
        Font font = workbook.createFont();
        return getFont(isBold, size, color, font);
    }

    public static Font getFont(boolean isBold, short size, short color, Font font) {
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints(size);
        if (isBold) {
            font.setBold(true);
        }
        font.setColor(color);
        return font;
    }

    public static Font getFontStyleStream(SXSSFWorkbook workbook, boolean isBold, short size, short color) {
        Font font = workbook.createFont();
        return getFont(isBold, size, color, font);
    }

    public static CellStyle getTitleCellStyleStream(SXSSFWorkbook workbook) {
        cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setFont(getFontStyleStream(workbook, false, (short) 20, Font.COLOR_NORMAL));
        return cellStyle;
    }

    public static CellStyle getReportDateCellStyleStream(SXSSFWorkbook workbook) {
        cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setFont(getFontStyleStream(workbook, false, (short) 12, Font.COLOR_NORMAL));
        return cellStyle;
    }

    public static CellStyle getHeaderCellStyleStream(SXSSFWorkbook workbook) {
        cellStyle = workbook.createCellStyle();
        getBorderStyle(cellStyle);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFColor myColor = new XSSFColor(new java.awt.Color(189, 215, 238));
        ((XSSFCellStyle) cellStyle).setFillForegroundColor(myColor);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFont(getFontStyleStream(workbook, true, (short) 12, Font.COLOR_NORMAL));
        return cellStyle;
    }

    public static CellStyle getDateCellStyleStream(SXSSFWorkbook workbook) {
        cellStyle = workbook.createCellStyle();
        getBorderStyle(cellStyle);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFont(getFontStyleStream(workbook, false, (short) 12, Font.COLOR_NORMAL));
        DataFormat fmt = workbook.createDataFormat();
        cellStyle.setDataFormat(fmt.getFormat("@"));
        return cellStyle;
    }

    public static CellStyle getStringCellStyleStream(SXSSFWorkbook workbook) {
        cellStyle = workbook.createCellStyle();
        getBorderStyle(cellStyle);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFont(getFontStyleStream(workbook, false, (short) 12, Font.COLOR_NORMAL));
        return cellStyle;
    }

    public static CellStyle getTitleCellStyle(Workbook workbook) {
        cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setFont(getFontStyle(workbook, false, (short) 20, Font.COLOR_NORMAL));
        return cellStyle;
    }

    public static CellStyle getTitleCellStyle2(Workbook workbook) {
        cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setFont(getFontStyle(workbook, true, (short) 12, Font.COLOR_NORMAL));
        return cellStyle;
    }

    public static CellStyle getHeaderCellStyle(Workbook workbook) {
        cellStyle = workbook.createCellStyle();
        getBorderStyle(cellStyle);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        if (workbook instanceof XSSFWorkbook || workbook instanceof SXSSFWorkbook) {
            XSSFColor myColor = new XSSFColor(new java.awt.Color(189, 215, 238));
            ((XSSFCellStyle) cellStyle).setFillForegroundColor(myColor);
        } else if (workbook instanceof HSSFWorkbook) {
            HSSFColor myColor = HSSFColor.HSSFColorPredefined.LIGHT_BLUE.getColor();
            cellStyle.setFillForegroundColor(myColor.getIndex());
        }
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFont(getFontStyle(workbook, true, (short) 12, Font.COLOR_NORMAL));
        cellStyle.setWrapText(true);
        return cellStyle;
    }

    public static CellStyle getHeaderCellStyle1(Workbook workbook) {
        cellStyle = workbook.createCellStyle();
        getBorderStyle(cellStyle);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFColor myColor = new XSSFColor(new java.awt.Color(255, 242, 204));
        ((XSSFCellStyle) cellStyle).setFillForegroundColor(myColor);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFont(getFontStyle(workbook, true, (short) 12, Font.COLOR_NORMAL));
        cellStyle.setWrapText(true);
        return cellStyle;
    }

    public static CellStyle getHeaderCellStyle2(Workbook workbook) {
        cellStyle = workbook.createCellStyle();
        getBorderStyle(cellStyle);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFColor myColor = new XSSFColor(new java.awt.Color(255, 230, 153));
        ((XSSFCellStyle) cellStyle).setFillForegroundColor(myColor);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFont(getFontStyle(workbook, true, (short) 12, Font.COLOR_NORMAL));
        cellStyle.setWrapText(true);
        return cellStyle;
    }

    public static CellStyle getHeaderCellStyle3(Workbook workbook) {
        cellStyle = workbook.createCellStyle();
        getBorderStyle(cellStyle);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFColor myColor = new XSSFColor(new java.awt.Color(255, 192, 0));
        ((XSSFCellStyle) cellStyle).setFillForegroundColor(myColor);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFont(getFontStyle(workbook, true, (short) 12, Font.COLOR_NORMAL));
        cellStyle.setWrapText(true);
        return cellStyle;
    }

    public static CellStyle getDateCellStyle(Workbook workbook) {
        cellStyle = workbook.createCellStyle();
        getBorderStyle(cellStyle);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFont(getFontStyle(workbook, false, (short) 12, Font.COLOR_NORMAL));
        DataFormat fmt = workbook.createDataFormat();
        cellStyle.setDataFormat(fmt.getFormat("@"));
        return cellStyle;
    }

    public static CellStyle getStringCellStyle(Workbook workbook) {
        cellStyle = workbook.createCellStyle();
        getBorderStyle(cellStyle);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFont(getFontStyle(workbook, false, (short) 12, Font.COLOR_NORMAL));
        cellStyle.setWrapText(false);
        return cellStyle;
    }

    public static CellStyle getReportDateCellStyle(Workbook workbook) {
        cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setFont(getFontStyle(workbook, false, (short) 12, Font.COLOR_NORMAL));
        return cellStyle;
    }

    public static CellStyle getBorderCellStyle(Workbook workbook) {
        getBorderStyle(cellStyle);
        return cellStyle;
    }

    public static CellStyle getStringNoBorderCellStyle(Workbook workbook) {
        cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setFont(getFontStyle(workbook, false, (short) 12, Font.COLOR_NORMAL));
        return cellStyle;
    }

    public static CellStyle getNumberCellStyle(Workbook workbook) {
        cellStyle = workbook.createCellStyle();
        getBorderStyle(cellStyle);
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFont(getFontStyle(workbook, false, (short) 12, Font.COLOR_NORMAL));
        cellStyle.setWrapText(true);
        return cellStyle;
    }

    public static CellStyle getRedStringCellStyle(Workbook workbook) {
        cellStyle = workbook.createCellStyle();
        getBorderStyle(cellStyle);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFont(getFontStyle(workbook, false, (short) 10, Font.COLOR_RED));
        cellStyle.setWrapText(false);
        return cellStyle;
    }

    public static CellStyle getDateReportCellStyle(Workbook workbook) {
        cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFont(getFontStyle(workbook, false, (short) 12, Font.COLOR_NORMAL));
        return cellStyle;
    }
}
