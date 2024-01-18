package org.smartblackbox.springbootdemo.datamodel.dto;

import java.util.List;

import org.smartblackbox.springbootdemo.datamodel.auth.Role;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 
 * @author Copyright (C) 2024  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org>
 *
 */
@Schema(description = "Update User DTO")
@Data
public class UpdateUserDTO {

	@JsonProperty
	private String username;

	@JsonProperty
	private String email;

	@JsonProperty
	private String oldPassword;
	
	@JsonProperty
	private String newPassword;
	
	@JsonProperty
	private Boolean resetPasswordRequired;
	
	@JsonProperty
	private List<Role> roles;
	
	@JsonProperty
	private Boolean active;

}
