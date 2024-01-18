package com.gntech.amrbe.repository;

import com.gntech.amrbe.model.WordSense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordSenseRepository extends JpaRepository<WordSense, Long> {
}
