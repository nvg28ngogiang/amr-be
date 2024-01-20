package vn.edu.hus.amr.repository;

import vn.edu.hus.amr.model.AmrTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmrTreeRepository extends JpaRepository<AmrTree, Long> {

    List<AmrTree> getByUserIdAndSentencePosition(Long userId, String sentencePosition);
}
