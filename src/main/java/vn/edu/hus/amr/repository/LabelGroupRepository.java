package vn.edu.hus.amr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.hus.amr.model.LabelGroup;

@Repository
public interface LabelGroupRepository extends JpaRepository<LabelGroup, Long> {
}
