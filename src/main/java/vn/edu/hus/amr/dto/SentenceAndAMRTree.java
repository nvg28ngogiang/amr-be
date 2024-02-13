package vn.edu.hus.amr.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class SentenceAndAMRTree {
    private SentenceDTO sentence;
    private List<AmrTreeResponseDTO> amrTrees;

    public SentenceAndAMRTree(SentenceDTO sentence) {
        this.sentence = sentence;
        amrTrees = new ArrayList<>();
    }

    public String getSentenceContentAndAmrTreeText() {
        StringBuilder result = new StringBuilder();
        result.append(sentence.getContent());
        result.append("\n");
        for (AmrTreeResponseDTO tree : amrTrees) {
            result.append(tree.getAmrText());
            result.append("\n");
        }
        return result.toString();
    }

}
