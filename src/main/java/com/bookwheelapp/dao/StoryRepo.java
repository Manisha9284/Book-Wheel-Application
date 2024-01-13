package com.bookwheelapp.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bookwheelapp.entities.Story;


public interface StoryRepo extends JpaRepository<Story, Integer>{

	
	
	  	@Query("select s from Story s where s.user.userId = :userId")
	  	//current page = page
	  	//story per page = 5
	    public Page<Story> findByUser(@Param("userId") int userId,Pageable pageable);
	  	
	  	@Query("select s from Story s where s.user.userId = :userId")
	    public List<Story> findStoryByUserId(@Param("userId") int userId);
	  	
	  	@Query("select s from Story s where s.story_id = :story_id")
	    public Story findByStoryId(@Param("story_id") int story_id);
	  	
	  	
	  	@Query("select s from Story s where s.status = :status ")
	  	Page<Story> findByStatus(@Param("status") String status,Pageable pageable);
	  	
	  	@Query("select s from Story s where s.id = :storyId and s.user.userId = :userId")
	  	Optional<Story> findByIdAndUserId(@Param("storyId") Integer storyId, @Param("userId") Integer userId);
	  	
	  	@Query("select s from Story s where s.category.categoryId = :categoryId  and s.user.userId = :userId")
	  	List<Story> findStoriesByCategoryIdAndUserId(@Param("categoryId") Integer categoryId, @Param("userId") Integer userId);
	  	
	  	@Query("select s from Story s where s.category.categoryId = :categoryId ")
	  	public List<Story> findByCategory( @Param("categoryId") int categoryId);
	  	
	  	
	  	
	  	
	  	
	  	

	
	    
}
