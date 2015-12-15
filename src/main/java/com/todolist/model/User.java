package com.todolist.model;

import java.util.*;

import javax.persistence.*;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "user")
@NamedQuery(name = "selectAllUsers", query = "SELECT a FROM User a")
public class User implements UserDetails{

	public User() {
	}
	//@Length(min = 5, max = 45)
	private String username;

	//@Length(min = 5, max = 60)
	private String password;

	private String confirmPassword;

	//@NotEmpty
	//@Email
	private String email;

	private boolean enabled;

	private Set<UserRole> userRole = new HashSet<UserRole>(0);

	public User(String username, String password, boolean enabled) {
		this.username = username;
		this.password = password;
		this.enabled = enabled;
	}

	public User(String username, String password, boolean enabled,
			Set<UserRole> userRole) {
		this.username = username;
		this.password = password;
		this.enabled = enabled;
		this.userRole = userRole;
	}

	@Id
	@Column(name = "username", unique = true, nullable = false, length = 45)
	public String getUsername() {
		return this.username;
	}

	@Override
	@Transient
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	@Transient
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	@Transient
	public boolean isCredentialsNonExpired() {
		return true;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	@Transient
	public Collection<? extends GrantedAuthority> getAuthorities() {

		Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();

		for (UserRole role : userRole) {
			setAuths.add(new SimpleGrantedAuthority(role.getRole()));
		}

		List<GrantedAuthority> result = new ArrayList<GrantedAuthority>(
				setAuths);
		return result;
	}

	@Column(name = "password", nullable = false, length = 60)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "enabled", nullable = false)
	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
	public Set<UserRole> getUserRole() {
		return this.userRole;
	}

	public void setUserRole(Set<UserRole> userRole) {
		this.userRole = userRole;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
