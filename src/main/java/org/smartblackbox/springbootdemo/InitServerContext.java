package org.smartblackbox.springbootdemo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.smartblackbox.springbootdemo.datamodel.auth.Role;
import org.smartblackbox.springbootdemo.datamodel.auth.User;
import org.smartblackbox.springbootdemo.datamodel.enums.RoleType;
import org.smartblackbox.springbootdemo.repository.RoleRepository;
import org.smartblackbox.springbootdemo.service.UserService;
import org.smartblackbox.springbootdemo.utils.Consts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Copyright (C) 2024  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org>
 *
 */
@Slf4j
@Configuration
@Component
public class InitServerContext {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostConstruct
	public void init() {
		log.info("init()");
		
		Role roleRoot;
		Role roleAdmin;
		Role roleUser;
		Optional<Role> role;

		role = roleRepository.findByRole(RoleType.ROLE_ROOT);
		if (role.isEmpty()) {
			roleRoot = new Role();
			roleRoot.setType(RoleType.ROLE_ROOT);
			roleRepository.save(roleRoot);
		}
		else {
			roleRoot = role.get();
		}
		
		role = roleRepository.findByRole(RoleType.ROLE_ADMIN);
		if (role.isEmpty()) {
			roleAdmin = new Role();
			roleAdmin.setType(RoleType.ROLE_ADMIN);
			roleRepository.save(roleAdmin);
		}
		else {
			roleAdmin = role.get();
		}

		role = roleRepository.findByRole(RoleType.ROLE_USER);
		if (role.isEmpty()) {
			roleUser = new Role();
			roleUser.setType(RoleType.ROLE_USER);
			roleRepository.save(roleUser);
		}
		else {
			roleUser = role.get();
		}

		String username = Consts.ROOT_USER_NAME;
		String password = Consts.SYS_ROOT_DEFAULT_PASSWORD;

		Optional<User> sysAdminUser = userService.findByUsername(username);
		
		
		if (sysAdminUser.isEmpty()) {

			User user = User.builder()
					.username(username)
					.password(passwordEncoder.encode(password))
					.resetPasswordRequired(true)
					.build();

			List<Role> roles = Arrays.asList(roleRoot, roleAdmin, roleUser);
			
			user.setRoles(roles);
			user.setActive(true);

			User savedUser = userService.save(user);
			if (savedUser != null) {
				log.info("Admin user created!");
			}
		}
		else {
			log.info("Admin user already created!");
		}
		
		log.info("Admin username: " + username);
		log.info("Admin password: " + password);
	}

}
