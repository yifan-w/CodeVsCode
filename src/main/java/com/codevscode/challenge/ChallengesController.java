package com.codevscode.challenge;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.codevscode.problem.Problem;
import com.codevscode.challenge.Challenge;
import com.codevscode.challenge.ChallengeDisplay;
import com.codevscode.challenge.ChallengeDisplayFactory;
import com.codevscode.common.Comment;
import com.codevscode.common.Database;
import com.codevscode.common.ResourceNotFoundException;
import com.codevscode.problem.ProblemsController;
import com.codevscode.problem.Result;
import com.codevscode.problem.ResultLeaderboardEntry;
import com.codevscode.problem.Solution;
import com.codevscode.problem.Testcase;
import com.codevscode.user.User;

@Controller
@RequestMapping(value = "/challenge")
@SessionAttributes("sessionUser")
public class ChallengesController {
	private final Database database;
	private final ChallengeDisplayFactory challengeDisplayFactory;
	private final ProblemsController problemsController;
	private final HttpServletRequest request;
	

	private Logger log = LoggerFactory.getLogger(ChallengesController.class);


	/**
	 * Controller method that redirects user to their profile if they enter
	 * "/challenge"
	 * 
	 * @param model
	 * @param httpSession
	 * @return
	 */
	@RequestMapping("/")
	public String sessionChallenge(Model model, HttpSession httpSession) {
		if (httpSession.getAttribute("sessionUser") == null) {
			return "redirect:/";
		}
		return "redirect:/user/" + ((User) httpSession.getAttribute("sessionUser")).getId();
		//return "challenge";
	}

	@RequestMapping("/{cId}/results")
	public String challenge(Model model, @PathVariable Long cId, HttpSession session) {
		// Check if current session has a user
		User sessionUser = (User) session.getAttribute("sessionUser");
		if (session.getAttribute("sessionUser") == null) {
			return "redirect:/";
		}
		
		// Check if accessed challenge is valid
		Challenge accessedChallenge;
		try {
			accessedChallenge = database.findChallengeById(cId);
		} catch (DataAccessException e) {
			// throw 404
			throw new ResourceNotFoundException("Challenge");
		}
		model.addAttribute("foruId", accessedChallenge);
		//Add to model for checking if user is in challenge
		Boolean isInChallenge = accessedChallenge.userInChallenge(sessionUser);
		model.addAttribute("challengePermission", isInChallenge);
		
		//Add to model to see if redirection is coming from challenge or problem
		Boolean isChallenge = true;
		model.addAttribute("isChallenge", isChallenge);
		
		// Check if current session user has access to challenge
		User cCreator = database.findUserById(accessedChallenge.getCrtId());
		if(!accessedChallenge.userHasAccess(sessionUser, cCreator) && !accessedChallenge.getUsers().contains(sessionUser.getId())) {
			System.out.println("challenge ID:" + accessedChallenge.getId() + " session user:" + sessionUser.getId() + " creator: " + cCreator.getId());
			throw new ResourceNotFoundException("Access for Challenge"); 
		}
		
		User challengeCreator = database.findUserById(accessedChallenge.getCrtId());
		if(!accessedChallenge.userHasAccess(sessionUser, challengeCreator)) {
			throw new ResourceNotFoundException("Access to Challenge"); 
		}
		
		//Get the challenge display for the specific challenge
		ChallengeDisplay challengeDisplay = null;
		try {
			challengeDisplay = challengeDisplayFactory.getChallengeDisplay(accessedChallenge);
		} catch (DataAccessException e){
			log.error("failed to display challenge id");
			throw new ResourceNotFoundException("Challenge");
		}
		
		log.info(challengeDisplay.getProblemTitle());
		model.addAttribute("challenge", challengeDisplay);
		
		String uri = request.getScheme() + "://" +
				request.getServerName() + 
				":" + request.getServerPort() +
				request.getRequestURI();
		
		log.info("This challenge URI: " + uri);
		model.addAttribute("uri", uri);
		List<Comment> comments = database.findCommentByCid(cId);
		List<Long> uids = accessedChallenge.getUsers();
		List<Solution> solutions = new ArrayList<>();
		List<ResultLeaderboardEntry> entries = new ArrayList<>();
		
		for (Long id : uids) {
			try {
				Solution s = database.findSolutionByUserAndChallenge(accessedChallenge.getId(), id);
				solutions.add(s);
				log.info(s.toString());
			} catch (DataAccessException e) {

				//log.info(e.getMessage());
				log.info("No solution for user: " + id);
				//solutions.add(new Solution(0L, "Not Completed", 0L, 0L, 0));
			}
		}
		
		for (Solution sol : solutions) {
			try {
				Result temp = database.findResultById((long)sol.getResultId());
				//results.add(new Result(temp.getElapsedTime(), temp.getExecutionTime(), temp.getMemory(), temp.getScore(), sol.getUser(), 
							//database.findUserById(sol.getUser()).getName()));
				ResultLeaderboardEntry entry = new ResultLeaderboardEntry(temp, database.findUserById(sol.getUser()), sol);
				entries.add(entry);
			} catch (DataAccessException e) {

				//log.error(e.getMessage());
				log.info("No result for: " + sol.getResultId());	
				ResultLeaderboardEntry entry = new ResultLeaderboardEntry(null, database.findUserById(sol.getUser()), sol);
				entries.add(entry);

			}
		}
		

		List<String> commentStrings = new ArrayList<>();

		log.info("number of comments : " + comments.size());
		for (Comment comment : comments) {
			StringBuilder sb = new StringBuilder();

			sb.append(database.findUserById(comment.getUid()).getName());
			sb.append(" ( " + Date.from(comment.getTime().toInstant()).toString() + " ) : ");
			sb.append(comment.getContent());
			commentStrings.add(sb.toString());

		}
		 		
		User user = (User) (session.getAttribute("sessionUser"));
		model.addAttribute("user", user);		
		model.addAttribute("comments", commentStrings);
		Collections.sort(entries);
		model.addAttribute("entries", entries);
		model.addAttribute("solutions", solutions);

		return "challenge";
	}
	
	@RequestMapping("/{cId}/results/{pId}/solution") 
	public String showSolution(Model model, @PathVariable Long cId, @PathVariable Long pId, HttpSession session) {
		Solution solution;
		try {
			solution = database.findSolutionByUserAndChallenge(cId, pId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			log.error("No solution for user: " + pId);
			solution = new Solution(0L, "Not Completed", 0L, 0L, 0);
		}
		model.addAttribute("solution", solution);
		return "solution";
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String createChallenge(@ModelAttribute(value = "problem") Problem problem,
			@ModelAttribute(value = "challenge") Challenge challengePer, Model model, HttpSession session) {
		if(session.getAttribute("sessionUser") == null) {
			return "redirect:/";
		}

		Long pid = problem.getId();
		Long crt_id = ((User)(session.getAttribute("sessionUser"))).getId();
		List<Long> uids = new ArrayList<Long>();
		uids.add(crt_id);
		Challenge challenge = new Challenge();
		challenge.setPid(pid);
		challenge.setCrtId(crt_id);
		challenge.setPermission(challengePer.getPermission());
		challenge.setUsers(uids);

		database.saveChallenge(challenge);
		
		return "redirect:/challenge/" + challenge.getId() + "/results";
	}
	

	

	@Autowired
	public ChallengesController(ProblemsController problemsController, Database database,
			ChallengeDisplayFactory challengeDisplayFactory, HttpServletRequest request) {
		this.request = request;
		this.problemsController = problemsController;
		this.database = database;
		this.challengeDisplayFactory = challengeDisplayFactory;
	}
	
	@RequestMapping("/{challengeId}/problem")
	public String challengeProblem(Model model, @PathVariable long challengeId, HttpSession session) {
		User sessionUser = ((User)session.getAttribute("sessionUser"));
		if (session.getAttribute("sessionUser") == null) {
			return "redirect:/";
		}
		
		Challenge challenge;
		
		

		try {
			challenge = database.findChallengeById(challengeId);
		} catch (DataAccessException e) {
			throw new ResourceNotFoundException("Challenge");
		}
		
		User challengeCreator = database.findUserById(challenge.getCrtId());
		if(!challenge.userHasAccess(sessionUser, challengeCreator)) {
			throw new ResourceNotFoundException("Access to Challenge"); 
		}
		
		if(!challenge.userInChallenge(sessionUser)){
			challenge.addUser(sessionUser.getId());
			database.saveChallenge(challenge);
		}
		problemsController.problemHandler(model, challenge.getPid(), session, challenge);
		model.addAttribute("challengeDisplay", challengeDisplayFactory.getChallengeDisplay(challenge));
		model.addAttribute(challenge);

		return "problem_ide";
	}

	@RequestMapping(value = "/{challengeId}/problem", method = RequestMethod.POST)
	public String postChallengeSolution(Model model, @PathVariable long challengeId, @ModelAttribute Solution solution,
			HttpSession session, final RedirectAttributes redirectAttributes) {
		
		if (session.getAttribute("sessionUser") == null) {
			return "redirect:/";
		}
		
		Challenge challenge;
		User user = (User) session.getAttribute("sessionUser");
		if (user == null) {
			return "redirect:/";
		} 
		
		try {
			challenge = database.findChallengeById(challengeId);
		} catch (DataAccessException e) {
			throw new ResourceNotFoundException("Challenge");
		}
		
		solution.setCid(challengeId);
		
		solution.setProblemId(challenge.getPid());
		
		problemsController.postSolution(solution, model, session, redirectAttributes);
		
		Map<String, ?> map = redirectAttributes.getFlashAttributes();
		
		Result result = (Result) map.get("result");
		
		return "redirect:/challenge/{challengeId}/problem";
		
	}
	
	@RequestMapping(value = "/{challengeId}/problem", params = { "save" }, method = RequestMethod.POST)
	public String saveChallengeSolution(@ModelAttribute Solution solution, @PathVariable long challengeId, Model model, HttpSession session) {
	
		solution.setCid(challengeId);
		
		if (session.getAttribute("sessionUser") == null) {
			return "redirect:/";
		}
		
		Challenge challenge;
		
		try {
			challenge = database.findChallengeById(challengeId);
		} catch (DataAccessException e) {
			throw new ResourceNotFoundException("Challenge");
		}
		
		solution.setProblemId(challenge.getPid());
		
		return problemsController.saveSolution(solution, model, session);
		
	}
	
	@RequestMapping("/challenge/comment/create")
	public String createCommentChallenge(Model model, HttpSession httpSession) {
		if (httpSession.getAttribute("sessionUser") == null) {
			return "redirect:/";
		}
		return "challenge";
	}
	
	@RequestMapping(value = "/{challengeId}/results", params = { "saveComment" } , method = RequestMethod.POST)
	 public String saveSingleComment(@RequestParam(value="content", required=true) String content, @PathVariable long challengeId, Model model, HttpSession session){
	 		Challenge challenge = database.findChallengeById(challengeId);
	 		User sessionUser = (User) session.getAttribute("sessionUser");
	 		if (sessionUser == null) {
	 			return "redirect:/";
	 		}
	 			 		
	 		log.info(content);
	 		Comment comment = new Comment();
	 		comment.setContent(content);
	 		comment.setUid(sessionUser.getId());
	 		
	 		comment.setTime(new Timestamp(new Date().getTime()));
	 		comment.setCid(challenge.getId());
	 		
	 		database.saveComment(comment);
	 		
	 		return challenge(model,challengeId,session);
	 }
	
	@RequestMapping(value = "/{challengeId}/leave", method = RequestMethod.GET)
	public String leaveChallenge (Model model, HttpSession session , @PathVariable long challengeId) {
		User user = (User) (session.getAttribute("sessionUser"));
		database.leaveChallenge(database.findChallengeById(challengeId), user.getId());
		return "redirect:/user/"+user.getId();
	}
	
	@RequestMapping(value = "/{challengeId}/delete", method = RequestMethod.GET)
	public String deleteChallenge(Model model, HttpSession session, @PathVariable long challengeId) {
		User user = (User) (session.getAttribute("sessionUser"));
		database.deleteChallenge(database.findChallengeById(challengeId));
		return "redirect:/user/"+user.getId();
	}
}
