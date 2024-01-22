package vn.edu.hus.amr.repository;

import vn.edu.hus.amr.model.UserParagraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserParagraphRepository extends JpaRepository<UserParagraph, Long> {
    UserParagraph getByDivIdAndParagraphIdAndUserId(Long divId, Long paragraphId, Long userId);
}
