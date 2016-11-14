package com.codevscode.user;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LogOutController {
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public String logOut(Model model, HttpSession session, RedirectAttributes redirectAttributes) {

		if (session.getAttribute("sessionUser") != null) {
			session.invalidate();
			SocialController.getConectionRepository().removeConnections("facebook");
			SocialController.getConectionRepository().removeConnections("twitter");

			redirectAttributes.addFlashAttribute("successMessage", "Log Out Successful");
		}
		return "redirect:/";
	}
}
