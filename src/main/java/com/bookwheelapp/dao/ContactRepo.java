package com.bookwheelapp.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bookwheelapp.entities.Contact;

public interface ContactRepo extends JpaRepository<Contact, Integer>{

	
	@Query("select c from Contact c where c.contactId = :contactId")
	public Contact getContactById(@Param("contactId") Integer contactId);
	
	
	
//	@Query("select c from Contact c where c.id = :contactId and c.user.userId = :userId")
//  	Optional<Contact> findByIdAndUserId(@Param("contactId") Integer contactId, @Param("userId") Integer userId);
}
