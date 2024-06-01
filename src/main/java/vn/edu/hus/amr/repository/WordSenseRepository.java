package vn.edu.hus.amr.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.hus.amr.model.WordSense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface WordSenseRepository extends JpaRepository<WordSense, Long> {
    List<WordSense> getByWordContent(String wordContent);

    @Modifying
    @Transactional
    @Query("DELETE FROM WordSense s WHERE s.id IN :senseIds")
    void deleteAllById(List<Long> senseIds);
    
}
