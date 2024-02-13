package vn.edu.hus.amr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.hus.amr.model.Word;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {

    @Query(value = "Select a from Word a join AmrWord b on a.id = b.wordId join AmrTree c on b.treeId = c.id where a.isAdditional = true and c.id = :treeId")
    List<Word> findByTreeId(@Param(value = "treeId") Long treeId);
}
