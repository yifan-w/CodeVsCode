
package com.codevscode.user;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.codevscode.common.Database;


@Controller

@SessionAttributes("sessionUser")
public class SocialController {

    private Facebook facebook;
	private static ConnectionRepository connectionRepository;
	private Twitter twitter;
	static Logger log = LoggerFactory.getLogger(SocialController.class);

	@Autowired
	private Database database;
	
    @Inject
    public SocialController(Facebook facebook, ConnectionRepository connectionRepository, Twitter twitter) {
        this.facebook = facebook;
		this.connectionRepository = connectionRepository;
		this.twitter = twitter;
    }

    @RequestMapping(value = "/facebook", method=RequestMethod.GET)
    public String FacebookSignIn(Model model) {
        if (connectionRepository.findPrimaryConnection(Facebook.class) == null) {
            return "redirect:/connect/facebook";
        } 
        
        org.springframework.social.facebook.api.User fbuser = facebook.userOperations().getUserProfile();
		User savedUser;
		try {
			savedUser = database.findUserByFid(fbuser.getId());
			
		} catch (EmptyResultDataAccessException e) {
			savedUser = null;
		}
		if(savedUser == null) { 
			User user = new User();
			user.setEmail(fbuser.getEmail());
			user.setFid(fbuser.getId());
			user.setId(null);
			user.setName(fbuser.getName());
			user.setPasswordHash(null);
			database.saveUser(user);
			user = database.findUserByFid(fbuser.getId());
			model.addAttribute("sessionUser", user);
		} else { 
			model.addAttribute("sessionUser", savedUser);
		}
		log.info("email : " + fbuser.getEmail());
		//connectionRepository.removeConnections("facebook");
        return "redirect:/user";
    }
    

    @RequestMapping(value = "/twitter", method=RequestMethod.GET)
    public String TwitterSignIn(Model model) {
        if (connectionRepository.findPrimaryConnection(Twitter.class) == null) {
        	log.info("itgose");
            return "redirect:/connect/twitter";
        } 
        
        TwitterProfile twitterProfile = twitter.userOperations().getUserProfile();
		User savedUser;
		try {
			savedUser = database.findUserByFid(String.valueOf(twitterProfile.getId()));
			
		} catch (EmptyResultDataAccessException e) {
			savedUser = null;
		}
		if(savedUser == null) { 
			User user = new User();

			user.setEmail(twitterProfile.getScreenName()+"@twitter.com");
			user.setFid(String.valueOf(twitterProfile.getId()));
			user.setId(null);
			user.setName(twitterProfile.getName());
			user.setPasswordHash(null);
			database.saveUser(user);
			user = database.findUserByFid(String.valueOf(twitterProfile.getId()));
			model.addAttribute("sessionUser", user);
			log.info("email : " + user.getEmail());
		} else { 
			model.addAttribute("sessionUser", savedUser);
			log.info("email : " + savedUser.getEmail());
		}
		//connectionRepository.removeConnections("twitter");
        return "redirect:/user";
    }
    
    public static ConnectionRepository getConectionRepository(){
    	return connectionRepository;
    }
    @RequestMapping(value = "/problems", params = { "share_facebook" })
    public String shareFacebook() {
    	facebook.feedOperations().updateStatus("Testing");
    	return "redirect:/problems";
    }
}