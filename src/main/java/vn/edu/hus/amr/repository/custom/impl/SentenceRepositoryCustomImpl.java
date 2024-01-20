package vn.edu.hus.amr.repository.custom.impl;

import vn.edu.hus.amr.dto.FormResult;
import vn.edu.hus.amr.dto.SentenceDetailDTO;
import vn.edu.hus.amr.repository.custom.SentenceRepositoryCustom;
import vn.edu.hus.amr.util.CommonUtils;
import vn.edu.hus.amr.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class SentenceRepositoryCustomImpl implements SentenceRepositoryCustom {
    private final EntityManager entityManager;

    @Override
    public FormResult getSentenceDetail(String username, Long divId, Long paragraphId, Long sentenceId) {
        FormResult result = new FormResult();
        StringBuilder sql = buildSentenceDetailSQL();
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotNUll(username)) {
            params.put("username", username);
        }
        if (divId != null) {
            params.put("divId", divId);
        }
        if (paragraphId != null) {
            params.put("paragraphId", paragraphId);
        }
        if (sentenceId != null) {
            params.put("sentenceId", sentenceId);
        }
        Query query = entityManager.createNativeQuery(sql.toString());
        if (params.size() > 0) {
            for (Map.Entry<String, Object> param : params.entrySet()) {
                query.setParameter(param.getKey(), param.getValue());
            }
        }
        NativeQueryImpl nativeQuery = (NativeQueryImpl) query;
        nativeQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        List<Map<String, Object>> listObjMap = nativeQuery.getResultList();
        Field[] fields = SentenceDetailDTO.class.getDeclaredFields();
        SentenceDetailDTO item;
        List<SentenceDetailDTO> listResponse = new ArrayList<>();
        for (Map<String, Object> objMap : listObjMap) {
            item = new SentenceDetailDTO();
            CommonUtils.convertMapResultToObject(objMap, fields, item);
            listResponse.add(item);
        }

        result.setTotalRecords(Long.valueOf(listResponse.size()));
        result.setListData(listResponse);
        return result;
    }

    StringBuilder buildSentenceDetailSQL() {
        StringBuilder sql = new StringBuilder("select  " +
                "a.id " +
                ", a.div_id as \"divId\" " +
                ", a.paragraph_id as \"paragraphId\" " +
                ", a.sentence_id as \"sentenceId\" " +
                ", a.word_order as \"wordOrder\" " +
                ", a.content " +
                ", a.pos_label as \"posLabel\" " +
                "from word a  " +
                "join user_paragraph b on a.div_id  = b.div_id and a.paragraph_id = b.paragraph_id " +
                "join app_user c on b.user_id = c.id " +
                "where c.username = :username " +
                "and a.div_id = :divId and a.paragraph_id = :paragraphId and a.sentence_id = :sentenceId ");
        return sql;
    }
}
