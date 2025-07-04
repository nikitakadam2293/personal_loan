package com.personal_loan.personal_loan.repository;

import com.personal_loan.personal_loan.entity.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant,Long> {

}


