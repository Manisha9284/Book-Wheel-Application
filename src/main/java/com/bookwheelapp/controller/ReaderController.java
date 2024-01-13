package com.bookwheelapp.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bookwheelapp.dao.ContactRepo;
import com.bookwheelapp.dao.StoryRepo;
import com.bookwheelapp.entities.Category;
import com.bookwheelapp.entities.Contact;
import com.bookwheelapp.entities.Story;
import com.bookwheelapp.entities.User;
import com.bookwheelapp.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
public class ReaderController {


	@Autowired
	private ContactRepo contactRepo;
	
	@Autowired
	private StoryRepo storyRepo;
	

	@RequestMapping(value = {"/reader/contact"}, method = RequestMethod.GET)
	public String contactUsReader(@ModelAttribute("contact") Contact contact,Model model,HttpSession session,Principal principal) {
		
		model.addAttribute("title", "Contact - Book Wheel Application");
		
		
		return "contact";
	}




//5  :  User contact handler
	@PostMapping("/process-reader-contact")
	public String processContactUsReader(@ModelAttribute("contact") Contact contact,Model model,HttpSession session,Principal principal) {
		
		model.addAttribute("title", "Contact - Book Wheel Application");
		
		
		try { 
			   this.contactRepo.save(contact);
			
			
			//message success
		    session.setAttribute("message", new Message("Your message sent successfully !! Thank you...", "success"));
			
		} catch(Exception e) {
			e.printStackTrace();
			
			//message error
			session.setAttribute("message", new Message("Something went wrong !! Try again..", "danger"));
			return "contact";
		}
		return "contact";
	}
	
	
	//handler for serving image with right media type
		@RequestMapping(value = {"/uploads/{imageName}","/author/{images}"}, method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
		public ResponseEntity<byte[]> serveImage(@PathVariable String imageName) throws IOException {
		    
		    File imageFile = new File("myuploads/" + imageName);
		    
		    if (!imageFile.exists() || !imageFile.isFile()) {
		        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		    }

		    byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
		    
		    HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.IMAGE_JPEG);

		    return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
		}
	
	//****************************************************
	//		9.Handler to show all stories  categorywise 
	//****************************************************
	@GetMapping("/reader-stories/category/{categoryId}")
	public String categorywiseListOfAllReaderStories(
			@ModelAttribute("story") Story story,
			@ModelAttribute("category") Category category,
			@PathVariable("categoryId") Integer categoryId,
			Model model,
			HttpSession session)
	{
			try {

				
				System.out.println("Category Id : "+categoryId);
				
//				System.out.println("User Id : "+user.getUserId());
				List<Story> stories  = this.storyRepo.findByCategory(categoryId);
				 
				 System.out.println(stories);
				 
				 model.addAttribute("stories", stories);
				 
			
				 return "categorywise_stories";
				 
			} catch (Exception e) {
				
				e.printStackTrace();
				
				session.setAttribute("message", new Message("Something went wrong ...", "alert-danger"));
			} 
			
		return "categorywise_stories";
	}
	
	
	//****************************************************
	//		10.Showing single  stoty to reader handler
	//****************************************************
	@GetMapping("/reader-stories/{storyId}")
	public String readStoryReaderHandler(@PathVariable("storyId") Integer storyId, Model model,HttpSession session) {
		
	    Optional<Story> singleStoryOptional = this.storyRepo.findById(storyId);
	    
	    
	    if (singleStoryOptional.isPresent()) {
	        Story singleStory = singleStoryOptional.get();
	        model.addAttribute("story", singleStory);
	        return "read_story";
	    } else {
	    	
	    	session.setAttribute("message", new Message("Something went wrong, cannot find the story .....", "danger"));
	        return "read_story"; // replace with your error page name or redirect
	    }
	}
}
