package org.smartblackbox.springbootdemo.repository;

import java.util.List;
import java.util.Optional;

import org.smartblackbox.springbootdemo.datamodel.auth.Role;
import org.smartblackbox.springbootdemo.datamodel.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 
 * @author Copyright (C) 2024  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org>
 *
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	@Query("SELECT u FROM User u WHERE u.id = :id")
	public Optional<User> findById(@Param("id") long id);
	
	@Query("SELECT u FROM User u WHERE u.username = :username")
	public Optional<User> findByUserName(@Param("username") String username);

	@Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username")
	public boolean existsByUserName(@Param("username") String username);
	
	@Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
	public boolean existsByEmail(@Param("email") String email);

	@Query("SELECT u.roles FROM User u WHERE u.id = :id")
	public List<Role> getAllRoles(@Param("id") long id);

}
