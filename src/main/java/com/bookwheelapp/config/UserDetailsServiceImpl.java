package com.bookwheelapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.bookwheelapp.dao.UserRepository;
import com.bookwheelapp.entities.User;

public class UserDetailsServiceImpl implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//fetching user from db
		 User user  = userRepository.getUserByUsername(username);
		 
		 if(user == null)
		 {
			 throw new UsernameNotFoundException("Could not found user !!");
		 }
		
		 
		 CustomUserDetails customUserDetails = new CustomUserDetails(user);
		 
		return customUserDetails;
	}

}
