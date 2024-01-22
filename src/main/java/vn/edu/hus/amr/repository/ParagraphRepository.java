package vn.edu.hus.amr.repository;

import vn.edu.hus.amr.model.Word;
import vn.edu.hus.amr.repository.custom.ParagraphRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParagraphRepository extends JpaRepository<Word, Long>, ParagraphRepositoryCustom {
}
