package com.bookwheelapp.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.bookwheelapp.config.CustomUserDetails;
import com.bookwheelapp.dao.CategoryRepo;
import com.bookwheelapp.dao.RoleRepo;
import com.bookwheelapp.dao.StoryRepo;
import com.bookwheelapp.dao.UserRepository;
import com.bookwheelapp.entities.Category;
import com.bookwheelapp.entities.Role;
import com.bookwheelapp.entities.Story;
import com.bookwheelapp.entities.User;
import com.bookwheelapp.helper.HtmlUtil;
import com.bookwheelapp.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
public class StoryController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private StoryRepo storyRepo;

	@Autowired
	private CategoryRepo categoryRepo;
	
	@Autowired
	private RoleRepo roleRepo;
	
	private Role role;

	
	
//	*********************************** AUTHOR HANDLERS METHODS *****************************************************
	
	
	//*************************************
    // 1 :  handler to write a new story
	//*************************************
	@RequestMapping("/author/write")
	public String writeStory(@ModelAttribute("story") Story story,Model model,HttpSession session,Principal principal ) {
		
		model.addAttribute("title", "Write - Book Wheel Application");
		model.addAttribute("story",new Story());
		model.addAttribute("categories",new Category());
		
//		CustomUserDetails customUserDetails = (CustomUserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
//	    User user = customUserDetails.getUser();
//	    String userRole = user.getRole();
//	    model.addAttribute("isAdmin", "ROLE_ADMIN".equals(userRole));
		
		try {

			 // Fetch all categories from the Category table
		    List<Category> allCategories = categoryRepo.findAll();
//		    System.out.println(allCategories);
		    model.addAttribute("allCategories", allCategories);
		    
		    story.setStatus("pending");
		    
		    
		} catch (Exception e) {
			
			//message error
			session.setAttribute("message", new Message("Error occurred while fetching categories ...", "alert-danger"));
			
			return "special-roles/write";
		}
		
	
		
	
		return "special-roles/write";
	}
	
	
	
	
	//*************************************
	//  2  : Handler for processing write story task
	//*************************************
	@Transactional
	@PostMapping("/author/process-story")
	public String processStory(@ModelAttribute("story") Story story,
	                           @RequestParam("storyImage") MultipartFile file,
	                           @RequestParam("categoryId") Integer categoryId,
	                           Principal principal,
	                           Model model,
	                           HttpSession session) {

	    try {
	        CustomUserDetails customUserDetails = (CustomUserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
	        User user = customUserDetails.getUser();

	        // Fetch all categories from the Category table
	        List<Category> allCategories = categoryRepo.findAll();
	        model.addAttribute("allCategories", allCategories);

	        // Fetch the selected category from the Category table by its id
	        Category selectedCategory = categoryRepo.getCategoryById(categoryId);
	        if (selectedCategory != null) {
	            story.setCategory(selectedCategory);
	        } else {
	            System.out.println("Category not found with id: " + categoryId);
	        }

	        // Processing and uploading file
	        if (file.isEmpty()) {
	            System.out.println("File is empty");
	        } else {
	            // The file to folder and update the name to story
//	            story.setImage(file.getOriginalFilename());

	            File saveFile = new File("myuploads");
	            if (!saveFile.exists()) {
	                saveFile.mkdir();
	            }

	            // Get the current date and format it
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	            String currentDate = sdf.format(new Date());

	            String originalFilename = file.getOriginalFilename();
	            String filenameWithoutExtension = originalFilename.substring(0, originalFilename.lastIndexOf("."));
	            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

	            // Append the current date to the original filename
	            String newFilename = filenameWithoutExtension + "_" + currentDate + extension;

	            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + newFilename);

	            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

	            System.out.println("Image saved to: " + path);
	            System.out.println("Image is uploaded");
	            
	            story.setImage(newFilename);
	        }

	        // Save the story
	        story.setUser(user);
	        user.getStories().add(story);
	        this.userRepository.save(user);

	        System.out.println("Data " + story);
	        System.out.println("Added to database");

	        // Message success
	        session.setAttribute("message", new Message("Your Story is added !! Add more...", "alert-success"));

	    } catch (Exception e) {
	        System.out.println("ERROR: " + e.getMessage());
	        e.printStackTrace();

	        // Message error
	        session.setAttribute("message", new Message("Something went wrong !! Try again..", "alert-danger"));
	    }

	    return "special-roles/write";
	}


	
	
	
	//*************************************	
	//  3  : Show stories handler
	//*************************************
	
	//per page = 5[n]
	//current page = 0[page]
	//	@GetMapping("/user/show-stories/{page}")
	@RequestMapping(value = {"/author/show-stories/{page}","/admin/show-stories/{page}"}, method = RequestMethod.GET)
	public String showStories(@PathVariable("page") Integer page,Model m,Principal principal) {
		
		m.addAttribute("title", "Show Stories");
		
//		CustomUserDetails customUserDetails = (CustomUserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
//	    User user = customUserDetails.getUser();
//	    String userRole = user.hasRoleWithName("ROLE_ADMIN");
//	    m.addAttribute("isAdmin", "ROLE_ADMIN".equals(userRole));
		
		CustomUserDetails customUserDetails = (CustomUserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        User user1 = customUserDetails.getUser();
        Role userRole = user1.getRole();
        m.addAttribute("isAdmin", "ROLE_ADMIN".equals(userRole));
		
		String userName = principal.getName();
		
		User user = this.userRepository.getUserByUsername(userName);
		
		Pageable pageable = PageRequest.of(page, 4);
		
		Page<Story>  stories= this.storyRepo.findByUser(user.getUserId(),pageable);
		 
//		User role = this.userRepository.getUserByRole(user.getRole());
		
		
//		m.addAttribute("role",role);
		m.addAttribute("stories",stories);
		m.addAttribute("currentPage",page);
		m.addAttribute("totalPages",stories.getTotalPages());
		
		
		
		return "special-roles/show_stories";
	}
	
	
	//*************************************	
	//  5  : delete story handler
	//*************************************
	@GetMapping("/author/delete/{story_id}")
	public String deleteStory(@PathVariable("story_id") Integer story_id, Model model,Principal principal,HttpSession httpSession) {
//		 
//		Story selectedStory = this.storyRepo.findByStoryId(story_id);
//		
//		System.out.println("Selected :"+selectedStory.getStatus());
		
		Optional<Story> storyOptional = this.storyRepo.findById(story_id);
		
		Story story = storyOptional.get();
		
		//check for logged in user
		String userName = principal.getName();
		
		User user = this.userRepository.getUserByUsername(userName);
		
		if(user.getUserId()== story.getUser().getUserId())
		{
			this.storyRepo.delete(story);
		}
		
		System.out.println("Deleted");
		httpSession.setAttribute("message", new Message("Story deleted successfully...","alert-success"));
		
		return "redirect:/author/show-stories/0";
	}
	
	
	//*************************************	
	//  6  :  Open update form handler for Story
	//*************************************
	@PostMapping("/author/update-story/{story_id}")
	public String updateStory(@PathVariable("story_id") Integer story_id,Model model,Principal principal ) {
		
		model.addAttribute("title", "Write - Book Wheel Application");
		model.addAttribute("categories",new Category());
		
		
		 // Fetch all categories from the Category table
	    List<Category> allCategories = categoryRepo.findAll();
	    model.addAttribute("allCategories", allCategories);
		
	    
		
		 String userName = principal.getName();
		    User user = this.userRepository.getUserByUsername(userName);
		    Optional<Story> storyOpt = this.storyRepo.findByIdAndUserId(story_id, user.getUserId());

		    if(storyOpt.isPresent()) {
		        Story storyToUpdate = storyOpt.get();
		        model.addAttribute("story", storyToUpdate);
		        return "special-roles/update_story";
		    } else {
		        // Handle the case where the story was not found
		        return "special-roles/update_story";
		    }
		   
		
	}
	
	
	
		//*************************************
		//  7  : update story Data handler{change story data to the database}
		//**************************************
	@PostMapping("/uploads/story-update")
	public String updateStoryDataHandler(@ModelAttribute Story story,
	                                     @RequestParam("storyImage") MultipartFile file,
	                                     @RequestParam("categoryId") Integer categoryId,
	                                     Model model, 
	                                     HttpSession session,
	                                     Principal principal) {

	    try {
	    	
	    	
	        User user = this.userRepository.getUserByUsername(principal.getName());
	        model.addAttribute("user", user);
	        
	        
	        //***********************
	        // Get old story details
	        //***********************
	        
	       
	        
	        Story oldStoryDetail = this.storyRepo.findById(story.getStory_id()).orElse(null);
	        if (oldStoryDetail == null) {
	            session.setAttribute("message", new Message("Story not found.", "danger"));
	            return "normal/update_story";
	        }

	        
	        
	        //******************************************************
	        // Fetch all categories from the Category table
	        //******************************************************
		    List<Category> allCategories = categoryRepo.findAll();
		    model.addAttribute("allCategories", allCategories);
	        
	        
	        
	        //******************************************************
	        // Fetch the category from the Category table by its id
	        //******************************************************
	        Category selectedCategory = categoryRepo.getCategoryById(categoryId);
	        
	        if (selectedCategory == null) {
	            session.setAttribute("message", new Message("Category not found with id: " + categoryId, "danger"));
	            return "special-roles/update_story";
	        }
	        story.setCategory(selectedCategory);

	        
	        //******************************************************
	        // Process the story image
	        //******************************************************
	        if (!file.isEmpty()) {
	            // Check if oldStoryDetail and its image are not null
	            if (oldStoryDetail != null && oldStoryDetail.getImage() != null) {
	                File oldImageFile = new File("myuploads");
	                File toDelete = new File(oldImageFile, oldStoryDetail.getImage());

	                // Check if old image exists
	                if (toDelete.exists() && toDelete.isFile()) {
	                    toDelete.delete();
	                } else {
	                    // Handle the situation if the image isn't in oldStories
	                    System.out.println("Old image doesn't exist. Uploading a new image.");
	                }
	            } else {
	                System.out.println("Either oldStoryDetail is null or its image is null. Proceeding to upload a new image.");
	            }

	            // Construct new image name with the current date
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	            String currentDate = sdf.format(new Date());

	            String originalFilename = file.getOriginalFilename();
	            String filenameWithoutExtension = originalFilename.substring(0, originalFilename.lastIndexOf("."));
	            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

	            // Append the current date and user's ID to the original filename
	            String newFilename = filenameWithoutExtension + "_" + currentDate + "_" + user.getUserId() + extension;

	            // Save new story image
	            File saveDirectory = new File("myuploads");
	            Path path = Paths.get(saveDirectory.getAbsolutePath() + File.separator + newFilename);
	            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	            
	            System.out.println(path);
	            story.setImage(newFilename);
	        } else {
	            if (oldStoryDetail != null) {
	                // Use image from old details 
	                story.setImage(oldStoryDetail.getImage());
	            } 
	                System.out.println("No image to set. oldStoryDetail is null.");
	            
	        }
	        
	        story.setUser(user);
	        this.storyRepo.save(story);
	        
	        session.setAttribute("message", new Message("Your story is updated successfully !!", "alert-success"));

	        
	        
	    } catch (Exception e) 
	    {
	        e.printStackTrace();
	        
	        session.setAttribute("message", new Message("Something went wrong .....", "danger"));
	        
	        return "special-roles/update_story";
	    }

	    return "special-roles/update_story";
	}

	
//******************************************* ADMIN HANDLER METHODS ***************************************************
	
	
	
	//*************************************
	//  8  :  Pending stories handler
	//*************************************
//	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping("/admin/pending-stories/{page}")
	public String ListOfStories(@PathVariable("page") Integer page, Model model,Principal principal,Authentication authentication) {

	    model.addAttribute("title", "List of stories - Book Wheel Application");
	   
	    
	    // Check if the user is an ADMIN
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
        	System.out.println("User is admin from show pending story");
            model.addAttribute("isAdmin", true);
        } else {
            model.addAttribute("isAdmin", false);
        }
        
	    
	    Pageable pageable = PageRequest.of(page, 4);

	    // Directly passing "pending" to findByStatus method
	    Page<Story> stories = this.storyRepo.findByStatus("pending", pageable);
	    
	    model.addAttribute("stories", stories);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", stories.getTotalPages());

	    return "special-roles/show_pending_stories";
	    
//	    return "redirect:/admin/pending-stories/0";
	}
	
	
//	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping("/admin/update-status")
	public String UpdateStoryStatus(@ModelAttribute Story story,@RequestParam String status, Model model, Authentication authentication, HttpSession session) {

	    model.addAttribute("title", "Update story status - Book Wheel Application");
	    
	    // Check if the user is an ADMIN
	    if (authentication != null && authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
	        model.addAttribute("isAdmin", true);
	    } else {
	        model.addAttribute("isAdmin", false);
	    }
	     
	    Story selectedStory = this.storyRepo.findByStoryId(story.getStory_id());
	    
	    System.out.println("Story to be updated :"+selectedStory);
	    
	    if (selectedStory != null) {
	        selectedStory.setStatus(status);
	        this.storyRepo.save(selectedStory);
	        
	        session.setAttribute("message", new Message("Story updated successfully: " + story.getStory_id(), "success"));
	    } else {
	        session.setAttribute("message", new Message("Story not found: " + story.getStory_id(), "danger"));
	    }
	    
	    
	    
	    model.addAttribute("story", selectedStory);
	    
	    return "redirect:/admin/pending-stories/0";
	    
	}

	
	

	
	
//****************************************** NORMAL USER HANDLER METHODS **********************************************
	
	//****************************************************
	//		9.Handler to show all stories  categorywise 
	//****************************************************
//	@GetMapping("/stories/category/{categoryId}")
//	public String categorywiseListOfAllStories(
//			@ModelAttribute("story") Story story,
//			@ModelAttribute("category") Category category,
//			@PathVariable("categoryId") Integer categoryId,
//			Model model,
//			Principal principal,
//			HttpSession session,
//			Authentication authentication)
//	{
//		
////		CustomUserDetails customUserDetails = (CustomUserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
////        User user1 = customUserDetails.getUser();
////        Role userRole = user1.getRole();
////        model.addAttribute("isAdmin", "ROLE_ADMIN".equals(userRole));
////		
//		 // Check if the user is an ADMIN
//        if (authentication != null && authentication.getAuthorities().stream()
//                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
//            model.addAttribute("isAdmin", true);
//        } else {
//            model.addAttribute("isAdmin", false);
//        }
//
//			try {
//
//				User user = this.userRepository.getUserByUsername(principal.getName());
//				
//				System.out.println("Category Id : "+categoryId);
//				
////				System.out.println("User Id : "+user.getUserId());
//				List<Story> stories  = this.storyRepo.findByCategory(categoryId);
//				 
//				 System.out.println(stories);
//				 
//				 model.addAttribute("stories", stories);
//				 
//			
//				 return "special-roles/categorywise_stories";
//				 
//			} catch (Exception e) {
//				
//				e.printStackTrace();
//				
//				session.setAttribute("message", new Message("Something went wrong ...", "alert-danger"));
//			} 
//			
//		return "special-roles/categorywise_stories";
//	}
	
	@GetMapping("/stories/category/{categoryId}")
	public String categorywiseListOfAllStories(
	        @ModelAttribute("story") Story story,
	        @ModelAttribute("category") Category category,
	        @PathVariable("categoryId") Integer categoryId,
	        Model model,
	        Principal principal,
	        HttpSession session,
	        Authentication authentication) {

	    // Check if the user is an ADMIN
	    if (authentication != null && authentication.getAuthorities().stream()
	            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
	        model.addAttribute("isAdmin", true);
	    } else {
	        model.addAttribute("isAdmin", false);
	    }

	    try {
	        User user = this.userRepository.getUserByUsername(principal.getName());

	        System.out.println("Category Id : " + categoryId);
	        List<Story> stories = this.storyRepo.findStoriesByCategoryIdAndUserId(categoryId, user.getUserId());

	        System.out.println(stories);

	        if (stories.isEmpty()) {
//	            model.addAttribute("noStoryMessage", "No story found with this category");
	            session.setAttribute("message", new Message("No story found with this category", "alert-danger"));
	        } else {
	            model.addAttribute("stories", stories);
	        }

	        return "special-roles/categorywise_stories";

	    } catch (Exception e) {
	        e.printStackTrace();
	        session.setAttribute("message", new Message("Something went wrong ...", "alert-danger"));
	    }

	    return "special-roles/categorywise_stories";
	}

	
		

	//****************************************************
	//		10.Showing single  story to read handler
	//****************************************************
	@GetMapping("/stories/{storyId}")
	public String readStory(@PathVariable("storyId") Integer storyId, Model model,HttpSession session) {
		
	    Optional<Story> singleStoryOptional = this.storyRepo.findById(storyId);
	    
	    
	    if (singleStoryOptional.isPresent()) {
	        Story singleStory = singleStoryOptional.get();
	        model.addAttribute("story", singleStory);
	        return "special-roles/Single_story";
	    } else {
	    	
	    	session.setAttribute("message", new Message("Something went wrong, cannot find the story .....", "danger"));
	        
	    	return "special-roles/Single_story"; // replace with your error page name or redirect
	    }
	}

	
	
}
