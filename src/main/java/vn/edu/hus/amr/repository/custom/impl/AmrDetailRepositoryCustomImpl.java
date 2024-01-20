package vn.edu.hus.amr.repository.custom.impl;

import vn.edu.hus.amr.dto.AmrDetailResponseDTO;
import vn.edu.hus.amr.dto.FormResult;
import vn.edu.hus.amr.repository.custom.AmrDetailRepositoryCustom;
import vn.edu.hus.amr.util.CommonUtils;
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
        if (params.size() > 0) {
            for (Map.Entry<String, Object> param : params.entrySet()) {
                query.setParameter(param.getKey(), param.getValue());
            }
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

        result.setTotalElements(Long.valueOf(listResponse.size()));
        result.setContent(listResponse);
        return result;
    }

    StringBuilder buildAmrDetailSQL() {
        StringBuilder sql = new StringBuilder("select " +
                "a.id " +
                ", a.parent_id as \"parentId\" " +
                ", b.content as \"wordContent\" " +
                ", c.id as \"amrLabelId\" " +
                ", c.name as \"amrLabelContent\" " +
                ", a.word_label as \"wordLabel\" " +
                "from amr_word a " +
                "join word b on a.word_id = b.id " +
                "join amr_label c on a.amr_label_id = c.id " +
                "where a.tree_id = :treeId");

        return sql;
    }
}
