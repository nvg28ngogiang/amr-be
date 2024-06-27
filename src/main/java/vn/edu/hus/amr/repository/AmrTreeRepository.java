package vn.edu.hus.amr.repository;

import org.springframework.data.jpa.repository.Query;
import vn.edu.hus.amr.model.AmrTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmrTreeRepository extends JpaRepository<AmrTree, Long> {

//    List<AmrTree> getByUserId(Long userId);
//    List<AmrTree> getByUserIdAndSentencePositionOrderById(Long userId, String sentencePosition);

//    List<AmrTree> getByUserId(Long userId);
    List<AmrTree> findBySentencePosition(String sentencePosition);

    @Query(nativeQuery = true, value = "SELECT a.* " +
            "FROM amr_tree a " +
            "join ( " +
            "   SELECT distinct(concat(w.div_id, '/', w.paragraph_id, '/', w.sentence_id)) as sentence_position " +
            "   FROM word w  " +
            "   JOIN user_paragraph up ON w.div_id = up.div_id AND w.paragraph_id = up.paragraph_id " +
            "   WHERE up.user_id = :userId " +
            ") b on a.sentence_position = b.sentence_position")
    List<AmrTree> getByUserId(Long userId);

    List<AmrTree> findBySentencePositionStartsWithAndStatus(String beginSentencePosition, Integer status);

    List<AmrTree> findBySentencePositionStartsWithAndStatusIn(String beginSentencePosition, List<Integer> statuses);
}
