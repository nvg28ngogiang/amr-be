package vn.edu.hus.amr.repository.custom.impl;

import vn.edu.hus.amr.dto.FormResult;
import vn.edu.hus.amr.dto.ParagraphDTO;
import vn.edu.hus.amr.dto.SentenceDTO;
import vn.edu.hus.amr.dto.UserDataDTO;
import vn.edu.hus.amr.repository.custom.ParagraphRepositoryCustom;
import vn.edu.hus.amr.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ParagraphRepositoryCustomImpl implements ParagraphRepositoryCustom {
    private final EntityManager entityManager;
    @Override
    public FormResult getParagraphPaging(String username, Integer first, Integer rows, Integer numOfWords) {
        FormResult result = new FormResult();
        StringBuilder sqlCount = new StringBuilder("select count(*) from ( ");
        StringBuilder sql = generateGetPaginSQL();
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotNUll(username)) {
            params.put("username", username);
        }

        params.put("numOfWords", numOfWords);

        sqlCount.append(sql);
        sqlCount.append(" ) b");

        Query query = entityManager.createNativeQuery(sql.toString());
        Query queryCount = entityManager.createNativeQuery(sqlCount.toString());

        if (params.size() > 0) {
            for (Map.Entry<String, Object> param : params.entrySet()) {
                query.setParameter(param.getKey(), param.getValue());
                queryCount.setParameter(param.getKey(), param.getValue());
            }
        }
        result.setTotalElements(((BigInteger) queryCount.getSingleResult()).longValue());

        if (first != null && first != -1) {
            result.setFirst(first);
            query.setFirstResult(first);
        }

        if (rows != null && rows != -1) {
            result.setRows(rows);
            query.setMaxResults(rows);
        }

//        NativeQueryImpl nativeQuery = (NativeQueryImpl) query;
//        nativeQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
//
//        List<Map<String, Object>> listObjMap = nativeQuery.getResultList();
//        Field[] fields = ParagraphDTO.class.getDeclaredFields();
        List<Object[]> objs = query.getResultList();
        ParagraphDTO item;
        List<ParagraphDTO> listResponse = new ArrayList<>();
        for (Object[] obj : objs) {
            item = new ParagraphDTO();
            item.setDivId(obj[0] != null ? Long.valueOf(obj[0].toString()) : null);
            item.setParagraphId(obj[1] != null ? Long.valueOf(obj[1].toString()) : null);
            item.setContent(obj[2] != null ? obj[2].toString() : "");
            listResponse.add(item);
        }
        result.setContent(listResponse);

        return result;
    }

    private StringBuilder generateGetPaginSQL() {
        StringBuilder sql = new StringBuilder("select " +
                "a.div_id as divId " +
                ", a.paragraph_id as paragraphId " +
                ", a.content " +
                " from " +
                "(select div_id, paragraph_id, string_agg(content, ' ') as content  " +
//                " from word " +
                " FROM (SELECT div_id, " +
                "             paragraph_id, " +
                "             content, " +
                "             row_number() over (partition by div_id, paragraph_id ORDER BY sentence_id, word_order) as rn " +
                "      FROM word) as tmp " +
                " WHERE rn <= :numOfWords " +
                "group by div_id, paragraph_id " +
                "order by div_id, paragraph_id ) a  " +
                "join user_paragraph b on a.div_id = b.div_id and a.paragraph_id = b.paragraph_id " +
                "join app_user c on b.user_id = b.user_id " +
                "where c.username = :username ");



        return sql;
    }

    @Override
    public FormResult getAllSentenceOfParagraph(String username, Long divId, Long paragraphId) {
        FormResult result = new FormResult();
        StringBuilder sql = generateGetAllSentenceOfParagraph();
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

        Query query = entityManager.createNativeQuery(sql.toString());

        if (params.size() > 0) {
            for (Map.Entry<String, Object> param : params.entrySet()) {
                query.setParameter(param.getKey(), param.getValue());
            }
        }

        List<Object[]> objs = query.getResultList();
        SentenceDTO item;
        List<SentenceDTO> listResponse = new ArrayList<>();
        for (Object[] obj : objs) {
            item = new SentenceDTO();
            item.setDivId(obj[0] != null ? Long.valueOf(obj[0].toString()) : null);
            item.setParagraphId(obj[1] != null ? Long.valueOf(obj[1].toString()) : null);
            item.setSentenceId(obj[2] != null ? Long.valueOf(obj[2].toString()) : null);
            item.setContent(obj[3] != null ? obj[3].toString() : "");
            listResponse.add(item);
        }
        result.setContent(listResponse);
        result.setTotalElements(Long.valueOf(listResponse.size()));

        return result;
    }

    private StringBuilder generateGetAllSentenceOfParagraph() {
        StringBuilder sql = new StringBuilder("select  " +
                "a.div_id as divId  " +
                ", a.paragraph_id as paragraphId " +
                ", a.sentence_id as sentenceId " +
                ", a.content  " +
                " from  " +
                "(select div_id, paragraph_id, sentence_id, string_agg(content, ' ' order by word_order) as content, " +
                "count(content) from word  " +
                "where div_id = :divId and paragraph_id = :paragraphId " +
                "group by div_id, paragraph_id, sentence_id ) a " +
                "join user_paragraph b on a.div_id = b.div_id and a.paragraph_id = b.paragraph_id  " +
                "join app_user c on b.user_id = b.user_id  " +
                "where c.username = :username " +
                "order by a.div_id, a.paragraph_id, a.sentence_id ");

        return sql;
    }

    @Override
    public FormResult getAssingUsers(Long divId, Long paragraphId) {
        FormResult result = new FormResult();
        StringBuilder sql = generateAssignUsersSQL();
        Map<String, Object> params = new HashMap<>();
        params.put("divId", divId);
        params.put("paragraphId", paragraphId);
        Query query = entityManager.createNativeQuery(sql.toString());
        if (params.size() > 0) {
            for (Map.Entry<String, Object> param : params.entrySet()) {
                query.setParameter(param.getKey(), param.getValue());
            }
        }

        List<Object[]> objs = query.getResultList();
        UserDataDTO item;
        List<UserDataDTO> listResponse = new ArrayList<>();
        for (Object[] obj : objs) {
            item = new UserDataDTO();
            item.setId(Long.parseLong(obj[0].toString()));
            item.setUsername(obj[1] != null ? obj[1].toString() : "");
            listResponse.add(item);
        }
        result.setContent(listResponse);
        result.setTotalElements(Long.valueOf(listResponse.size()));
        return result;
    }

    private StringBuilder generateAssignUsersSQL() {
        StringBuilder sql = new StringBuilder("select a.id, a.username as username " +
                "from app_user a " +
                "join user_paragraph b on a.id = b.user_id  " +
                "where b.div_id = :divId and b.paragraph_id = :paragraphId");
        return sql;
    }
}
