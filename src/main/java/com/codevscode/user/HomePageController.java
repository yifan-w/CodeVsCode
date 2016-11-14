package com.codevscode.user;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@SessionAttributes("sessionUser")
@Controller
public class HomePageController{


	@RequestMapping(value = "/")
	public String homePage(Model model, HttpSession session) {
		model.addAttribute("user", new User());
		model.addAttribute("error", 0);
		if (session.getAttribute("sessionUser") != null)
			return "redirect:/user";
		return "index";
	}
	
	@RequestMapping(value = "/about")
	public String about (Model model, HttpSession session) {
		boolean signed = true;
		User user = (User) (session.getAttribute("sessionUser"));
		if (user == null) {
			signed = false;
		}
		model.addAttribute("signed", signed);
		return "about";
	}

}
