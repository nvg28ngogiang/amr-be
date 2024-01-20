package vn.edu.hus.amr.repository;

import vn.edu.hus.amr.model.AmrWord;
import vn.edu.hus.amr.repository.custom.AmrDetailRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmrWordRepository extends JpaRepository<AmrWord, Long>, AmrDetailRepositoryCustom {
}
