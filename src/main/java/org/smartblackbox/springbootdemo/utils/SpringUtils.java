package org.smartblackbox.springbootdemo.utils;

import org.smartblackbox.springbootdemo.datamodel.auth.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 
 * @author Copyright (C) 2024  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org>
 *
 */
public class SpringUtils {
	
	public static Authentication getAuthentication() {
		return  SecurityContextHolder.getContext().getAuthentication();
	}

	/**
	 * Checks if the user is authenticated and logged in.
	 * If user is not authenticated or logged in,
	 * then it always throws AccessDeniedException().
	 * 
	 * Any HTTP request must be provided with a token.
	 * A token is provided when user a signed in.
	 * 
	 * @return the user if logged in, otherwise throws AccessDeniedException().
	 */
	public static User getAndValidateUser() {
		try {
			User user = (User) getAuthentication().getPrincipal();
			if (user != null && user.isLoggedIn() && user.isAccountNonLocked()) {
				return user;
			}
			throw new AccessDeniedException("");
		}
		catch (Exception e) {
			throw new AccessDeniedException("");
		}
	}  	
	
}
