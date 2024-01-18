package org.smartblackbox.springbootdemo.repository.exceptions;

/**
 * 
 * @author Copyright (C) 2024  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org>
 *
 */
public class UserIdNotFoundException extends Exception {

	private static final long serialVersionUID = -8706704840297497934L;

	public UserIdNotFoundException(long id) {
		super(String.format("Error: user id '%d' not found!", id));
	}
	
}
