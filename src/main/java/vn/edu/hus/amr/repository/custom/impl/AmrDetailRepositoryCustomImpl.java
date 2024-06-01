package vn.edu.hus.amr.repository.custom.impl;

import lombok.extern.log4j.Log4j2;
import vn.edu.hus.amr.dto.AmrDetailResponseDTO;
import vn.edu.hus.amr.dto.FormResult;
import vn.edu.hus.amr.dto.StatisticUserDTO;
import vn.edu.hus.amr.model.AppUserRole;
import vn.edu.hus.amr.repository.custom.AmrDetailRepositoryCustom;
import vn.edu.hus.amr.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Log4j2
public class AmrDetailRepositoryCustomImpl implements AmrDetailRepositoryCustom {
    private final EntityManager entityManager;

    @Override
    public FormResult getAmrDetail(String username, Long treeId) {
        FormResult result = new FormResult();
        StringBuilder sql = buildAmrDetailSQL();
        Map<String, Object> params = new HashMap<>();
        if (treeId != null) {
            params.put("treeId", treeId);
        }

        Query query = entityManager.createNativeQuery(sql.toString());
        try {
            if (params.size() > 0) {
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    query.setParameter(param.getKey(), param.getValue());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        NativeQueryImpl nativeQuery = (NativeQueryImpl) query;
        nativeQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        List<Map<String, Object>> listObjMap = nativeQuery.getResultList();
        Field[] fields = AmrDetailResponseDTO.class.getDeclaredFields();
        AmrDetailResponseDTO item;
        List<AmrDetailResponseDTO> listResponse = new ArrayList<>();
        for (Map<String, Object> objMap : listObjMap) {
            item = new AmrDetailResponseDTO();
            CommonUtils.convertMapResultToObject(objMap, fields, item);
            listResponse.add(item);
        }

        for (AmrDetailResponseDTO responseDTO : listResponse) {
            if (Boolean.TRUE.equals(responseDTO.getIsAdditional())) {
                List<AmrDetailResponseDTO> childNodes = getChildAmrNode(listResponse, responseDTO.getWordId());

                responseDTO.changeToAdditionalWord();
                if (childNodes != null && !childNodes.isEmpty()) {
                    for (AmrDetailResponseDTO childNode : childNodes) {
                        childNode.changeAdditionalParent();
                    }
                }
            }
        }

        result.setTotalElements(Long.valueOf(listResponse.size()));
        result.setContent(listResponse);
        return result;
    }

    StringBuilder buildAmrDetailSQL() {
        StringBuilder sql = new StringBuilder("select " +
                "a.id " +
                ", a.word_id as \"wordId\" " +
                ", a.parent_id as \"parentId\" " +
                ", b.content as \"wordContent\" " +
                ", c.id as \"amrLabelId\" " +
                ", c.name as \"amrLabelContent\" " +
                ", a.word_label as \"wordLabel\" " +
                ", a.word_sense_id as \"wordSenseId\" " +
                ", a.corref_id as \"correfId\" " +
                ", a.corref_position as \"correfPosition\" " +
                ", b.is_additional as \"isAdditional\" " +
                ", a.english_sense as \"englishSense\" " +
                "from amr_word a " +
                "join word b on a.word_id = b.id " +
                "left join amr_label c on a.amr_label_id = c.id " +
                "where a.tree_id = :treeId");

        return sql;
    }

    private List<AmrDetailResponseDTO> getChildAmrNode(List<AmrDetailResponseDTO> all, Long parentId) {
        return all.stream().filter(node -> parentId.equals(node.getParentId())).collect(Collectors.toList());
    }

    @Override
    public FormResult getAmrDetailForExport(Long exportUserId) {
        FormResult result = new FormResult();
        StringBuilder sql = buildAmrDetailForExportSQL();
        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("userId", exportUserId);

        NativeQueryImpl nativeQuery = (NativeQueryImpl) query;
        nativeQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        List<Map<String, Object>> listObjMap = nativeQuery.getResultList();
        Field[] fields = AmrDetailResponseDTO.class.getDeclaredFields();
        AmrDetailResponseDTO item;
        List<AmrDetailResponseDTO> listResponse = new ArrayList<>();
        String curAmrSentencePosition = "";
        try {
            for (Map<String, Object> objMap : listObjMap) {
                item = new AmrDetailResponseDTO();
                CommonUtils.convertMapResultToObject(objMap, fields, item);
                if (Objects.equals(curAmrSentencePosition, item.getSentencePosition() + "/" + item.getTreeId())) {
                    item.setSentencePosition(null);
                } else {
                    curAmrSentencePosition = item.getSentencePosition() + "/" + item.getTreeId();
                    item.setSentencePosition(formatSentencePosition(item.getSentencePosition()));
                }
                listResponse.add(item);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        result.setTotalElements(Long.valueOf(listResponse.size()));
        result.setContent(listResponse);
        return result;
    }

    private String formatSentencePosition(String sentencePosition) {
        String[] parts = sentencePosition.split("/");
        return String.format("d%sp%ss%s", parts[0], parts[1], parts[2]);
    }

    StringBuilder buildAmrDetailForExportSQL() {
        StringBuilder sql = new StringBuilder("select w.id as \"wordId\", aw.parent_id as \"parentId\",  " +
                "    w.content as \"wordContent\", aw.tree_id as \"treeId\",  " +
                "    aw.amr_label_id as \"amrLabelId\", al.name as \"amrLabelContent\",  " +
                "    aw.word_label as \"wordLabel\", aw.word_sense_id as \"wordSenseId\",  " +
                "    w.pos_label as \"posLabel\", ws.sense as \"wordSense\", " +
                "    at.sentence_position as \"sentencePosition\", " +
                "    w.word_order as \"wordOrder\", pw.word_order as \"parentOrder\", " +
                "    au.username, at.update_time as \"updateTime\"," +
                "    w.is_additional as \"isAdditional\"  " +
                "from amr_word aw  " +
                "left join word w on aw.word_id = w.id  " +
                "left join word pw on aw.parent_id = pw.id  " +
                "left join amr_label al on aw.amr_label_id = al.id  " +
                "left join word_sense ws on aw.word_sense_id = ws.id  " +
                "left join amr_tree at on aw.tree_id = at.id  " +
                "LEFT JOIN user_paragraph up ON w.div_id = up.div_id AND w.paragraph_id = up.paragraph_id " +
                "left join app_user au on au.id = up.user_id " +
                "   where au.id = :userId  " +
                "   order by " +
                "   CAST(split_part(at.sentence_position, '/', 1) AS INTEGER), " +
                "    CAST(split_part(at.sentence_position, '/', 2) AS INTEGER), " +
                "    CAST(split_part(at. sentence_position, '/', 3) AS INTEGER), " +
//                "   at.sentence_position, " +
                "at.id, w.word_order");

        return sql;
    }

    @Override
    public FormResult statisticUsers() {
        FormResult result = new FormResult();
        StringBuilder sql = buildStatisticUserSQL();
        Query query = entityManager.createNativeQuery(sql.toString());

        NativeQueryImpl nativeQuery = (NativeQueryImpl) query;
        nativeQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        List<Map<String, Object>> listObjMap = nativeQuery.getResultList();
        Field[] fields = StatisticUserDTO.class.getDeclaredFields();
        StatisticUserDTO item;
        List<StatisticUserDTO> listResponse = new ArrayList<>();
        for (Map<String, Object> objMap : listObjMap) {
            item = new StatisticUserDTO();
            CommonUtils.convertMapResultToObject(objMap, fields, item);
            listResponse.add(item);
        }

        result.setTotalElements(Long.valueOf(listResponse.size()));
        result.setContent(listResponse);
        return result;
    }

    StringBuilder buildStatisticUserSQL() {

        StringBuilder sql = new StringBuilder("select " +
                "    a.id as \"userId\", a.username, a.name , " +
                "    case when b.total_paragraph is not null then b.total_paragraph else 0 " +
                "      end as \"totalParagraph\" , " +
                "    case when c.total_sentence is not null then c.total_sentence else 0 " +
                "      end as \"totalSentence\" , " +
                "    case when d.total_amr is not null then d.total_amr else 0 " +
                "      end as \"totalAmr\" " +
                "from " +
                "    app_user a " +
                "left join  " +
                "(select count(*) as total_paragraph, user_id " +
                "    from user_paragraph " +
                "    group by user_id ) b on b.user_id = a.id " +
                "left join  " +
                "(select count(distinct concat('d', w.div_id, 'p', w.paragraph_id, 's', w.sentence_id)) as total_sentence, user_id  " +
                "    from word w  " +
                "    join user_paragraph up on w.div_id = up.div_id and w.paragraph_id = up.paragraph_id " +
                "    group by up.user_id) c on a.id = c.user_id " +
                "left join  " +
                "(select count(at.id) as total_amr, temp.user_id " +
                "    from amr_tree at  " +
                "    join  " +
                "    (select distinct concat(w.div_id, '/', w.paragraph_id, '/', w.sentence_id) as sentence_position , user_id  " +
                "    from word w  " +
                "    join user_paragraph up on w.div_id = up.div_id and w.paragraph_id = up.paragraph_id) " +
                "    temp on at.sentence_position  = temp.sentence_position " +
                "    group by temp.user_id ) d on a.id = d.user_id");

        return sql;
    }
}
