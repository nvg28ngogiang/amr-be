package vn.edu.hus.amr.repository;

import vn.edu.hus.amr.model.WordSense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordSenseRepository extends JpaRepository<WordSense, Long> {
    List<WordSense> getByWordContent(String wordContent);
}
