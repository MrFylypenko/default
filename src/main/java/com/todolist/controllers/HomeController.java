package com.todolist.controllers;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.todolist.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.todolist.model.Task;
import com.todolist.model.User;
import com.todolist.model.UserRole;

/**
 * Handles requests for the application home page.
 */
@Controller
@RequestMapping(value = "/")
public class HomeController {


	@RequestMapping ("/name")
	@ResponseBody
	String getMyName (){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName(); //get logged in username

		System.out.println(name);



		 auth = SecurityContextHolder.getContext()
				.getAuthentication();

		if (!(auth instanceof AnonymousAuthenticationToken)) {
			UserDetails userDetail = (UserDetails) auth.getPrincipal();

			System.out.println(userDetail.getUsername());
		}

		System.out.println();



		return name;
	}




	@Autowired
	UserService userService;

	@Autowired
	TaskService taskService;

	@Autowired
	UserRoleService userRoleService;

@Secured(value = "hasRole('Admin')")
	@RequestMapping(value = "reg2", method = RequestMethod.GET)
	public String homes2() {
		System.out.println("ss");
		return "redirect:/index";
	}



	@RequestMapping(value = "reg/{userName}", method = RequestMethod.GET)
	public String homes(@PathVariable("userName") final String userName) {


		System.out.println("work!!!!");

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		final Authentication authentication2 = authentication;



		User user3 = new User();
		user3.setUsername(userName);
		user3.setPassword("dddd");
		user3.setEnabled(true);

		Set<UserRole> userRoles = new HashSet<UserRole>();
		UserRole userRole = new UserRole();
		userRole.setRole("ROLE_USER");
		userRoles.add(userRole);

		user3.setUserRole(userRoles);


		final MyUserDetailsService userDetailsService = new MyUserDetailsService();

		final org.springframework.security.core.userdetails.UserDetails u =  userDetailsService.getUser(user3);



		Authentication trustedAuthentication = new Authentication () {
			private String name = userName;
			private Object details = authentication2.getDetails();


			private UserDetails user = u;
			private boolean authenticated = true;
			private Collection<? extends GrantedAuthority> authorities = user.getAuthorities();


			@ Override
			public String getName() {
				return name;
			}
			@ Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				return authorities;
			}
			@ Override
			public Object getCredentials() {
				return user.getPassword();
			}
			@ Override
			public Object getDetails() {
				return details;
			}
			@ Override
			public Object getPrincipal() {
				return user;
			}
			@ Override
			public boolean isAuthenticated() {
				return authenticated;
			}
			@ Override
			public void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
				this.authenticated = authenticated;
			}

		};



		//(user, authentication.getDetails())
		authentication = trustedAuthentication;
		SecurityContextHolder.getContext().setAuthentication(authentication);





		return "redirect:/index";
	}


	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		return "redirect:/index";
	}






@Autowired
	Vk vk;

	@RequestMapping(value = "/", method = RequestMethod.GET, params = {"code"})
	public String home2(@RequestParam(value = "code") String code) throws Exception {
		vk.getToken(code);

		return "redirect:/index";
	}






	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(Model model, HttpServletRequest request) {

		Map<String, String> mapUsers = new LinkedHashMap<String, String>();
		List<User> users = userService.getAllUsers();
		for (User user : users) {
			mapUsers.put(user.getUsername(), user.getUsername());
		}
		List<Task> tasks = taskService.getAllTasks();

		Task task = new Task();

		if (request.getRemoteUser() != null) {
			task.setUser(request.getRemoteUser());
		}

		model.addAttribute("tasks", tasks);
		model.addAttribute("mapUsers", mapUsers);
		model.addAttribute("task", task);
		model.addAttribute("page", "task.jsp");
		return "index";
	}

	@RequestMapping(value = "/index", method = RequestMethod.POST)
	public String indexPost(@Valid Task task, BindingResult result,
			Model model, HttpServletRequest request) {

		if (!result.hasErrors()) {
			task.setDate(new Date());
			task.setResult("");
			task.setResolved(false);
			taskService.addTask(task);
			task = new Task();
		}
		Map<String, String> mapUsers = new LinkedHashMap<String, String>();
		List<User> users = userService.getAllUsers();
		for (User user : users) {
			mapUsers.put(user.getUsername(), user.getUsername());
		}

		if (request.getRemoteUser() != null) {
			task.setUser(request.getRemoteUser());
		}
		List<Task> tasks = taskService.getAllTasks();

		model.addAttribute("task", task);
		model.addAttribute("tasks", tasks);
		model.addAttribute("mapUsers", mapUsers);
		model.addAttribute("page", "task.jsp");
		return "index";
	}

	@RequestMapping(value = "/taskdelete/{idTask}")
	public String deleteTask(@PathVariable("idTask") Integer idTask) {
		taskService.deleteTask(idTask);
		return "redirect:/index";
	}

	@RequestMapping(value = "/taskedit/{idTask}", method = RequestMethod.GET)
	public String taskEdit(@PathVariable("idTask") Integer idTask, Model model) {

		Map<String, String> mapUsers = new LinkedHashMap<String, String>();
		List<User> users = userService.getAllUsers();
		for (User user : users) {
			mapUsers.put(user.getUsername(), user.getUsername());
		}

		Task task = taskService.findTaskById(idTask);
		model.addAttribute("mapUsers", mapUsers);
		model.addAttribute("task", task);
		model.addAttribute("page", "taskedit.jsp");
		return "index";
	}

	@RequestMapping(value = "/taskedit/{idTask}", method = RequestMethod.POST)
	public String editTaskSave(@PathVariable("idTask") Integer idTask,
			@Valid Task task, BindingResult result, Model model) {		
		
		if (!result.hasErrors()) {
			Task taskLoaded = taskService.findTaskById(idTask);
			taskLoaded.setTitle(task.getTitle());
			taskLoaded.setDescription(task.getDescription());
			taskLoaded.setPerformer(task.getPerformer());
			taskLoaded.setResult(task.getResult());
			taskLoaded.setResolved(task.isResolved());
			
			taskService.updateTask(taskLoaded);
			return "redirect:/index";
		}
		model.addAttribute("idTask", idTask);
		model.addAttribute("task", task);
		model.addAttribute("page", "taskedit.jsp");
		return "index";
	}

	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public String registration(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		model.addAttribute("page", "registration.jsp");
		return "index";
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public String registrationsave(@Valid User user, BindingResult result,
			Model model) {

		if (user.getUsername() != null) {
			User userCheck = userService.findByUserName(user.getUsername());
			if (userCheck != null
					&& (userCheck.getUsername() == user.getUsername())) {
				result.rejectValue("username", "Exists.user.username",
						"An account already exists for this username.");
			}
		}

		if (!(user.getPassword().equals(user.getConfirmPassword()))) {
			result.rejectValue("confirmPassword", "Notmatch.user.password",
					"Password and Conform password is not match!");
		}

		if (!result.hasErrors()) {
			user.setPassword(userService.encode(user.getPassword()));
			user.setEnabled(true);
			userService.addUser(user);
			UserRole userRole = new UserRole();
			userRole.setRole("ROLE_USER");
			userRole.setUser(user);
			Set<UserRole> userRoleList = new HashSet<UserRole>();
			user.setUserRole(userRoleList);
			userRoleService.addUserRole(userRole);
			return "redirect:/login";
		}

		model.addAttribute("user", user);
		model.addAttribute("page", "registration.jsp");
		return "index";
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(
			@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout,
			HttpServletRequest request) {

		ModelAndView model = new ModelAndView();
		if (error != null) {
			model.addObject("error",
					getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION"));
		}
		if (logout != null) {
			model.addObject("msg", "You've been logged out successfully.");
		}
		model.addObject("page", "login.jsp");
		model.setViewName("index");
		return model;
	}

	private String getErrorMessage(HttpServletRequest request, String key) {

		Exception exception = (Exception) request.getSession()
				.getAttribute(key);

		String error = "";
		if (exception instanceof BadCredentialsException) {
			error = "Invalid username and password!";
		} else if (exception instanceof LockedException) {
			error = exception.getMessage();
		} else {
			error = "Invalid username and password!";
		}
		return error;
	}

	@RequestMapping(value = "/403", method = RequestMethod.GET)
	public ModelAndView accesssDenied() {

		ModelAndView model = new ModelAndView();

		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();

		if (!(auth instanceof AnonymousAuthenticationToken)) {
			UserDetails userDetail = (UserDetails) auth.getPrincipal();

			model.addObject("username", userDetail.getUsername());
		}

		model.addObject("page", "403.jsp");

		model.setViewName("index");
		return model;
	}

}
