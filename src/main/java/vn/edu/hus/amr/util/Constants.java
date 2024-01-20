package vn.edu.hus.amr.util;

public class Constants {
    public interface STATUS_CODE {
        String INVALID = "INVALID_DATA";
        String SUCCESS = "SUCCESS";
        String ERROR = "ERROR";
        String UN_AUTHENTICATE = "UN_AUTHENTICATE";
    }

    public interface RESPONSE_STATUS {
        Integer ERROR = 0;
        Integer OK = 1;
        Integer UN_AUTHENTICATE = 2;
    }
}
