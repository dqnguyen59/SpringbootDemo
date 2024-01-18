package org.smartblackbox.springbootdemo.datamodel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 
 * @author Copyright (C) 2024  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org>
 *
 */
@Schema(description = "Sign In DTO")
@Data
public class SignInDTO {
	
	@JsonProperty
    private String username;

	@JsonProperty
    private String password;   

}