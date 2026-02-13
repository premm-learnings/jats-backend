package com.prem.jats.jats_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prem.jats.jats_backend.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
     User findByEmail(String email);

}
