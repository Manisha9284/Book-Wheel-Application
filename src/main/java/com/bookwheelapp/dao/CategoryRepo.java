package com.bookwheelapp.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bookwheelapp.entities.Category;

public interface CategoryRepo extends JpaRepository<Category, Integer>{

	@Query("select c from Category c where c.categoryId = :categoryId")
	public Category getCategoryById(@Param("categoryId") Integer categoryId);
	
	
//	@Query("select c from Category c where c.user.userId = :userId")
//  	//current page = page
//  	//story per page = 5
//    public Page<Category> findStoriesbyCategoryByUserId(@Param("userId") int userId,Pageable pageable);
//	
//	@Query("select c from Category c where c.categoryId = :categoryId and c.user.userId = :userId")
//  	Optional<Category> findStoriesByIdAndUserId(@Param("categoryId") Integer categoryId, @Param("userId") Integer userId);	
}
