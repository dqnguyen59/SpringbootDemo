package org.smartblackbox.springbootdemo.controller;

import org.modelmapper.ModelMapper;
import org.smartblackbox.springbootdemo.datamodel.auth.Role;
import org.smartblackbox.springbootdemo.datamodel.auth.User;
import org.smartblackbox.springbootdemo.datamodel.dto.SignInDTO;
import org.smartblackbox.springbootdemo.datamodel.dto.SignUpDTO;
import org.smartblackbox.springbootdemo.datamodel.enums.RoleType;
import org.smartblackbox.springbootdemo.datamodel.response.SingleMessageDTO;
import org.smartblackbox.springbootdemo.datamodel.response.UserDTO;
import org.smartblackbox.springbootdemo.datamodel.response.exception.DefaultExceptionDTO;
import org.smartblackbox.springbootdemo.repository.RoleRepository;
import org.smartblackbox.springbootdemo.service.AuthenticationService;
import org.smartblackbox.springbootdemo.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/auth")
@RestController
@SecurityRequirement(name = "bearerAuth")
public class AuthenticationController {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private RoleRepository roleRepository;

	@Operation(summary = "Sign in user")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User signed in succesfully",
			content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
			}),
			@ApiResponse(responseCode = "401", description = "Bad credentials", 
			content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = DefaultExceptionDTO.class))
			}),
			@ApiResponse(responseCode = "403", description = "User not authorized",
			content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = DefaultExceptionDTO.class))
			}),
	})
	@PostMapping("/signin")
	public ResponseEntity<UserDTO> signIn(@RequestBody SignInDTO loginUserDTO) {
		User user = authenticationService.authenticate(loginUserDTO);
		User userResponse = authenticationService.signin(user);
		UserDTO userDTO = modelMapper.map(userResponse, UserDTO.class);

		return ResponseEntity.ok(userDTO);
	}

	@Operation(summary = "Sign out user")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Sign out user",  
			content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = SingleMessageDTO.class))
			}),
			@ApiResponse(responseCode = "401", description = "Bad credentials", 
			content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = DefaultExceptionDTO.class))
			}),
			@ApiResponse(responseCode = "403", description = "User not authorized",
			content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = DefaultExceptionDTO.class))
			}),
	})
	@PostMapping("/signout")
	public ResponseEntity<SingleMessageDTO> signOut() {
		try {
			User user = (User) SpringUtils.getAuthentication().getPrincipal();
			String response = authenticationService.signout(user);

			SingleMessageDTO msgDTO = new SingleMessageDTO();
			msgDTO.setMessage(response);

			return ResponseEntity.ok(msgDTO);
		}
		catch (Exception e) {
			throw new AccessDeniedException("");
		}
	}

	@Operation(summary = "Sign up user")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User signed up succesfully",
			content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", 
			content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = DefaultExceptionDTO.class))
			}),
			@ApiResponse(responseCode = "403", description = "User not authorized",
			content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = DefaultExceptionDTO.class))
			}),
	})
	@PostMapping("/signup")
	public ResponseEntity<UserDTO> signUp(@RequestBody SignUpDTO registerUserDTO) {
		SpringUtils.getAndValidateUser();
		Role role = roleRepository.findByRole(RoleType.ROLE_USER).get();
		User registeredUser = authenticationService.signup(registerUserDTO, role);

		UserDTO userDTO = modelMapper.map(registeredUser, UserDTO.class);
		return ResponseEntity.ok(userDTO);
	}

}
