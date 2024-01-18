package org.smartblackbox.springbootdemo.datamodel.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.smartblackbox.springbootdemo.datamodel.response.RoleDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Copyright (C) 2024  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org>
 *
 */
@Schema(description = "User DTO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

	private static final long serialVersionUID = 7351552538256446648L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@NotBlank
	@Size(max = 20)
	@Column(unique = true)
	private String username;

	@Size(max = 50)
	@Column(unique = true)
	private String email;

	@NotBlank
	@Size(max = 120)
	private String password;
	
	@Column(nullable = false)
	private boolean resetPasswordRequired;
	
	@ManyToMany(fetch = FetchType.EAGER)
	//@JoinColumn(name = "user_id")
	private List<Role> roles;
	
	@Column(nullable = false)
	private boolean active;

	//@Transient
	private String token;
	
	//@Transient
	private long expiredTime;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public boolean hasRole(Role.RoleEnum role) {
		for (Role r : roles) {
			if (r.getName() == role)
				return true;
		}
		return false;
	}

	public boolean roleMatched(List<Role> roles) {
		if (this.roles.size() == roles.size()) {
			for (Role role : roles) {
				if (!this.roles.stream().anyMatch(r -> r.getName().equals(role.getName()))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public boolean roleMatchedDTO(List<RoleDTO> roles) {
		if (this.roles.size() == roles.size()) {
			for (RoleDTO role : roles) {
				if (!this.roles.stream().anyMatch(r -> r.getName().toString().equals(role.getName().name()))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public boolean isResetPasswordRequired() {
		return resetPasswordRequired;
	}

	public void setResetPasswordRequired(boolean resetPasswordRequired) {
		this.resetPasswordRequired = resetPasswordRequired;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean isLoggedIn() {
		return token != null;
	}
	
	public long getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(long expiredTime) {
		this.expiredTime = expiredTime;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();
	    for (Role role: roles) {
	        authorities.add(new SimpleGrantedAuthority(role.getName().toString()));
	    }
	    return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
