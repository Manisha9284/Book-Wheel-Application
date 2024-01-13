package com.bookwheelapp.entities;



import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Setter
@Getter
@NoArgsConstructor
public class Role {

	//reason to not using auto-incremented id bcaz we have few roles and want to fix them
	@Id
	private Integer role_id;
	
	private String roleName;
	
	
//	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//	@JoinTable(name="roles_of_user",
//	joinColumns = @JoinColumn(name="role",referencedColumnName = "role_id"),
//	inverseJoinColumns = @JoinColumn(name="user",referencedColumnName = "userId"))
//	private Set<User> users = new HashSet<>();
	

	
}
