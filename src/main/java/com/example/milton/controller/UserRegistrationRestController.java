package com.example.milton.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.milton.domain.UsersDto;
import com.example.milton.exception.CustomErrorType;
import com.example.milton.repository.UserJpaRepository;


@RestController
@RequestMapping("/api/user")
public class UserRegistrationRestController {
	public static final Logger logger = LoggerFactory.getLogger(UserRegistrationRestController.class);

	private UserJpaRepository userJpaRepository;

	@Autowired
	public void setUserJpaRepository(UserJpaRepository userJpaRepository) {
		this.userJpaRepository = userJpaRepository;
	}

	@GetMapping("/")
	public ResponseEntity<List<UsersDto>> listAllUsers() {
		logger.info("Fetching all users");
		List<UsersDto> users = userJpaRepository.findAll();
		if (users.isEmpty()) {
			return new ResponseEntity<List<UsersDto>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<UsersDto>>(users, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<UsersDto> getUserById(@PathVariable("id") final Long id) {
		logger.info("Fetching User with id {}", id);
		Optional<UsersDto>optional = userJpaRepository.findById(id);
		UsersDto user = optional.get();
		if (user == null) {
			logger.error("User with id {} not found.", id);
			return new ResponseEntity<UsersDto>(new CustomErrorType("User with id " + id + " not found"),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<UsersDto>(user, HttpStatus.OK);
	}

	/**
	 * @exception MethodArgumentNotValidException
	 *                (validation fails)
	 */
	@PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UsersDto> createUser(@Valid @RequestBody final UsersDto user) {
		logger.info("Creating User : {}", user);
		if (userJpaRepository.findByName(user.getName()) != null) {
			logger.error("Unable to create. A User with name {} already exist", user.getName());
			return new ResponseEntity<UsersDto>(
					new CustomErrorType("Unable to create new user. A User with name " + user.getName() + " already exist."),
					HttpStatus.CONFLICT);
		}
		userJpaRepository.save(user);
		return new ResponseEntity<UsersDto>(user, HttpStatus.CREATED);
	}

	@PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UsersDto> updateUser(@PathVariable("id") final Long id, @RequestBody UsersDto user) {
		logger.info("Updating User with id {}", id);
		Optional<UsersDto>optional = userJpaRepository.findById(id);
		UsersDto currentUser = optional.get();
		if (currentUser == null) {
			logger.error("Unable to update. User with id {} not found.", id);
			return new ResponseEntity<UsersDto>(
					new CustomErrorType("Unable to upate. User with id " + id + " not found."), HttpStatus.NOT_FOUND);
		}
		currentUser.setName(user.getName());
		currentUser.setAddress(user.getAddress());
		currentUser.setEmail(user.getEmail());
		userJpaRepository.saveAndFlush(currentUser);
		return new ResponseEntity<UsersDto>(currentUser, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<UsersDto> deleteUser(@PathVariable("id") final Long id) {
		logger.info("Deleting User with id {}", id);
		Optional<UsersDto>optional = userJpaRepository.findById(id);
		UsersDto user = optional.get();
		if (user == null) {
			logger.error("Unable to delete. User with id {} not found.", id);
			return new ResponseEntity<UsersDto>(
					new CustomErrorType("Unable to delete. User with id " + id + " not found."), HttpStatus.NOT_FOUND);
		}
		userJpaRepository.deleteById(id);
		return new ResponseEntity<UsersDto>(new CustomErrorType("Deleted User with id " + id + "."),
				HttpStatus.NO_CONTENT);
	}

}
