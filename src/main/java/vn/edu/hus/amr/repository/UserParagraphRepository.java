package vn.edu.hus.amr.repository;

import vn.edu.hus.amr.model.UserParagraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserParagraphRepository extends JpaRepository<UserParagraph, Long> {
    UserParagraph getByDivIdAndParagraphIdAndUserId(Long divId, Long paragraphId, Long userId);

    List<UserParagraph> findByDivIdAndParagraphIdAndUserIdIn(Long divId, Long paragraphId, List<Long> userIds);
    List<UserParagraph> findByUserId(Long userId);
}
