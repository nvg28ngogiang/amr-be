package vn.edu.hus.amr.util;

import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import vn.edu.hus.amr.dto.ExcelHeaderDTO;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Log4j2
public class CommonUtils {

    public static void convertMapResultToObject(Map<String, Object> objMap, Field[] fields,
                                                Object temp) {
        for (Field f : fields) {
            f.setAccessible(true);
            if (objMap.containsKey(f.getName())) {
                try {
                    Class<?> type = f.getType();
                    Object o = objMap.get(f.getName());
                    if (o != null) {
                        if (type.equals(Long.class)) {
                            if (o instanceof BigDecimal) {
                                f.set(temp, ((BigDecimal) o).longValue());
                            } else if (o instanceof Double) {
                                f.set(temp, ((Double) o).longValue());
                            } else if (o instanceof Long) {
                                f.set(temp, ((Long) o));
                            } else if (o instanceof Integer) {
                                f.set(temp, ((Integer) o).longValue());
                            } else if (o instanceof BigInteger) {
                                f.set(temp, ((BigInteger) o).longValue());
                            }
                        } else if (type.equals(String.class)) {
                            f.set(temp, o.toString());
                        } else if (type.equals(Double.class)) {
                            if (o instanceof BigDecimal) {
                                f.set(temp, ((BigDecimal) o).doubleValue());
                            } else if (o instanceof Double) {
                                f.set(temp, ((Double) o));
                            } else if (o instanceof Long) {
                                f.set(temp, ((Long) o).doubleValue());
                            } else if (o instanceof Integer) {
                                f.set(temp, ((Integer) o).doubleValue());
                            }
                        } else if (type.equals(Integer.class)) {
                            if (o instanceof BigDecimal) {
                                f.set(temp, ((BigDecimal) o).intValue());
                            } else if (o instanceof Double) {
                                f.set(temp, ((Double) o).intValue());
                            } else if (o instanceof Long) {
                                f.set(temp, ((Long) o).intValue());
                            } else if (o instanceof Integer) {
                                f.set(temp, ((Integer) o));
                            } else if (o instanceof BigInteger) {
                                f.set(temp, ((BigInteger) o).intValue());
                            }
                        } else if (type.equals(Date.class)) {
                            if (o instanceof java.sql.Timestamp) {
                                f.set(temp, o);
                            }
                        } else if (type.equals(Boolean.class)) {
                            if (o instanceof Boolean) {
                                f.set(temp, o);
                            }
                        }
                    } else {
                        f.set(temp, null);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    public static void setHeaderToRowList(Workbook workbook, List<ExcelHeaderDTO> headerList,
                                          Row headerRow,
                                          Sheet sheet,
                                          CellStyle headerCellStyle) {
        for (int i = 0; i < headerList.size(); i++) {
            Cell cell = headerRow.createCell(i);
            ExcelHeaderDTO headerDTO = headerList.get(i);
            cell.setCellValue(headerDTO.getName());
            cell.setCellStyle(headerCellStyle);
            sheet.setColumnWidth(i, headerDTO.getSize());
        }
    }

    public static void setDataToRowByList(List<Object> dataList,
                                          Row row,
                                          CellStyle stringCellStyle,
                                          CellStyle dateCellStyle,
                                          CellStyle numberCellStyle,
                                          DateFormat df) {
        for (int i = 0; i < dataList.size(); i++) {
            row.setHeightInPoints(17);

            Object value = dataList.get(i);
            Cell cell = row.createCell(i);
            String cellValue = value.toString();

            if (i == 0) {
                cell.setCellStyle(dateCellStyle);
            } else {
                if (value instanceof Date) {
                    cell.setCellStyle(dateCellStyle);
                    cellValue = df.format(value);
                } else if (value instanceof Number) {
                    cell.setCellStyle(numberCellStyle);
                } else {
                    cell.setCellStyle(stringCellStyle);
                }
            }

            cell.setCellValue(cellValue);
        }
    }

    public static String getStrDate(Long time, String format) {
        return new SimpleDateFormat(format).format(new Date(time));
    }
}
