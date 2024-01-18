package org.smartblackbox.springbootdemo.service;

import java.util.Collections;

import org.smartblackbox.springbootdemo.datamodel.auth.Role;
import org.smartblackbox.springbootdemo.datamodel.auth.User;
import org.smartblackbox.springbootdemo.datamodel.dto.SignInDTO;
import org.smartblackbox.springbootdemo.datamodel.dto.SignUpDTO;
import org.smartblackbox.springbootdemo.repository.UserRepository;
import org.smartblackbox.springbootdemo.security.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Copyright (C) 2024  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org>
 *
 */
@Service
public class AuthenticationService {
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtService jwtService;

	public User signup(SignUpDTO input, Role role) {
		User user = User.builder()
				.username(input.getUsername())
				.password(passwordEncoder.encode(input.getPassword()))
				.active(true)
                .resetPasswordRequired(true)
				.roles(Collections.singletonList(role))
				.build();

		return userRepository.save(user);
	}

	public User signin(User user) {
        String jwtToken = jwtService.generateToken(user);
        long expiredTime = jwtService.getExpirationTime();

        user.setToken(jwtToken);
        user.setExpiredTime(expiredTime);
		
        return userRepository.save(user);
	}
	
	public String signout(User user) {
		if (user != null) {
            user.setToken(null);
            user.setExpiredTime(0);
            userRepository.save(user);
    		return "Signout successfully!";
		}
		return "Error: no user defined!";
	}

	public User authenticate(SignInDTO input) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						input.getUsername(),
						input.getPassword()
						)
				);

		return userRepository.findByUserName(input.getUsername()).orElseThrow();
	}
}
