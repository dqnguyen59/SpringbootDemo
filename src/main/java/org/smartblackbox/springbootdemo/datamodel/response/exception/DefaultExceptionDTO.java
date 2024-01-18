package org.smartblackbox.springbootdemo.datamodel.response.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 
 * @author Copyright (C) 2024  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org>
 *
 */
@Schema(description = "Default Exception DTO")
@Data
public class DefaultExceptionDTO {
	
	@JsonProperty
	private String type;
	
	@JsonProperty
	private String title;
	
	@JsonProperty
	private int status;
	
	@JsonProperty
	private String detail;
	
	@JsonProperty
	private String instance;
	
	@JsonProperty
	private String description;
	
}
