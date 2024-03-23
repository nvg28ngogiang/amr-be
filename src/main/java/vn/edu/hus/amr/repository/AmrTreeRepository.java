package vn.edu.hus.amr.repository;

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

}
