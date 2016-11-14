package com.codevscode.user;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.codevscode.challenge.Challenge;
import com.codevscode.challenge.ChallengeDisplay;
import com.codevscode.challenge.ChallengeDisplayFactory;
import com.codevscode.common.Database;
import com.codevscode.common.ResourceNotFoundException;

@Controller
@SessionAttributes("sessionUser")
public class ProfileController {
	private final Database database;
	private final ChallengeDisplayFactory displayFactory;
	private Long userId;
	private Logger log = LoggerFactory.getLogger(ProfileController.class);
	@Autowired
	public ProfileController(Database database, ChallengeDisplayFactory displayFactory) {
		this.database = database;
		this.displayFactory = displayFactory;
	}

	/**
	 * Controller method that redirects user to their profile if they enter
	 * "/user"
	 * 
	 * @param model
	 * @param httpSession
	 * @return
	 */
	@RequestMapping("/user")
	public String sessionUserProfile(Model model, HttpSession httpSession) {
		if (httpSession.getAttribute("sessionUser") == null) {
			return "redirect:/";
		}
		return "redirect:/user/" + ((User) httpSession.getAttribute("sessionUser")).getId();
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/upload")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, 
								   HttpSession session,
								   RedirectAttributes redirectAttributes) {
		User user = (User) session.getAttribute("sessionUser");
		if (!file.isEmpty()) {
			try {
				if (user == null)
					return "redirect:/";
				user.setProfilePic(file.getBytes());
				database.saveUser(user);
			}
			catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		else {
			redirectAttributes.addFlashAttribute("message",
					"The file was empty");
		}

		return "redirect:/user/"+ user.getId();
	}
	
	@RequestMapping(value = "/user/{userId}/image", method = RequestMethod.GET)
	  public void showImage(HttpSession session,
			  		@PathVariable Long userId,
			  		HttpServletResponse response,
			  		HttpServletRequest request) 
	          throws ServletException, IOException {
		User user = database.findUserById(userId);

		if (!user.hasPic())
			user = database.findUserByName("image");
	    response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
	    response.getOutputStream().write(user.getProfilePic());
	    response.getOutputStream().close();
	}
	
	@RequestMapping("/user/{userId}")
	public String userProfile(Model model, @PathVariable Long userId, HttpSession session) {
		if (session.getAttribute("sessionUser") == null) {
			return "redirect:/";
		}
		User accessedUser;
		this.userId=userId;
		try {
			accessedUser = database.findUserById(userId);
		} catch (DataAccessException e) {
			// throw 404
			throw new ResourceNotFoundException("User");
		}
		List<Challenge> challenges = database.findChallengesByUid(accessedUser.getId());

		List<ChallengeDisplay> challengeDisplays = new ArrayList<>();
		for (Challenge challenge : challenges) {
			// TODO: check whether to add based on challenge permission
			if(accessedUser.getId() == ((User) session.getAttribute("sessionUser")).getId()) {	
				try {
					challengeDisplays.add(displayFactory.getChallengeDisplay(challenge));
					//System.out.println("CID: " + challenge.getId());
				} catch (DataAccessException e) {
					// :(
					log.error(e.getMessage());
					log.error("failed to add challenge display with challenge id=" + challenge.getId());
				}
			}
			//IF accessed User and sessionUser are friends -> show public and private challenges
			else if(accessedUser.checkIfFriends(((User) session.getAttribute("sessionUser")).getId()) && ((User) session.getAttribute("sessionUser")).checkIfFriends(accessedUser.getId())) {
				if(challenge.getPermission() != challenge.UNLISTED) {
					try {
						challengeDisplays.add(displayFactory.getChallengeDisplay(challenge));
						//System.out.println("CID: " + challenge.getId());
					} catch (DataAccessException e) {
						// :(
						log.error(e.getMessage());
						log.error("failed to add challenge display with challenge id=" + challenge.getId());
					}
				}
			}
			//IF accessedUser and sessionUser are not friends, only show public challenges
			else {
				if(challenge.getPermission() == challenge.PUBLIC) {
					try {
						challengeDisplays.add(displayFactory.getChallengeDisplay(challenge));
						//System.out.println("CID: " + challenge.getId());
					} catch (DataAccessException e) {
						// :(
						log.error(e.getMessage());
						log.error("failed to add challenge display with challenge id=" + challenge.getId());
					}
				}
			}
		}
		
		List <Long> friendsIds = accessedUser.getFriends();
		//List <String> friendsNames = new ArrayList<>();
		List <User> friends = new ArrayList<>();
		for(Long friendId : friendsIds) {
			User friend;
			try {
				friend = database.findUserById(friendId);
			} catch (DataAccessException e) {
				throw new ResourceNotFoundException("User");
			}
			friends.add(friend);
			//friendsNames.add(friend.getName());
		}
		
		User sessionUser = (User) session.getAttribute("sessionUser");
		
		List <Long> sFriend= sessionUser.getFriends();
		
		Float userSubRate;
		if(accessedUser.getNum_Total() != 0){
			userSubRate =  (100 * (float)accessedUser.getNum_Success() / (float)accessedUser.getNum_Total());			
		}
		else{
			userSubRate = (float)0;
		}
		
		System.out.println("userSubRate: " + userSubRate);
		
		model.addAttribute("submissionRate", userSubRate);
		model.addAttribute("sfriends", sFriend);
		model.addAttribute("suser", sessionUser);
		model.addAttribute("friends", friends);
		model.addAttribute("auser", accessedUser);
		model.addAttribute("challenges", challengeDisplays);
		
		/* User profile picture testing */
//		File file = new File("/Users/wangyifan/Desktop/GCC profile.png");
//		byte[] pic = null;
//		try {
//			pic = Files.readAllBytes(file.toPath());
//		} catch (IOException e) {
//			System.out.println(e.getMessage());
//		}
//		accessedUser.setProfilePic(pic);
//		database.saveUser(accessedUser);
		
		
		return "profile";
	}
	@RequestMapping(value="/user/{userId}", params = {"search"})
	public String searchUser( Model model, HttpServletRequest httpServletRequest){
		String username = httpServletRequest.getParameter("searchUser");
		User user = database.findUserByName(username);
		String redirect = "redirect:/user/"+user.getId();
		return redirect;
	}
	
	@RequestMapping(value="/user/{added}", params = {"addfriend"})
	public String addFriend(Model model, HttpSession session, HttpServletRequest req, @PathVariable String added) {
		User accessedUser;
		User sessionUser = (User)session.getAttribute("sessionUser");
		try {
		accessedUser = database.findUserById(userId);
		} catch (DataAccessException e) {
			// throw 404
			throw new ResourceNotFoundException("User");
		}
		sessionUser.addFriend(accessedUser.getId());
		database.saveUser(sessionUser);
		return "redirect:/user/"+accessedUser.getId();
	}
	@RequestMapping(value="/user/{deleted}", params = {"delfriend"})
	public String delFriend(Model model, HttpSession session,HttpServletRequest req, @PathVariable String deleted) {
		User accessedUser;
		User sessionUser = (User)session.getAttribute("sessionUser");
		try {
		accessedUser = database.findUserById(userId);
		} catch (DataAccessException e) {
			// throw 404
			throw new ResourceNotFoundException("User");
		}
		sessionUser.delFriend(accessedUser.getId());
		database.saveUser(sessionUser);
		return "redirect:/user/"+accessedUser.getId();
	}

}
