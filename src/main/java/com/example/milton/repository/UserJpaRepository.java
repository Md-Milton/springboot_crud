package com.example.milton.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.milton.domain.UsersDto;



public interface UserJpaRepository extends JpaRepository<UsersDto, Long> {

	//UsersDTO findById(Long id);

	UsersDto findByName(String name);
	}
