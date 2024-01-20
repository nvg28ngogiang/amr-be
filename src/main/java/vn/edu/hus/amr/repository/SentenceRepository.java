package vn.edu.hus.amr.repository;

import vn.edu.hus.amr.model.Word;
import vn.edu.hus.amr.repository.custom.SentenceRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SentenceRepository extends JpaRepository<Word, Long>, SentenceRepositoryCustom {
}
