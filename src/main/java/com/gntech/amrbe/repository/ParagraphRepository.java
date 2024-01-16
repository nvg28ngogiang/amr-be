package com.gntech.amrbe.repository;

import com.gntech.amrbe.model.Word;
import com.gntech.amrbe.repository.custom.ParagraphRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParagraphRepository extends JpaRepository<Word, Long>, ParagraphRepositoryCustom {
}
