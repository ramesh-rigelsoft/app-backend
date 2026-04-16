package com.rigel.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {
	
	
	
	 @GetMapping(value = "/{path:[^\\.]*}")
	    public String redirect() {
	        return "forward:/index.html";
	    }
}
