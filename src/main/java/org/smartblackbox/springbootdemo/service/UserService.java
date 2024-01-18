package org.smartblackbox.springbootdemo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.smartblackbox.springbootdemo.datamodel.auth.Role;
import org.smartblackbox.springbootdemo.datamodel.auth.User;
import org.smartblackbox.springbootdemo.datamodel.dto.ResetPasswordDTO;
import org.smartblackbox.springbootdemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Copyright (C) 2024  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org>
 *
 */
@Service
public class UserService {

	@Autowired
	private UserRepository repository;

	/**
	 * If pageNo is null, then default page 0 is used.
	 * If pageSize is null, then default value Integer.MAX_VALUE is used.
	 * If sortBy is null, then sorting will not be performed.
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param sortBy
	 * @return
	 */
	public List<User> getPage(Integer pageNo, Integer pageSize, String sortBy) {
		if (pageNo == null) pageNo = 0;
		if (pageSize == null) pageSize = Integer.MAX_VALUE;
		
		Pageable paging;
		if (sortBy == null || sortBy.isEmpty()) {
	        paging = PageRequest.of(pageNo, pageSize);
		}
		else {
	        paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
		}

        Page<User> pagedResult = repository.findAll(paging);

        if (pagedResult.hasContent()) {
            return pagedResult.getContent();
        }
        return new ArrayList<User>();
	}

    public Optional<User> findById(int id) {
        return repository.findById(id);
    }

    public User save(User user) {
        return repository.save(user);
    }

    public void deleteById(Long id) {
    	repository.deleteById(id);
    }

    public Optional<User> findByUsername(String username) {
        return repository.findByUserName(username);
    }

	public Boolean isActive(int id) {
		User user = findById(id).orElseThrow();
		return user.isActive();
	}

	public boolean resetPassword(int id, ResetPasswordDTO resetPasswrodDTO) {
		User user = findById(id).orElseThrow();
		
		if (user.getPassword() == resetPasswrodDTO.getOldPassword()) {
			user.setPassword(resetPasswrodDTO.getNewPassword());
			repository.saveAndFlush(user);
			return true;
		}
		
		return false;
	}

	public String getAllRolesString(int id) {
		String result = "";
		List<Role> roles = repository.getAllRoles(id);

		for (Role role : roles) {
			result += (result.isEmpty()? "" : ", ") + role.getName().toString(); 
		}
		return result;
	}

}
