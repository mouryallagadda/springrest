package com.venkat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class HelloWorldRestController {

	@Autowired
	UserService userService; //service will do all data retrieval manipulation work
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ResponseEntity<Void> doNotPrint(){
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	/**
	 * In post man give the exact url and method as get. 
	 * In the header part of postman request give key as accept and value as either application/xml or application/json 
	 * */
	@RequestMapping(value = "/user/", method = RequestMethod.GET)
	public ResponseEntity<List<User>> listAllUsers()
	{
		List<User> users = userService.findAllUsers();
		
		if(users.isEmpty()){
			return new ResponseEntity<List<User>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<User>>(users, HttpStatus.OK);
	}
	
	/**
	 * In post man give the exact url like /user/5 and method as get. 
	 * In the header part of postman request give key as accept and value as either application/xml or application/json 
	 * */
	
	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
	public ResponseEntity<User> getUser(@PathVariable("id") long id)
	{
		System.out.println("Fetching User with id " + id);
		User user = userService.findById(id);
		if(user == null)
		{
			System.out.println("User with id" + id + "not found");
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
	
	
	/**
	 * In post man give the exact url and method as post. 
	 * In the header part of postman request give key as content-type and value as application/json 
	 * In the body part of request select "raw" type radio button and give the input in json format.
	 * */
	@RequestMapping(value = "/user/", method = RequestMethod.POST)
	public ResponseEntity<Void> createUser(@RequestBody User user, UriComponentsBuilder ucBuilder)
	{
		System.out.println("Creating User " + user.getName());
		
		if(userService.isUserExist(user))
		{
			System.out.println("A User with name:" + user.getName() + "already exist");
			return new ResponseEntity<Void>(HttpStatus.CONFLICT);
			
		}
		userService.saveUser(user);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri());
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
	}
	
	
	/**
	 * In post man give the exact url like /user/5/up and method as put. 
	 * In the header part of postman request give key as content-type and value as application/json 
	 * In the body part of request select "raw" type radio button and give the input in json format.
	 * */
	@RequestMapping(value = "/user/{id}/up", method = RequestMethod.PUT)
	public ResponseEntity<User> updateUser(@PathVariable("id") long id, @RequestBody User user)
	{
		System.out.println("Updating User " + id);
		User currentUser = userService.findById(id);
		
		if(currentUser == null)
		{
			System.out.println("User with id " + id + " not found");
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
			
		}
		currentUser.setName(user.getName());
		currentUser.setAge(user.getAge());
		currentUser.setSalary(user.getSalary());
		userService.updateUser(currentUser);
		return new ResponseEntity<User>(currentUser, HttpStatus.OK);
	}
	
	/**
	 * In post man give the exact url like /user/5 and method as delete. 
	 * No header, No body 
	 * */
	@RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<User> deleteUser(@PathVariable("id") long id)
	{
		System.out.println("Fetching & Deleting User with id " + id);
		User user = userService.findById(id);
		if(user == null)
		{
			System.out.println("Unable to delete. User with id " + id + " not found");
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		}
		userService.deleteUserById(id);
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}	
	
	
}
