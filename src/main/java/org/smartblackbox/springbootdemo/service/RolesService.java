package org.smartblackbox.springbootdemo.service;

import java.util.List;
import java.util.Optional;

import org.smartblackbox.springbootdemo.datamodel.auth.Role;
import org.smartblackbox.springbootdemo.datamodel.enums.RoleType;
import org.smartblackbox.springbootdemo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Copyright (C) 2024  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org>
 *
 */
@Service
public class RolesService {

	@Autowired
	private RoleRepository repository;

	public List<Role> findAll() {
		return repository.findAll();
	}

	public List<Role> findAll(boolean sorted) {
		return sorted? repository.findAllSorted() : repository.findAll();
	}

    public Optional<Role> findById(long id) {
        return repository.findById(id);
    }

    public Role save(Role role) {
        return repository.save(role);
    }

    public void deleteById(Long id) {
    	repository.deleteById(id);
    }

    public Optional<Role> findByRole(RoleType role) {
        return repository.findByRole(role);
    }

	public String getRolesString() {
		String result = "";

		for (Role role : findAll(true)) {
			result += (result.isEmpty()? "" : ", ") + role.getType().getName(); 
		}
		return result;
	}
	
}
