package com.bookwheelapp.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bookwheelapp.entities.Category;
import com.bookwheelapp.entities.Role;

public interface RoleRepo extends JpaRepository<Role, Integer>{

//	 @Query("select u from User u where u.role = :role")
//	    List<Role> findByRole(@Param("role") String role);
	
	@Query("select u from Role u where u.role_id = :role_id")
	public Role getRoleById(@Param("role_id") Integer role_id);
	
	 
	 @Query("select r from Role r where r.roleName = :roleName")
		public Role getRoleByRoleName(@Param("roleName") String roleName);
	 
//	 @Query("select r from Role r where r.id = :roleId")
//		public Role getRoleById(@Param("roleId") Integer roleId);
}
