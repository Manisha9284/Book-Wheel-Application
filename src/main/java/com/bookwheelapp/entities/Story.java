package com.bookwheelapp.entities;


import java.io.Serializable;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.Setter;

@Entity
@Table(name="stories")
@Setter
@Getter
@NoArgsConstructor
public class Story {
	

	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int story_id;
	
	@Column(name = "story_title", length = 100, nullable = false)
	private String story_title;
	
	@Column( name = "description" ,length = 500)
	private String story_description;
	
	@Column(length =100000 )
	private String  story_content;
	
	private String image;
	
	private String status;
	
	@ManyToOne( fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id")
	private Category category;
	
	@ManyToOne( fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	@JsonIgnore
	private User user;

	
	
}
