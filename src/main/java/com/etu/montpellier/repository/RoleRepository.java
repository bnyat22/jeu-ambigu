package com.etu.montpellier.repository;


import com.etu.montpellier.domain.ERoles;
import com.etu.montpellier.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(ERoles name);
}
