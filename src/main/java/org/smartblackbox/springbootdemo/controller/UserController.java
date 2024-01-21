package org.smartblackbox.springbootdemo.controller;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.smartblackbox.springbootdemo.datamodel.auth.User;
import org.smartblackbox.springbootdemo.datamodel.dto.UpdateUserDTO;
import org.smartblackbox.springbootdemo.datamodel.enums.RoleType;
import org.smartblackbox.springbootdemo.datamodel.response.UserDTO;
import org.smartblackbox.springbootdemo.datamodel.response.exception.DefaultExceptionDTO;
import org.smartblackbox.springbootdemo.service.UserService;
import org.smartblackbox.springbootdemo.utils.Consts;
import org.smartblackbox.springbootdemo.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * 
 * @author Copyright (C) 2024  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org>
 *
 */
@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Find by Id", 
			content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
		}),
		@ApiResponse(responseCode = "400", description = "Invalid id supplied",
		content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = DefaultExceptionDTO.class))
		}), 
		@ApiResponse(responseCode = "403", description = "User not authorized",
		content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = DefaultExceptionDTO.class))
		}),
	})
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{id}")
	public ResponseEntity<UserDTO> findById(@PathVariable("id") int id) {
		SpringUtils.getAndValidateUser();

		UserDTO userDTO = modelMapper.map(userService.findById(id).get(), UserDTO.class);
		return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
	}

	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Gets my account", 
			content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
		}),
		@ApiResponse(responseCode = "403", description = "User not authorized",  
		content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = DefaultExceptionDTO.class))
		}),
	})
	@GetMapping("/myaccount")
	public ResponseEntity<UserDTO> myUserAccount() {
		User userLoggedIn = SpringUtils.getAndValidateUser();
		return new ResponseEntity<>(modelMapper.map(userLoggedIn, UserDTO.class), HttpStatus.OK);
	}

	/**
	 * All fields of updateUserDTO are optional.
	 * If a field is not included, then it will not be updated.
	 * 
	 * The logged in user can only modify their own account or users that have a lower role level.
	 * 
	 * @param id
	 * @param updateUserDTO
	 * @return
	 */
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "All fields of updateUserDTO are optional."
			+ "If a field is not included, then it will not be updated."
			+ ""
			+ "The logged in user can only modify their own account."
			+ "If the logged in user has an admin role, then their own and other accounts can be modified.", 
			content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
		}),
		@ApiResponse(responseCode = "400", description = "Invalid id supplied",
		content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = DefaultExceptionDTO.class))
		}), 
		@ApiResponse(responseCode = "403", description = "User not authorized",
		content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = DefaultExceptionDTO.class))
		}),
	})
	@PutMapping("/{id}")
	public ResponseEntity<UserDTO> updateUser(@PathVariable("id") int id, @RequestBody UpdateUserDTO updateUserDTO) {
		User loggedInUser = SpringUtils.getAndValidateUser();
		Optional<User> user = userService.findById(id);

		int loggedInUserHighestRoleLevel = loggedInUser.getHighestRoleLevel();
		int userHighestRoleLevel = user.get().getHighestRoleLevel();
		
		boolean isRoot = loggedInUser.hasRole(RoleType.ROLE_ROOT);

		if (loggedInUser.getId() != id && loggedInUserHighestRoleLevel <= userHighestRoleLevel) {
			throw new AccessDeniedException(
				"You are not allowed to alter users with a higher or the same role then yours!");
		}
		
		if (updateUserDTO.getRoles() != null && loggedInUser.getId() == id &&
			!loggedInUser.roleMatched(updateUserDTO.getRoles())) {
			throw new AccessDeniedException("You are not allowed to modify roles of your own!");
		}

		return user.map(savedUser -> {
			if (updateUserDTO.getUsername() != null) {
				if (isRoot) {
					throw new AccessDeniedException("You are not allowed to alter root username!");
				}
				savedUser.setUsername(updateUserDTO.getUsername());
			}

			// If user change their own password, then old password must match the current password
			if (loggedInUser.getId() == id) {
				if (updateUserDTO.getOldPassword() != null && updateUserDTO.getNewPassword() != null) {
					if (passwordEncoder.matches(updateUserDTO.getOldPassword(), user.get().getPassword())) {
						savedUser.setPassword(passwordEncoder.encode(updateUserDTO.getNewPassword()));
					}
					else {
						throw new BadCredentialsException("Error: old password not match! Password not updated for user id: " + id);
					}
				}
			}
			// Altering other users with a lower role level.
			else {
				savedUser.setPassword(passwordEncoder.encode(updateUserDTO.getNewPassword()));
			}
			
			if (updateUserDTO.getResetPasswordRequired() != null) {
				savedUser.setResetPasswordRequired(updateUserDTO.getResetPasswordRequired());
			}

			if (updateUserDTO.getActive() != null) {
				savedUser.setActive(updateUserDTO.getActive());
			}
			if (updateUserDTO.getEmail() != null) {
				savedUser.setEmail(updateUserDTO.getEmail());
			}

			if (updateUserDTO.getRoles() != null) {
				savedUser.setRoles(updateUserDTO.getRoles());
			}

			User updatedUser = userService.save(savedUser);

			UserDTO userDTO = modelMapper.map(updatedUser, UserDTO.class);

			return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
		})
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Delete user", 
			content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
		}),
		@ApiResponse(responseCode = "400", description = "Invalid id supplied",
		content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = DefaultExceptionDTO.class))
		}), 
		@ApiResponse(responseCode = "403", description = "User not authorized",
		content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = DefaultExceptionDTO.class))
		}),
	})
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT) // 204
	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable("id") Long id) {
		User loggedInUser = SpringUtils.getAndValidateUser();
		if (loggedInUser.getUsername() == Consts.ROOT_USER_NAME) {
			throw new DataIntegrityViolationException("Unable to delete sysadmin user! sysadmin user should never be deleted!");
		}
		userService.deleteById(id);
	}

	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Gets user list", 
			content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
		}),
		@ApiResponse(responseCode = "403", description = "User not authorized",
		content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = DefaultExceptionDTO.class))
		}),
	})
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/page")
	public ResponseEntity<List<User>> getUserListAndSortBy(
		@RequestParam(name = "pageNo", required = false) Integer pageNo,
		@RequestParam(name = "pageSize", required = false) Integer pageSize,
		@RequestParam(name = "sortBy", required = false) String sortBy) {
		SpringUtils.getAndValidateUser();
		List<User> list = userService.getPage(pageNo, pageSize, sortBy);

		return new ResponseEntity<List<User>>(list, new HttpHeaders(), HttpStatus.OK);
	}	

}
