package vn.edu.hus.amr.dto;

import lombok.Data;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class AmrDetailRequestDTO {
    private Long divId;
    private Long paragraphId;
    private Long sentenceId;
    private Long amrTreeId;
    private String amrTreeName;

    private List<AmrNode> nodes;

    static final String SENTENCE_REGEX = "d(\\d+)p(\\d+)s(\\d+)";
    public void convertSentencePosition(String sentencePosition) {
        Pattern pattern = Pattern.compile(SENTENCE_REGEX);
        Matcher matcher = pattern.matcher(sentencePosition);

        if (matcher.matches()) {
            this.divId = Long.parseLong(matcher.group(1));
            this.paragraphId = Long.parseLong(matcher.group(2));
            this.sentenceId = Long.parseLong(matcher.group(3));
        }
    }

    public String getSentencePosition() {
        return String.format("d%sp%ss%s", this.getDivId(), this.getParagraphId(), this.getSentenceId());
    }
}
