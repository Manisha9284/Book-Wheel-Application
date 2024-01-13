package com.bookwheelapp.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="users")
@Setter
@Getter
@NoArgsConstructor

public class User implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO )
	private int userId;
	
//	@Column(nullable = false, length = 100)
	@NotEmpty
	@Size(min = 4, message = "Username must be minimum of 4 characters")
	private String username;
	
	@Email(message = "Email address is not valid")
	@NotEmpty(message="Email is required  !!")
	@Column(unique = true)
	private String email;
	
	@NotEmpty
//	@Size(min = 3, max = 10, message = "Password must be min of 3 chars and max of 10 chars")
//	@Pattern(regexp = "^[a-zA-Z0-9]{3,10}$", message = "Password can only contain alphanumeric characters")
	private String password;

	
//	@NotEmpty
//	@Size(min = 3, max = 10 ,message = "Password must be min of 3 chars and max of 10 chars")
//	private String confirm_password;
	
	private Boolean enabled;
	
	private String imageUrl;
	
//	@Column( name = "roleId")
//	@NotNull
//	private Role role;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roleId")
    private Role role;
	
	
//	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private  List<Story> stories = new ArrayList<>();




}
