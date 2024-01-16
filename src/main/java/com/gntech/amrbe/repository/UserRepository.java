package com.gntech.amrbe.repository;

import com.gntech.amrbe.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Integer> {
    boolean existsByUsername(String username);

    AppUser findByUsername(String username);

}
