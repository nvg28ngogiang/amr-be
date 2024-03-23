package vn.edu.hus.amr.dto.projection;

public interface SentenceDetailDTO {
    Long getId();
    Long getDivId();
    Long getParagraphId();
    Long getSentenceId();
    Long getWordOrder();
    String getContent();
    String getPosLabel();
}
