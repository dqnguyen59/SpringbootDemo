package org.smartblackbox.springbootdemo.controller;

import org.smartblackbox.springbootdemo.datamodel.auth.User;
import org.smartblackbox.springbootdemo.datamodel.response.SingleMessageDTO;
import org.smartblackbox.springbootdemo.datamodel.response.exception.DefaultExceptionDTO;
import org.smartblackbox.springbootdemo.service.UserService;
import org.smartblackbox.springbootdemo.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Copyright (C) 2024  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org>
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/welcome")
@SecurityRequirement(name = "bearerAuth")
public class WelcomeController {

	@Autowired
	private UserService userService;
	
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Welcome message for user", 
					content = {
							@Content(mediaType = "application/json", schema = @Schema(implementation = SingleMessageDTO.class))
			}),
			@ApiResponse(responseCode = "403", description = "User not authorized",
			content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = DefaultExceptionDTO.class))
			}),
	})
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/role/user")
	public ResponseEntity<SingleMessageDTO> welcomeUser() {
		log.info("welcomeUser()");
		User userLoggedIn = SpringUtils.getAndValidateUser();
		String roles = userService.getAllRolesString(userLoggedIn.getId());
		
		SingleMessageDTO msgDTO = new SingleMessageDTO();
		msgDTO.setMessage("Welcome user " + userLoggedIn.getUsername() + ", your roles are: [" + roles + "]!");
		
		return ResponseEntity.ok(msgDTO);
	}

	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Welcome message for administrators", 
					content = {
							@Content(mediaType = "application/json", schema = @Schema(implementation = SingleMessageDTO.class))
			}),
			@ApiResponse(responseCode = "403", description = "User not authorized",
			content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = DefaultExceptionDTO.class))
			}),
	})
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/role/admin")
	public ResponseEntity<SingleMessageDTO> welcomeAdmin() {
		log.info("welcomeAdmin()");
		User userLoggedIn = SpringUtils.getAndValidateUser();
		String roles = userService.getAllRolesString(userLoggedIn.getId());

		SingleMessageDTO msgDTO = new SingleMessageDTO();
		msgDTO.setMessage("Welcome admin user " + userLoggedIn.getUsername() + ", your roles are: [" + roles + "]!");
		
		return ResponseEntity.ok(msgDTO);
	}

}
