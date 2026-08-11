package org.xyonsoft.bankingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.xyonsoft.bankingapp.Entity.Admin;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username);
}