package com.kmii.home.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kmii.home.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByUsername(String username);

}
