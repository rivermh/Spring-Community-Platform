package com.community.platform.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.community.platform.user.entity.User;



public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String email);
	
	//이메일 , 비밀번호 중복
	boolean existsByUsername(String username);
	boolean existsByEmail(String email);
	
}
  