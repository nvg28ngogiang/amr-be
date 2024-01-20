package vn.edu.hus.amr.util;

import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
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
}
