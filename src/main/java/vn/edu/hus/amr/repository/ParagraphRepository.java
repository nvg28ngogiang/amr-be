package vn.edu.hus.amr.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.hus.amr.dto.projection.SentenceDTO;
import vn.edu.hus.amr.model.Word;
import vn.edu.hus.amr.repository.custom.ParagraphRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParagraphRepository extends JpaRepository<Word, Long>, ParagraphRepositoryCustom {
    @Query(nativeQuery = true, value = "select  " +
            "a.div_id as divId  " +
            ", a.paragraph_id as paragraphId " +
            ", a.sentence_id as sentenceId " +
            ", a.content  " +
            " from  " +
            "(select div_id, paragraph_id, sentence_id, string_agg(replace(content, '_', ' '), ' ' order by word_order) as content, " +
            "count(content) from word  " +
            "where is_additional is not true and div_id = :divId and paragraph_id = :paragraphId " +
            "group by div_id, paragraph_id, sentence_id ) a " +
            "order by a.div_id, a.paragraph_id, a.sentence_id ")
    List<SentenceDTO> getAllSentenceOfParagraph(Long divId, Long paragraphId);

    @Query(nativeQuery = true, value = "select  " +
                "a.div_id as divId  " +
                ", a.paragraph_id as paragraphId " +
                ", a.sentence_id as sentenceId " +
                ", a.content  " +
                " from  " +
                "(select div_id, paragraph_id, sentence_id, string_agg(replace(content, '_', ' '), ' ' order by word_order) as content, " +
                "count(content) from word  " +
                "where is_additional is not true and div_id = :divId and paragraph_id = :paragraphId " +
                "group by div_id, paragraph_id, sentence_id ) a " +
                "where " +
                    " concat(a.div_id, '/', a.paragraph_id, '/', a.sentence_id) in (:sentencePositions)" +
                "order by a.div_id, a.paragraph_id, a.sentence_id ")
    List<SentenceDTO> getAllSentenceOfParagraphBySentencePositionIn(Long divId, Long paragraphId, List<String> sentencePositions);

    @Query(nativeQuery = true, value = "select  " +
            "a.div_id as divId  " +
            ", a.paragraph_id as paragraphId " +
            ", a.sentence_id as sentenceId " +
            ", a.content  " +
            " from  " +
            "(select div_id, paragraph_id, sentence_id, string_agg(replace(content, '_', ' '), ' ' order by word_order) as content, " +
            "count(content) from word  " +
            "where is_additional is not true and div_id = :divId and paragraph_id = :paragraphId " +
            "group by div_id, paragraph_id, sentence_id ) a " +
            "where " +
                " concat(a.div_id, '/', a.paragraph_id, '/', a.sentence_id) not in (:sentencePositions) " +
            "order by a.div_id, a.paragraph_id, a.sentence_id ")
    List<SentenceDTO> getAllSentenceOfParagraphBySentencePositionNotIn(Long divId, Long paragraphId, List<String> sentencePositions);

    @Query(nativeQuery = true, value = "select  " +
            "a.div_id as divId  " +
            ", a.paragraph_id as paragraphId " +
            ", a.sentence_id as sentenceId " +
            ", a.content  " +
            " from  " +
            "(select div_id, paragraph_id, sentence_id, string_agg(content, ' ' order by word_order) as content, " +
            "count(content) from word  " +
            "where is_additional is not true " +
            "group by div_id, paragraph_id, sentence_id ) a " +
            "WHERE concat(a.div_id, '/', a.paragraph_id, '/', a.sentence_id) in (:sentencePositions)")
    List<SentenceDTO> getAllSentenceOfUserHaveAmrTree(List<String> sentencePositions);
}
