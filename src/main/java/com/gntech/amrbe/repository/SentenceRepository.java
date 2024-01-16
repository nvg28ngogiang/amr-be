package com.gntech.amrbe.repository;

import com.gntech.amrbe.model.Word;
import com.gntech.amrbe.repository.custom.SentenceRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SentenceRepository extends JpaRepository<Word, Long>, SentenceRepositoryCustom {
}
