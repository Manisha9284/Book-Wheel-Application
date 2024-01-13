package com.bookwheelapp.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bookwheelapp.config.CustomUserDetails;
import com.bookwheelapp.dao.CategoryRepo;
import com.bookwheelapp.dao.UserRepository;
import com.bookwheelapp.entities.Category;
import com.bookwheelapp.entities.Role;
import com.bookwheelapp.entities.User;

@Controller
public class CategoryController {
	
	@Autowired
	private CategoryRepo categoryRepo;
	
	@Autowired
	private UserRepository userRepo;

	
//*************************************
//	1.Showing all categories handler
//*************************************
//	@RequestMapping("/category")
	@RequestMapping(value = {"/admin/category", "/author/category"}, method = RequestMethod.GET)
	public String categories(Model model,Principal principal) {
		
		model.addAttribute("title", "Category - Book Wheel Application");
		
		CustomUserDetails customUserDetails = (CustomUserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
	    User user = customUserDetails.getUser();
	    Role userRole = user.getRole();
	    
	    String role = userRole.getRoleName();
	   
	    System.out.println("User Role "+role);
	    
	    model.addAttribute("isAdmin", "ROLE_ADMIN".equals(role));
	    
	    System.out.println("isAdmin "+"ROLE_ADMIN".equals(role));
		
		// Fetch all categories from the Category table
	    List<Category> allCategories = categoryRepo.findAll();
//	    System.out.println(allCategories);
	    model.addAttribute("allCategories", allCategories);
		
		return "special-roles/category";
	}
	
	
	
	//*************************************
//	2.Showing all categories handler
//*************************************
	@RequestMapping("/normal-category")
	public String Allcategories(Model model) {
		
		model.addAttribute("title", "Category - Book Wheel Application");
		
		// Fetch all categories from the Category table
	    List<Category> allCategories = categoryRepo.findAll();
	    model.addAttribute("allCategories", allCategories);
		
		return "category";
	}
	

	
	
	
}
