package org.smartblackbox.springbootdemo.security.jwt;

import java.io.IOException;
import java.util.Optional;

import org.smartblackbox.springbootdemo.datamodel.auth.User;
import org.smartblackbox.springbootdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 
 * @author Copyright (C) 2024  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org>
 *
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserService userService;

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain
			) throws ServletException, IOException {
		final String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			final String token = authHeader.substring(7);
			final String username = jwtService.extractUsername(token);

			//log.info("doFilterInternal(): token = " + token);
			//log.info("doFilterInternal(): username = " + username);

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (username != null && authentication == null) {
				Optional<User> user = userService.findByUsername(username);
				
				boolean isTokenValid = jwtService.isTokenValid(token, user.get());
				// Token must be exact the same!
				isTokenValid = isTokenValid && token.equals(user.get().getToken());
				
				//log.info("doFilterInternal(): isTokenValid = " + isTokenValid);
				if (isTokenValid) {
					
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
							user.get(),
							null,
							user.get().getAuthorities()
							);

					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}

		} catch (Exception exception) {
			//log.warn("Warning: " + exception.getMessage());
		}
		filterChain.doFilter(request, response);
	}
}
