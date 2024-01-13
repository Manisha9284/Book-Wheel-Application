package com.bookwheelapp.controller;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.origin.Origin;
import org.springframework.data.domain.Range.Bound;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bookwheelapp.config.CustomUserDetails;
import com.bookwheelapp.dao.CategoryRepo;
import com.bookwheelapp.dao.MyOrderRepo;
import com.bookwheelapp.dao.RoleRepo;
import com.bookwheelapp.dao.StoryRepo;
import com.bookwheelapp.dao.UserRepository;
import com.bookwheelapp.entities.Category;
import com.bookwheelapp.entities.MyOrder;
import com.bookwheelapp.entities.Role;
import com.bookwheelapp.entities.Story;
import com.bookwheelapp.entities.User;
import com.bookwheelapp.helper.Message;
import com.bookwheelapp.service.EmailService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import com.razorpay.*;

@Controller
//@RequestMapping("/admin")
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private StoryRepo storyRepo;
	
	@Autowired
	private CategoryRepo categoryRepo;
	
	@Autowired
	private RoleRepo roleRepo;
	
	@Autowired
	private MyOrderRepo myOrderRepo;
	
//	Random random = new Random(1000);
	

//*******************
//Home handler
//*******************
	@RequestMapping("/")
	public String home(Model model) {
		
		model.addAttribute("title", "Home - Book Wheel Application");
		
		
		// Fetch all categories from the Category table
	    List<Story> allStories = storyRepo.findAll();
//	    System.out.println(allCategories);
	    model.addAttribute("allStories", allStories);
	    
	    List<Category> allCategories = categoryRepo.findAll();
//	    System.out.println(allCategories);
	    model.addAttribute("allCategories", allCategories);
	    
		return "home";
	}
	
	
//*******************
//login handler
//*******************
	@GetMapping("/login")
	public String login(Model model) {
		
		model.addAttribute("title", "Login - Book Wheel Application");

		
		return "login";
	}
	
//*******************
//login handler
//*******************
	@PostMapping("/do_login")
	public String do_login(
	        @RequestParam("username") String email, 
	        @RequestParam("password") String password,
	        Model model, HttpSession session) {
	    
	    User user = userRepository.getUserByUsername(email);
	    
	    if(user != null && passwordEncoder.matches(password, user.getPassword())) {
	        // authentication successful
	        
	        // Check user role and redirect accordingly
	        String userRole = user.getRole().getRoleName();
	        
	        if ("ROLE_AUTHOR".equalsIgnoreCase(userRole)) {
	        	
	        	return "redirect:/author/index";
	        	
	        } else if ("ROLE_NORMAL".equalsIgnoreCase(userRole)) {
	        	
	            return "redirect:/";
	            
	        } else {
	            // Unknown role; handle appropriately
	        	
	        	session.setAttribute("message", new Message("Unknown role assigned. Contact support.", "danger"));
	            //model.addAttribute("errorMessage", "Unknown role assigned. Contact support.");
	            return "login";
	        }

	    } else {
	        // authentication failed
	        //model.addAttribute("errorMessage", "Invalid email or password.");
	        return " redirect:/login-failed";
	    }
	}


	//*******************
	//login-failed handler
	//*******************
	 @GetMapping("/login-failed")
	    public String loginFailed(@RequestParam(value = "error", required = false) String error, Model model) {
	        if (error != null && !error.trim().isEmpty()) {
	            model.addAttribute("errorMessage", error);
	        } else {
	            model.addAttribute("errorMessage", "Something went wrong during login try again......");
	        }
	        return "login-failed";
	    }

//*******************
//register handler
//*******************
	@RequestMapping("/register")
	public String register(Model model) {
		
		model.addAttribute("title", "Register - Book Wheel Application");
		model.addAttribute("user",new User());
		return "register";
	}

	
//******************************
//handlder for registegring user
//******************************
	@RequestMapping(value ="/do_register",method = RequestMethod.POST)
	public String registerUser(
			@ModelAttribute("user") @Valid User user,
			BindingResult bResult, 
			@RequestParam(value="agreement", defaultValue = "false") boolean agreement,
			@RequestParam(value="role_id" )Integer role_id,
			Model model,
			HttpSession session) {
		
		try {
			
			User existingUser = userRepository.getUserByUsername(user.getEmail());
	        if (existingUser != null) {
	            model.addAttribute("user", user);
	            session.setAttribute("message", new Message("Email already in use.Try with another email id!", "alert-danger"));
	            return "register";
	        }
			
		    // Fetch the selected category from the Category table by its id
		    Role selectedRole = roleRepo.getRoleById(role_id);
		    
		    System.out.println("selectedRole" +selectedRole);
		    if (selectedRole != null) {
		        user.setRole(selectedRole);
		    } else {
		        System.out.println("Role not found with id: " + role_id);
		    }
			if(!agreement)
			{
				System.out.println("You have not agreed the terms and conditions");
				throw new Exception("You have not agreed the terms and conditions");
			}
			
			
			if(bResult.hasErrors()) {
				System.out.println("ERROR" +bResult.toString());
				model.addAttribute("user", user);
				return "register";
				
			}
			
			
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println("Agreement" +agreement);
			System.out.println("USER" +user);
			
			User result = this.userRepository.save(user);
			
			model.addAttribute("user",new User());
			session.setAttribute("message", new Message("Successfully Registered !!", "alert-success") );
			
			return "register";
			
		}catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong !!" +e.getMessage(),"alert-danger"));

			return "register";
		}
	}

	
	
//*******************
//	Logout handler
//*******************
	@RequestMapping("/logout")
	public String logOut(Model model,HttpSession session) {
		
		//model.addAttribute("title", "Register - Book Wheel Application");
		session.setAttribute("message", new Message("You have been logged out successfully !!", "alert-success") );
		return "login";
	}
	
	
	
	// 4 : Handler for about page	
		@RequestMapping("/normal-about")
		public String about(Model model) {
			
			model.addAttribute("title", "About - Book Wheel Application");
			return "aboutUs";
		}
	
	
	

//***************************
//handler for open setting 
//****************************
//	@RequestMapping("/settings")
	@RequestMapping(value = {"/admin/settings","/author/settings","/normal/settings"}, method = RequestMethod.GET)
	public String openSettings(Model model,Principal principal)
	{
		CustomUserDetails customUserDetails = (CustomUserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
	    User user = customUserDetails.getUser();
	    Role userRole = user.getRole();
	    
	    String role=userRole.getRoleName();
	   
	    System.out.println("User Role "+role);
	    model.addAttribute("isAdmin", "ROLE_ADMIN".equals(role));
	    
	    System.out.println("isAdmin "+"ROLE_ADMIN".equals(role));
	    
		String name = principal.getName();
	    User user1 = this.userRepository.getUserByUsername(name);
		
	    model.addAttribute("user",user1);
		
		return "special-roles/settings";
	}
	
	
//***************************
//emial id form open handler
//****************************
	@RequestMapping("/forgot")
	public String openEmailForm()
	{
		return "forgot_email_form";
	}
	

//****************
//	Handler for send otp
//	*****************
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email,HttpSession session)
	{
		System.out.println("EMAIL: "+email);
		
		//generating otp of 4 digit
		
//		int otp = random.nextInt(99999);
		
		int otp = ThreadLocalRandom.current().nextInt(1000,9999);
		
		System.out.println("OTP : " +otp);
		
//		System.out.println("OTP: " +otp);
		
		//write code foe send otp to email...
		
		String subject = "OTP From BWAPP";
		
		
		String message = ""
				+"<div style='border:1px solid #e2e2e2; padding:20px'>"
				+"<h1>"
				+"OTP is "
				+"<b>"+otp
				+"<n>"
				+"<h1>"
				+"</div>";
		
		
		String to = email;
		
		boolean flag = this.emailService.sendEmail(subject, message, to);
		
			if(flag)
			{
				//to verify otp save otp to session so that we can compare it to otp comes from forgot_email_form 
				session.setAttribute("myotp", otp);
				session.setAttribute("email", email);
				return "verify_otp";
			}
			else {
			session.setAttribute("message", new Message("Check your email id !!" ,"alert-danger"));
//			session.setAttribute("message", "Check your email id !!");

			return "forgot_email_form";
			}
	}

	
	
//*******************
//	Verify otp handler
//	*********************

	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp,HttpSession session) {
		
		int myOtp = (int)session.getAttribute("myotp"); 
		
		
		System.out.println("User OTP " +otp);
		System.out.println("Our OTP " +myOtp);
		
		String email = (String)session.getAttribute("email");
		
		if(myOtp==otp)
		{
			//password change form
			
		    User user =	this.userRepository.getUserByUsername(email);
			
		    if(user==null) {
		    	//send error message
		    	session.setAttribute("message", new Message("User does not exist with this email id !!" ,"alert-danger"));

				return "forgot_email_form";
		    	
		    }else
		    {
		    	//send change password form
		    	
		    }
			
			 
			return "password_change_form";
		}else
		{
			session.setAttribute("message", new Message("You have entered wrong otp !!" ,"alert-danger"));
			
			return "verify_otp";
		}
	}
	
	
//*************************
//Change Password
//*************************
	
	@PostMapping("/set-newpassword")
	public String setNewPassword(@RequestParam("newpassword") String newpassword,HttpSession session)
	{
		String email = (String)session.getAttribute("email");
		
		User user = this.userRepository.getUserByUsername(email);
		
		user.setPassword(this.passwordEncoder.encode(newpassword));
		
		this.userRepository.save(user);
		
		session.setAttribute("message", new Message("Password changed successfully.. !!" ,"alert-success"));
		
		return "login";
	}
	
	
//Payment form api
	@RequestMapping("/subscribe")
	public String openSubscribeForm()
	{
		return "special-roles/subscribe";
	}
	
//create order for payment
	@PostMapping("/create_order")
	@ResponseBody//bcaz we are not returning any view from here
	public String createOrder(@RequestBody Map<String, Object> data,Principal principal )  throws Exception
	{
		//System.out.println("Hey order function executed");
		System.out.println(data);
		
		int amount = Integer.parseInt(data.get("amount").toString());
		
		var client = new RazorpayClient("rzp_test_GgvdjAWe7WEFiv","xHsJnb6W1hjrleVKR6AdBwVO");
		
		JSONObject orderRequest = new JSONObject();
		orderRequest.put("amount",amount*100);
		orderRequest.put("currency","INR");
		orderRequest.put("receipt", "txn_235425");
		
		//creating new order
		Order order = client.orders.create(orderRequest);
		System.out.println(order);
		
		//save the data to the database
		MyOrder myOrder = new MyOrder();
		
		myOrder.setAmount(order.get("amount")+"");
		myOrder.setOrderId(order.get("id"));
		myOrder.setPaymentId(null);
		myOrder.setStatus("created");
		myOrder.setUser(this.userRepository.getUserByUsername(principal.getName()));
		myOrder.setReceipt(order.get("receipt"));
		
		this.myOrderRepo.save(myOrder);
		
		//if you want to save this to your data...
		return order.toString();
	}
	
	
	//handler to update payment data in db
	@PostMapping("/update_order")
	public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data){
		
		MyOrder myOrder = this.myOrderRepo.findByOrderId(data.get("order_id").toString());
		
		myOrder.setPaymentId(data.get("payment_id").toString());
		myOrder.setStatus(data.get("status").toString());
		
		this.myOrderRepo.save(myOrder);
		
		System.out.println(data);
		return ResponseEntity.ok(Map.of("msg","updated"));
	}
	
	
}
