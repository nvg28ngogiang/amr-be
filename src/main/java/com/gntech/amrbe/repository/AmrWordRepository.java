package com.gntech.amrbe.repository;

import com.gntech.amrbe.model.AmrWord;
import com.gntech.amrbe.repository.custom.AmrDetailRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmrWordRepository extends JpaRepository<AmrWord, Long>, AmrDetailRepositoryCustom {
}
