package com.bookwheelapp.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bookwheelapp.entities.Story;
import com.bookwheelapp.entities.User;

public interface UserRepository  extends JpaRepository<User,Integer>{

	@Query("select u from User u where u.email = :email")
	public User getUserByUsername(@Param("email") String email);
	
	  @Query("select u from User u where u.userId = :userId")
      User findByUserId(@Param("userId") int userId);
	  
	  @Query("select u from User u where u.userId = :userId")
      Page<User> findByUserId(@Param("userId") int userId,Pageable pageable);
	
}
