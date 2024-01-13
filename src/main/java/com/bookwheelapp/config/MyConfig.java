package com.bookwheelapp.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class MyConfig {

	
	@Bean
	public UserDetailsService getUserDetailService()
	{
		return new UserDetailsServiceImpl();	
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	
	
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider()
	{
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		
		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		
		return daoAuthenticationProvider;
	}
	
	@Bean
	public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception {
		
		return configuration.getAuthenticationManager();
	}
	
//	protected void configure(AuthenticationManagerBuilder auth)throws Exception{
//		auth.authenticationProvider(authenticationProvider());
//	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

	    http.authorizeRequests()
	        .requestMatchers("/admin/**").hasRole("ADMIN")
	        .requestMatchers("/user/**").hasRole("USER")
	        .requestMatchers("/author/**").hasRole("AUTHOR")
	        .requestMatchers("/**").permitAll()
	        .and()
	        .formLogin()
	        .loginPage("/login") // my custom login page
	        .loginProcessingUrl("/do_login") // submit my credentials on this url
	        .successHandler(new AuthenticationSuccessHandler() {
	            @Override
	            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
	                if (authentication.getAuthorities().stream()
	                    .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
	                    response.sendRedirect("/admin/index"); // redirect to specified url on success for ADMIN
	                }else if (authentication.getAuthorities().stream()
		                    .anyMatch(r -> r.getAuthority().equals("ROLE_AUTHOR"))) {
		                    response.sendRedirect("/author/index"); // redirect to specified url on success for ADMIN
		                }
	                else {
	                    response.sendRedirect("/"); // default success URL for others
	                }
	            }
	        })
	       // .defaultSuccessUrl("/user/index")
	        .failureUrl("/login-failed")
	        .and()
	        .csrf()
	        .disable();

	    http.authenticationProvider(daoAuthenticationProvider());
	    DefaultSecurityFilterChain build = http.build();

	    return build;
	}


	
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/**")
//        .addResourceLocations("classpath:/static/","classpath:/images/")
//        .setCachePeriod(0);
//    }
	
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
	        // For /uploads/** URL pattern
	        registry.addResourceHandler("/uploads/**")
	            .addResourceLocations("file:myuploads/");

	        // For /images/** URL pattern
	        registry.addResourceHandler("/author/**")
	            .addResourceLocations("file:myuploads/");
	    }
	
	
}
