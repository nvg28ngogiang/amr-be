package vn.edu.hus.amr.repository;

import vn.edu.hus.amr.model.AmrLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmrLabelRepository extends JpaRepository<AmrLabel, Long> {
}
