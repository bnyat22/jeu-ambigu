package com.etu.montpellier.repository;


import com.etu.montpellier.domain.MotAmbigu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MotAmbiguRepository extends JpaRepository<MotAmbigu, Long> {

    MotAmbigu getById(int id);

}
