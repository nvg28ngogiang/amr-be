package vn.edu.hus.amr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.hus.amr.model.Word;

import java.util.List;
import java.util.Set;

public interface WordRepository extends JpaRepository<Word, Long> {

    @Query(value = "Select a from Word a join AmrWord b on a.id = b.wordId join AmrTree c on b.treeId = c.id where a.isAdditional = true and c.id = :treeId")
    List<Word> findAdditionalWordByTreeId(@Param(value = "treeId") Long treeId);

    @Query(nativeQuery = true, value = "select a.* from word a where concat('d', div_id, 'p', paragraph_id, 's', sentence_id) in (:sentencePositions)")
    List<Word> findBySentencePositions(@Param(value = "sentencePositions") Set<String> sentencePositions);
}
