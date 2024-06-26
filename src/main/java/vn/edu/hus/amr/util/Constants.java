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

    public interface SentenceStatus {
        Integer PENDING_LEVEL_1 = 1;
        Integer PENDING_LEVEL_2 = 2;
        Integer COMPLETED_LEVEL_2 = 3;
    }

    public interface SentenceRole {
        Integer DONE = 1;
        Integer REVISE = 2;
    }
}
