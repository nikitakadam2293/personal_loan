package com.personal_loan.personal_loan.repository;
import com.personal_loan.personal_loan.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long > {

    Optional<AppUser> findByUsername(String username);

}
