package vn.edu.hus.amr.dto;

import lombok.Data;
import vn.edu.hus.amr.util.StringUtils;

import java.util.*;

@Data
public class AmrTreeResponseDTO {
    private Map<AmrDetailResponseDTO, List<AmrDetailResponseDTO>> adjNodes;
    private Map<AmrDetailResponseDTO, Boolean> visited;
    private AmrDetailResponseDTO rootNode;
    private String sentencePosition;
    private StringBuilder amrText;
    private Date updateTime;

    public AmrTreeResponseDTO(List<AmrDetailResponseDTO> nodes) {
        adjNodes = new HashMap<>();
        visited = new HashMap<>();
        Map<Long, List<AmrDetailResponseDTO>> parentMap = buildParentMap(nodes);
        List<AmrDetailResponseDTO> childs;
        for (AmrDetailResponseDTO node : nodes) {
            if (node.getSentencePosition() != null) {
                sentencePosition = node.getSentencePosition();
            }
            visited.put(node, Boolean.FALSE);
            if (parentMap.containsKey(node.getWordId())) {
                childs = parentMap.get(node.getWordId());
                adjNodes.put(node, childs);
            }
            if (node.getParentId() == null) {
                rootNode = node;
            }

            if (node.getUpdateTime() != null) {
                updateTime = node.getUpdateTime();
            }
        }

        amrText = new StringBuilder();
        drawAmrTree(rootNode, 0);
    }

    private Map<Long, List<AmrDetailResponseDTO>> buildParentMap(List<AmrDetailResponseDTO> nodes) {
        Map<Long, List<AmrDetailResponseDTO>> parentMap = new HashMap<>();
        // init map
        for (AmrDetailResponseDTO node : nodes) {
            parentMap.put(node.getWordId(), new ArrayList<>());
        }
        // put child for map
        List<AmrDetailResponseDTO> childs;
        for (AmrDetailResponseDTO node : nodes) {
            if (parentMap.containsKey(node.getParentId())) {
                childs = parentMap.get(node.getParentId());
                childs.add(node);
            }
        }
        return parentMap;
    }

    private void drawAmrTree(AmrDetailResponseDTO node, Integer level) {
        Boolean visit = visited.get(node);
        if (Boolean.FALSE.equals(visit)) {
            if (level != 0) {
                amrText.append("\n");
            }
            printSpace(level);
            if (StringUtils.isNotNUll(node.getAmrLabelContent())) {
                amrText.append(":" + node.getAmrLabelContent());
            }
            amrText.append("(" + node.getWordLabel() + " / " + node.getWordContent());
            visited.put(node, Boolean.TRUE);
            List<AmrDetailResponseDTO> childs = adjNodes.get(node);
            if (childs != null && !childs.isEmpty()) {
                for (AmrDetailResponseDTO child : childs) {
                    drawAmrTree(child, level + 1);
                }
            }
            amrText.append(")");
        }
    }

    private void printSpace(int n) {
        for (int i = 0; i < n; i++) {
            amrText.append("    ");
        }
    }
}
