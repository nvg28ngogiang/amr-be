package vn.edu.hus.amr.repository;

import org.springframework.data.jpa.repository.Query;
import vn.edu.hus.amr.dto.projection.SentenceDetailDTO;
import vn.edu.hus.amr.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SentenceRepository extends JpaRepository<Word, Long> {

    @Query(nativeQuery = true, value = "select  " +
            "a.id " +
            ", a.div_id as \"divId\" " +
            ", a.paragraph_id as \"paragraphId\" " +
            ", a.sentence_id as \"sentenceId\" " +
            ", a.word_order as \"wordOrder\" " +
            ", replace(a.content, '_', ' ') as content " +
            ", a.pos_label as \"posLabel\" " +
            "from word a  " +
            "join user_paragraph b on a.div_id  = b.div_id and a.paragraph_id = b.paragraph_id " +
            "join app_user c on b.user_id = c.id " +
            "where a.is_additional is not true and c.username = :username " +
            "and a.div_id = :divId and a.paragraph_id = :paragraphId and a.sentence_id = :sentenceId " +
            "order by a.word_order ")
    List<SentenceDetailDTO> getSentenceDetail(String username, Long divId, Long paragraphId, Long sentenceId);
}
