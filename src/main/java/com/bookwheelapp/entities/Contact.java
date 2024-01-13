package com.bookwheelapp.entities;

import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="contact")
@Setter
@Getter
@NoArgsConstructor
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO )
	private int contactId;
	
	@NotEmpty
	private String fullName;
	
	@Email(message = "Email address is not valid")
	@NotEmpty(message="Email is required  !!")
	private String emailAdd;
	
	private String subject;
	
	private String message;
}
