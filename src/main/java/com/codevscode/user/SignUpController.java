package com.codevscode.user;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.codevscode.common.Database;

@Controller
@SessionAttributes("sessionUser")
public class SignUpController {
	
	static Logger log = LoggerFactory.getLogger(SignUpController.class);

	@Autowired
	private PasswordEncoderGenerator passwordEncoder;

	@Autowired
	private Database database;

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signUpForm(Model model, HttpSession session) {
		if (session.getAttribute("sessionUser") != null) {
			return "redirect:/user";
		}
		model.addAttribute("user", new User());
		return "signUp";
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String signUpSubmit(@ModelAttribute @Valid User user, BindingResult bindingResult, Model model) {

		if (bindingResult.hasErrors()) {
			return "signUp";
		}

		String rawPassword = user.getPasswordHash();
		user.setPasswordHash(passwordEncoder.getHashPassword(rawPassword));

		try {
			database.saveUser(user);
			log.info("created user: " + user.getName() + "; email: " + user.getEmail());
		} catch (DataAccessException e) {
			log.error(e.getLocalizedMessage());
			// username already exists
			return "redirect:/signup";
		}

		model.addAttribute("sessionUser", database.findUserByName(user.getName()));
		
		return "redirect:/user";
	}

	/*
	 * @RequestMapping(value = "/login", method=RequestMethod.POST) public
	 * String login(@ModelAttribute User user, Model model) { User savedUser =
	 * null; try { savedUser = database.findUserByName(user.getName()); } catch
	 * (EmptyResultDataAccessException e) { return "redirect:/"; } if
	 * (!passwordEncoder.authenticatePassword(user.getPasswordHash(),
	 * savedUser.getPasswordHash())) { return "redirect:/"; }
	 * 
	 * model.addAttribute("sessionUser", savedUser);
	 * 
	 * return "redirect:/problems"; }
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@ModelAttribute User user, Model model, final RedirectAttributes redirectAttributes) {
		User savedUser;
		try {
			savedUser = database.findUserByName(user.getName());
		} catch (EmptyResultDataAccessException e) {
			savedUser = null;
		}
		if (savedUser == null
				|| !passwordEncoder.authenticatePassword(user.getPasswordHash(), savedUser.getPasswordHash())) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"Username/Password does not match or could not be found");
			return "redirect:/";
		}

		model.addAttribute("sessionUser", savedUser);

		return "redirect:/user";
		//return "redirect:/problems";
	}
}