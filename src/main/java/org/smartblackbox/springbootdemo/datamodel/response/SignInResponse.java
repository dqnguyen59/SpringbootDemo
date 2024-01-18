package org.smartblackbox.springbootdemo.datamodel.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 
 * @author Copyright (C) 2024  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org>
 *
 */
@Builder
@Schema(description = "Sign Response DTO")
@Data
public class SignInResponse {

	@JsonProperty
	private String token;

	@JsonProperty
    private long expiresIn;
    
	@JsonProperty
    private boolean resetPasswordRequired;

}