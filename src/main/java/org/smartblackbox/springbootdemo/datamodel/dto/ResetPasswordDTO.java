package org.smartblackbox.springbootdemo.datamodel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 
 * @author Copyright (C) 2024  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org>
 *
 */
@Schema(description = "Reset Password DTO")
@Data
public class ResetPasswordDTO {

	@JsonProperty
	private String oldPassword;

	@JsonProperty
	private String newPassword;

}
