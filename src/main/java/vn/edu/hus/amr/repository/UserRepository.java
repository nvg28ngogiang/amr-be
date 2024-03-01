package vn.edu.hus.amr.repository;

import org.springframework.data.jpa.repository.Query;
import vn.edu.hus.amr.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<AppUser, Integer> {
    boolean existsByUsername(String username);

    AppUser findByUsername(String username);

    List<AppUser> findByIdIn(List<Long> ids);

}
