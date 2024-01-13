package com.bookwheelapp.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.mail.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.bookwheelapp.config.CustomUserDetails;
import com.bookwheelapp.dao.CategoryRepo;
import com.bookwheelapp.dao.ContactRepo;
import com.bookwheelapp.dao.StoryRepo;
import com.bookwheelapp.dao.UserRepository;
import com.bookwheelapp.entities.Category;
import com.bookwheelapp.entities.Contact;
import com.bookwheelapp.entities.Role;
import com.bookwheelapp.entities.Story;
import com.bookwheelapp.entities.User;
import com.bookwheelapp.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
//@RequestMapping("/user")
public class UserController {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private StoryRepo storyRepo;
	
	@Autowired
	private ContactRepo contactRepo;

	
//  1  :  Handler to adding common data to response 
	@ModelAttribute
	public void addCommonData(Model model,Principal principal) {
		
		String userName =principal.getName();
		 System.out.println("USERNAME " +userName);
		 
		 //get user using username(email)
		 User user = userRepository.getUserByUsername(userName);
		 
		 System.out.println("USER "+user);
		 
		 model.addAttribute("user", user);
	
	}

//  2  :  Handler for user dashboard home
//	@RequestMapping("/author/index")
	@RequestMapping(value = {"/author/index","/admin/index"}, method = RequestMethod.GET)
	public String dashboard(Model model, Principal principal) {
	    
		// Get the CustomUserDetails from the principal
//		CustomUserDetails customUserDetails = (CustomUserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();

		// Get the user and role from CustomUserDetails
//		Role user = customUserDetails.getUser();
//		Role userRole = user.getRole();
		
		CustomUserDetails customUserDetails = (CustomUserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
	    User user = customUserDetails.getUser();
	    Role userRole = user.getRole();
	    
	    String role=userRole.getRoleName();
	   
	    model.addAttribute("isAdmin", "ROLE_ADMIN".equals(role));
	    

		// Check if the user has the "ROLE_ADMIN" role
		if ("ROLE_ADMIN".equals(userRole.getRoleName())) {
		    // If user is admin, fetch all stories
		    List<Story> allStories = storyRepo.findAll();
		    model.addAttribute("allStories", allStories);
		    
		    return "special-roles/admin_dashboard";
		} else {
		    // If user is not an admin, fetch stories only for that user
		    List<Story> allStories = storyRepo.findStoryByUserId(user.getUserId());
		    model.addAttribute("allStories", allStories);
		    
		    return "special-roles/author_dashboard";
		}

	    
	    
	}



	

	

	
	// 5  :  User contact handler
//	@GetMapping("/author/contact")
@RequestMapping(value = {"/author/contact"}, method = RequestMethod.GET)
	public String contactUs(@ModelAttribute("contact") Contact contact,Model model,HttpSession session,Principal principal) {
		
		model.addAttribute("title", "Contact - Book Wheel Application");
		
		
		return "special-roles/contact";
	}



	
//	@RequestMapping("/normal-category")
//	public String Allcategories(Model model) {
//		
//		model.addAttribute("title", "Category - Book Wheel Application");
//		
//		// Fetch all categories from the Category table
//	    List<Category> allCategories = categoryRepo.findAll();
//	    model.addAttribute("allCategories", allCategories);
//		
//		return "category";
//	}
	
	
	
// 5  :  User contact handler
	@PostMapping("/process-contact")
//	@RequestMapping(value = {"/author/process-contact","/admin/process-contact"}, method = RequestMethod.POST)
	public String processContactUs(@ModelAttribute("contact") Contact contact,Model model,HttpSession session,Principal principal) {
		
		model.addAttribute("title", "Contact - Book Wheel Application");
		
		
		try { 
			   this.contactRepo.save(contact);
			
			
			//message success
		    session.setAttribute("message", new Message("Your message sent successfully !! Thank you...", "success"));
			
		} catch(Exception e) {
			e.printStackTrace();
			
			//message error
			session.setAttribute("message", new Message("Something went wrong !! Try again..", "danger"));
			return "special-roles/contact";
		}
		return "special-roles/contact";
	}
	
	

	
	
	
//  6  :  User profile handler
	@RequestMapping(value = {"/admin/profile", "/author/profile"}, method = RequestMethod.GET)
	public String userProfile(Model model,Principal principal) 
	{
		model.addAttribute("title","Profile Image");
		
//		CustomUserDetails customUserDetails = (CustomUserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
//	    User user = customUserDetails.getUser();
//	    Role userRole = user.getRole();
//	    model.addAttribute("isAdmin", "ROLE_ADMIN".equals(userRole));
		
		return "special-roles/profile";
	}
	
	
	//*************************************	
		//  7  :  Open update form handler for Profile
		//*************************************
		@PostMapping("/open-edit-profile/{userId}")
		public String updateProfile(@PathVariable("userId") Integer userId,Model model,Principal principal,HttpSession session ) {
			
			model.addAttribute("title", "Write - Book Wheel Application");
			model.addAttribute("categories",new Category());
			System.out.println(userId);
			
			try {
			   User user2 = this.userRepository.findByUserId(userId);

			        model.addAttribute("user", user2);
			        
			        session.setAttribute("message", new Message("Profile updated successfully !", "success"));
			        
			        return "special-roles/edit_profile";
			    } catch (Exception e) {
			    	
			        // Handle the case where the user was not found
			    	
			    	session.setAttribute("message", new Message("Something went wrong .....", "danger"));
			        return "special-roles/edit_profile";
			    }
			   
			
		}
	
	//*************************************	
	//  7  :  Process open update form handler for Profile i.e. edit profile
	//*************************************
	@RequestMapping(value = {"/uploads/edit-profile"}, method = RequestMethod.POST)
	public String editUserProfile(@ModelAttribute User user1,@RequestParam("profileImage") MultipartFile file,Model model,Principal principal,HttpSession session ) 
	{
		
		model.addAttribute("title","Edit Profile Details");
		
		
		try {
			    	
			        User user = this.userRepository.getUserByUsername(principal.getName());
			        model.addAttribute("user", user);
			        
			        
			        
			        
			        //***********************
			        // Get old user details
			        //***********************
			        User oldUserDetail = this.userRepository.findById(user.getUserId()).orElse(null);
			        if (oldUserDetail == null) {
			            session.setAttribute("message", new Message("User not found.", "danger"));
			            return "special-roles/edit_profile";
			        }
			        
			        if (!file.isEmpty()) {
			        	
			            // Delete old story image
//			            File oldImageFile = new ClassPathResource("static/images").getFile();
			            
			            File oldImageFile = new File("myuploads");
			            File toDelete = new File(oldImageFile, oldUserDetail.getImageUrl());
			            toDelete.delete();

			            // Save new user image
//			            File saveDirectory = new ClassPathResource("static/images").getFile();
//			            Path path = Paths.get(saveDirectory.getAbsolutePath() + File.separator + file.getOriginalFilename()+user.getUserId());
//			            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//			            
//			            System.out.println(path);
//			            user.setImageUrl(file.getOriginalFilename()+user.getUserId());
			            
			         // Construct new image name with the current date
			            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			            String currentDate = sdf.format(new Date());

			            String originalFilename = file.getOriginalFilename();
			            String filenameWithoutExtension = originalFilename.substring(0, originalFilename.lastIndexOf("."));
			            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

			            // Append the current date and user's ID to the original filename
			            String newFilename = filenameWithoutExtension + "_" + currentDate + "_" + user.getUserId() + extension;

			            // Save new story image
//			            File saveDirectory = new ClassPathResource("static/images").getFile();
			            
			            File saveDirectory = new File("myuploads"); // Changed from ClassPathResource to File
			            Path path = Paths.get(saveDirectory.getAbsolutePath() + File.separator + newFilename);
//			            Path path = Paths.get(saveDirectory.getAbsolutePath() + File.separator + newFilename);
			            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			            
			            System.out.println(path);
			            user.setImageUrl(newFilename);
			            
			            
			        } else {
			        	
			            // Use default image from old details 
			            user.setImageUrl(oldUserDetail.getImageUrl());
			        }

			        
//			        user.setUsername(user);
//			        this.storyRepo.save(user);
			        
			        
			        this.userRepository.save(user);
			        
			        session.setAttribute("message", new Message("Your Profile is updated successfully !!", "alert-success"));

			        
			        
			    } catch (Exception e) 
			    {
			        e.printStackTrace();
			        
			        session.setAttribute("message", new Message("Something went wrong .....", "danger"));
			    }

		
		return "special-roles/edit_profile";
	}
	
	
	//  8 : change password handler
	//@PostMapping("/change-password")
	@RequestMapping(value = "/change-password", method = RequestMethod.POST)
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
	                             @RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {
	    System.out.println("OLD PASSWORD " + oldPassword);
	    System.out.println("NEW PASSWORD " + newPassword);

	    User currentUser = this.userRepository.getUserByUsername(principal.getName());

	    System.out.println(currentUser.getPassword());

	    if (this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
	        // Change the password
	        currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
	        this.userRepository.save(currentUser);

	        session.setAttribute("message", new Message("Your password is successfully changed...", "alert-success"));

	        System.out.println("current user :"+currentUser.getRole().getRoleName());
	        
	        // Check the user's role and redirect accordingly
	        if (currentUser.getRole().getRoleName().equals("ROLE_ADMIN")) {
	            return "redirect:/admin/index";
	        } else {
	            return "redirect:/author/index";
	        }
	    } else {
	        // Error
	        session.setAttribute("message", new Message("Please enter the correct old password !!", "alert-danger"));
	        if (currentUser.getRole().getRoleName().equals("ROLE_ADMIN")) {
	            return "redirect:/admin/settings";
	        } else {
	            return "redirect:/author/settings";
	        }
	    }
	}
		

		
		//*************************************
		//  9  :  List of Authors handler
		//*************************************
		@RequestMapping("/admin/authors-list/{page}")
		public String ListOfAuthors(@PathVariable("page") Integer page,@ModelAttribute("user") User user ,Model model) {

			model.addAttribute("title", "List of stories - Book Wheel Application");
			    
			Pageable pageable = PageRequest.of(page, 4);

			// Directly passing "pending" to findByStatus method
//			    Page<Story> stories = this.storyRepo.findByStatus("pending", pageable);
			
			 Page<User> users = this.userRepository.findAll( pageable);
			    
			    
			    
			    model.addAttribute("users", users);
			    model.addAttribute("currentPage", page);
			    model.addAttribute("totalPages", users.getTotalPages());

			    return "special-roles/Author_List";
		}
	
	//  9  :  Start free trial handler
//	@GetMapping("/startTrial")
//		    public String startTrial(Model model ,Principal principal) {
//			  
//			// you'd retrieve the logged-in user in a real application
//		        User user = this.userRepository.getUserByUsername(principal.getName());
//		        
//		        user.setTrialEndDate(LocalDate.now().plusMonths(1));
//		        user.setIsTrialActive(true);
//		        
//		        userRepository.save(user);
//		        model.addAttribute("user", user);
//		        return "trialInfo";
//		    }
//
//	//  10  :  Checking if  free trial is active handler
//		    @GetMapping("/checkTrialStatus")
//		    public String checkTrialStatus(Model model,Principal principal) {
//		        // you'd retrieve the logged-in user in a real application
//		    	
//		    	User user = this.userRepository.getUserByUsername(principal.getName());
//		    	
//		        if (user.getIsTrialActive() && LocalDate.now().isAfter(user.getTrialEndDate())) {
//		            user.setIsTrialActive(false);
//		            userRepository.save(user);
//		        }
//		        model.addAttribute("user", user);
//		        return "trialInfo";
//		    }
	
}
