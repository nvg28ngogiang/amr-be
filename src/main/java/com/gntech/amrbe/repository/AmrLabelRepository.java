package com.gntech.amrbe.repository;

import com.gntech.amrbe.model.AmrLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmrLabelRepository extends JpaRepository<AmrLabel, Long> {
}
