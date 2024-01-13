package com.bookwheelapp.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.bookwheelapp.entities.Role;
import com.bookwheelapp.entities.User;

public class CustomUserDetails implements UserDetails{
	
	private User user;
	
	

	public CustomUserDetails(User user) {
		super();
		this.user = user;
	}
	
	public User getUser() {
        return this.user;
    }
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
	    Role userRole = this.user.getRole();
	    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userRole.getRoleName());
	    return Collections.singletonList(authority);
	}


//	@Override
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//		
//	List<SimpleGrantedAuthority>	authorities = this.user.getRoles().stream().map((role)-> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toList());
//		
//		return authorities;
//	}
	
//	@Override
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//	    // Create a list and add the SimpleGrantedAuthority from user's role_name
//	    List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(this.user.getRole_name()));
//	    return authorities;
//	}

	@Override
	public String getPassword() {
		
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}


}
