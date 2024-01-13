package com.bookwheelapp.controller;

import java.security.Principal;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.bookwheelapp.config.CustomUserDetails;
import com.bookwheelapp.entities.Role;
import com.bookwheelapp.entities.User;

@ControllerAdvice
public class GlobalControllerAdvice {
	
	
	
	@ModelAttribute("isAdmin")
  public boolean isAdmin(Principal principal) {
      if (principal != null) {
          CustomUserDetails customUserDetails = (CustomUserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
          User user = customUserDetails.getUser();
          Role userRole = user.getRole();
          String role=userRole.getRoleName();
          
          return "ROLE_ADMIN".equals(role);
      }
      return false;
  }
	
	
	

//	 @ModelAttribute("isAdmin")
//	    public boolean isAdmin(Principal principal) {
//	        return hasRole(principal, "ROLE_ADMIN");
//	    }
//
//	    @ModelAttribute("isAuthor")
//	    public boolean isAuthor(Principal principal) {
//	        return hasRole(principal, "ROLE_AUTHOR");
//	    }
//
//	    @ModelAttribute("isReader")
//	    public boolean isReader(Principal principal) {
//	        return hasRole(principal, "ROLE_NORMAL");
//	    }
//
//	    private boolean hasRole(Principal principal, String role) {
//	        if (principal != null) {
//	            CustomUserDetails customUserDetails = (CustomUserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
//	            User user = customUserDetails.getUser();
//	            Role userRole = user.getRole();
//	            
//	            System.out.println("User Role : "+userRole.getRoleName());
//	            return role.equals(userRole.getRoleName());
//	        }
//	        return false;
//	    }

	
}
