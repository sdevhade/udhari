package com.websystique.springmvc.controller;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.websystique.springmvc.model.User;
import com.websystique.springmvc.service.ItemService;
import com.websystique.springmvc.service.UserService;

@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    ItemService itemService;  //Service which will do all data retrieval/manipulation work
 
	@RequestMapping(value="/computers")
    public ResponseEntity<List<?>> listAllComputers() {
		System.out.println("*************************************ListAllComputers");
    	List<?> items = 	itemService.findItemsByCategory("computers");
        if(items.isEmpty()){
            return new ResponseEntity<List<?>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<List<?>>(items, HttpStatus.OK);
    }
	@RequestMapping(value="/sqldata")
    public ResponseEntity<List<?>> sqlData() {
		System.out.println("*************************************FetchSQLData");
		JdbcTemplate jdbcTemplate = UserService.getJdbcTemplate();
		String sql = "SELECT * FROM users";		
		List<?> listContact = jdbcTemplate.queryForList(sql);	 
		return new ResponseEntity<List<?>>(listContact, HttpStatus.OK);
    }
	
	@RequestMapping(value = "/creategroup", method = RequestMethod.POST)
	public ResponseEntity<String> creategroup(@RequestBody String string) {
	    System.out.println(string);
	    JsonReader reader = Json.createReader(new StringReader(string));        
        JsonObject jsonObj = reader.readObject();
        reader.close();        
		String value = UserService.addGroup(jsonObj);       
	    return new ResponseEntity<String>(value.toString(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public ResponseEntity<HashMap<String, String>> update(@RequestBody User user) {
	    HashMap<String,String> test = new HashMap<String, String>();	    
	    String validateUser = UserService.validateUser(user);
	    if(validateUser.equals("no_user")){
	    	UserService.addUser(user);
	    	test.put("status","user_added");
	    }
	    else if(validateUser.equals("invalid_password"))
	    	test.put("status",validateUser);
	    else
	    	test.put("status","user_exist");
	    return new ResponseEntity<HashMap<String, String>>(test, HttpStatus.OK);
	}
	@RequestMapping(value="/computers/{id}")
    public ResponseEntity<Object> findSpecificComputer(@PathVariable("id") long id) {
		System.out.println("*************************************findSpecificComputer");
    	Object item = 	itemService.findItemById(id, "computers");
        if(item == null){
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<Object>(item, HttpStatus.OK);
    }

	
	@RequestMapping(value="/phones")
    public ResponseEntity<List<?>> listAllPhones() {
		System.out.println("*************************************ListAllPhones");
    	List<?> items = 	itemService.findItemsByCategory("phones");
        if(items.isEmpty()){
            return new ResponseEntity<List<?>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<List<?>>(items, HttpStatus.OK);
    }

	@RequestMapping(value="/phones/{id}")
    public ResponseEntity<Object> findSpecificPhone(@PathVariable("id") long id) {
		System.out.println("*************************************findSpecificPhone");
    	Object item = 	itemService.findItemById(id, "phones");
        if(item == null){
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<Object>(item, HttpStatus.OK);
    }

	@RequestMapping(value="/printers")
    public ResponseEntity<List<?>> listAllPrinters() {
		System.out.println("*************************************ListAllPrinters");
    	List<?> items = 	itemService.findItemsByCategory("printers");
        if(items.isEmpty()){
            return new ResponseEntity<List<?>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<List<?>>(items, HttpStatus.OK);
    }
	@RequestMapping(value="/demo")
    public ResponseEntity<String> demo() {
		
        return new ResponseEntity<String>("Working", HttpStatus.OK);
    }
	@RequestMapping(value="/printers/{id}")
    public ResponseEntity<Object> findSpecificPrinter(@PathVariable("id") long id) {
		System.out.println("*************************************findSpecificPrinter");
    	Object item = 	itemService.findItemById(id, "printers");
        if(item == null){
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<Object>(item, HttpStatus.OK);
    }

}
