package vn.edu.hus.amr.util;

public class StringUtils {
    public static boolean isNotNUll(String str) {
        return str != null && !"".equals(str.trim());
    }
}
