package com.codevscode.problem;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.codevscode.challenge.Challenge;
import com.codevscode.challenge.ChallengeDisplay;
import com.codevscode.common.Comment;
import com.codevscode.common.Database;
import com.codevscode.common.ResourceNotFoundException;
import com.codevscode.user.User;

@Controller
@RequestMapping(value = "/problems")
@SessionAttributes("sessionUser")
public class ProblemsController {

	private final Database database;
	private final SphereEngineApiWrapper api;
	private final ProblemCreationHandler problemCreationHandler;

	private static final Logger log = LoggerFactory.getLogger(ProblemsController.class);

	@Autowired
	public ProblemsController(Database database, SphereEngineApiWrapper api,
			ProblemCreationHandler problemCreationHandler) {
		this.database = database;
		this.api = api;
		this.problemCreationHandler = problemCreationHandler;
	}

	@RequestMapping
	public String problemsList(Model model, HttpSession session) {
		boolean signed = true;
		List<Problem> problems = database.listProblems();
		User user = (User) (session.getAttribute("sessionUser"));
		if (user == null) {
			signed = false;
		}
		model.addAttribute("problems", problems);
		model.addAttribute("signed", signed);
		
		return "problems";
	}

	public String problemHandler(Model model, @PathVariable Long problemId, HttpSession session, Challenge challenge) {
		model.addAttribute("problem", database.findProblemById(problemId));
		User user = (User) (session.getAttribute("sessionUser"));
		if (user == null) {
			return "redirect:/";
		}
		Solution solution = null;
		
		if (challenge == null) {
			try {
				solution = database.findSolutionByUserAndProblemNoChallenge(problemId, user.getId());
				log.info(solution.toString());
			} catch (DataAccessException e) {
				solution = new Solution();
				solution.setProblemId(problemId);
			}
		} else {
			try {
				solution = database.findSolutionByUserAndChallenge(challenge.getId(), user.getId());
			} catch (DataAccessException e) {
				solution = new Solution();
				solution.setProblemId(problemId);
				solution.setCid(challenge.getId());
			}
		}
		
		model.addAttribute(solution);
		return "problem_ide";
	}
	
	@RequestMapping("/{problemId}/results")
	public String problemResults(Model model, @PathVariable Long problemId, HttpSession session) {
		//Add to model to see if redirection is coming from challenge or problem
		Boolean isChallenge = false;
		model.addAttribute("isChallenge", isChallenge);
		
		// Check if current session has a user
		User sessUser = (User) session.getAttribute("sessionUser");
		if (session.getAttribute("sessionUser") == null) {
			return "redirect:/";
		}
		// Check if accessed challenge is valid
		Problem problem;
		try {
			problem = database.findProblemById(problemId);
		} catch (DataAccessException e) {
			// throw 404
			throw new ResourceNotFoundException("problem");
		}
		model.addAttribute("problem", problem);
		
		//List<Long> commentsIds = problem.getComments();
		List<Long> uids = database.findUserByPid(problem.getId());
		List<Result> results = new ArrayList<>();
		List<Solution> solutions = new ArrayList<>();
		List<ResultLeaderboardEntry> entries = new ArrayList<>();
		List<Comment> comments = database.findCommentByPid(problemId);
		for (Long id : uids) {
			try {
				Solution s = database.findSolutionByUserAndProblemNoChallenge(problem.getId(), id);
				solutions.add(s);
				log.info(s.toString());
			} catch (DataAccessException e) {
				log.error(e.getMessage());
				log.error("No solution for user: " + id);
				solutions.add(new Solution(0L, "Problem Not Completed!", id, 0L, 0));
			}
		}
		for (Solution sol : solutions) {
			try {
				Result temp = database.findResultById((long)sol.getResultId());
				ResultLeaderboardEntry entry = new ResultLeaderboardEntry(temp, database.findUserById(sol.getUser()), sol);
				entries.add(entry);
			} catch (DataAccessException e) {
				log.info("No result for: " + sol.getResultId());	
				ResultLeaderboardEntry entry = new ResultLeaderboardEntry(null, database.findUserById(sol.getUser()), sol);
				entries.add(entry);
			}
		}
		List<String> commentStrings = new ArrayList<>();

		for (Comment comment : comments) {
			StringBuilder sb = new StringBuilder();

			sb.append(database.findUserById(comment.getUid()).getName());
			sb.append(" ( " + Date.from(comment.getTime().toInstant()).toString() + " ) : ");
			sb.append(comment.getContent());
			commentStrings.add(sb.toString());
		}
		
		model.addAttribute("comments", commentStrings);
		model.addAttribute("results", results);
		model.addAttribute("solutions", solutions);
		model.addAttribute("entries", entries);

		return "challenge";
	}
	
	@RequestMapping(value = "{problemId}")
	public String problem(Model model, @PathVariable Long problemId, HttpSession session) {
		String result = problemHandler(model, problemId, session, null);
		model.addAttribute("challenge", new Challenge());
		Problem problem = database.findProblemById(problemId);
		Float probSubRate;
		if(problem.getNum_Total() != 0){
			probSubRate = 100 * (float)problem.getNum_Success() / problem.getNum_Total();
		}
		else {
			probSubRate = (float)0;
		}
		model.addAttribute("submissionRate", probSubRate);
		System.out.println("probSubRate: " + probSubRate);
		return result;
	}

	@RequestMapping(value = "{problemId}", params = { "save" }, method = RequestMethod.POST)
	public String saveSolution(@ModelAttribute Solution solution, Model model, HttpSession session) {
		User user = (User) (session.getAttribute("sessionUser"));
		if (user == null) {
			return "redirect:/";
		}
		Problem problem = database.findProblemById(solution.getProblemId());
		model.addAttribute("problem", problem);
		solution.setUser(user.getId());
		log.info(solution.toString());
		database.saveSolution(solution);
		return "problem_ide";
	}

	@RequestMapping(value = "{problemId}", method = RequestMethod.POST)
	public String postSolution(@ModelAttribute Solution solution, Model model,
			HttpSession session, final RedirectAttributes redirectAttributes) {
		
		User user = (User) session.getAttribute("sessionUser");

		if (user == null) {
			return "redirect:/";
		}

		solution.setUser(user.getId());
		Problem problem = database.findProblemById(solution.getProblemId());

		if (!user.checkAndUpdateSubmissionTime()) {
			database.saveSolution(solution);

			redirectAttributes.addFlashAttribute("submitError",
					"You have resubmitted too soon, please wait and resubmit in a moment");

			return "redirect:/problems/" + solution.getProblemId();
		}

		int submissionId = api.createSubmission(problem.getSphereEngineCode(), solution.getLanguage(),
				solution.getSource());
		if (submissionId == -1) { // error is creating solution
			database.saveSolution(solution);
			redirectAttributes.addFlashAttribute("submitError", "Error creating submission");
			return "redirect:/problems/" + solution.getProblemId();
		}

		Result result;
		do {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			result = api.fetchSubmissionDetails(submissionId);
			result.setElapsedTime(solution.getElapsedTime());
		} while (result.isBlockingStatus());
		long rid = database.saveResult(result);
		solution.setResultId(rid);
		database.saveSolution(solution);
		
		if(result.getStatus().equals("accepted")){
			try{
				problem.setNum_Success(problem.getNum_Success() + 1);
				user.setNum_Success(user.getNum_Success() + 1);
			} catch (DataAccessException e) {
				problem.setNum_Success(1);
				user.setNum_Success(1);
			}
			try{
				System.out.println("numTotal: " + user.getNum_Total());
				user.setNum_Total(user.getNum_Total() + 1);
				System.out.println("numTotal: " + user.getNum_Total());
				problem.setNum_Total(problem.getNum_Total() + 1);
			
			} catch (DataAccessException e){
				user.setNum_Total(1);
				System.out.println("numTotalexep: " + user.getNum_Total());
				problem.setNum_Total(1);
			}
		}
		else {
			try{
				user.setNum_Total(user.getNum_Total() + 1);
				System.out.println("numTotal: " + user.getNum_Total());
				problem.setNum_Total(problem.getNum_Total() + 1);
			} catch (DataAccessException e){
				user.setNum_Total(1);
				System.out.println("numTotalexep: " + user.getNum_Total());
				problem.setNum_Total(1);
			}
		}
		
		System.out.println("numTotal: " + user.getNum_Total());
		System.out.println("numSucces: " + user.getNum_Success());
		database.saveUser(user);
		database.saveProblem(problem);
		
		redirectAttributes.addFlashAttribute(result);
		redirectAttributes.addFlashAttribute(problem);
		model.addAttribute("problem",problem);
		
		
		return "redirect:/problems/" + solution.getProblemId();
	}
	
	@RequestMapping("/{pId}/results/{uId}/solution") 
	public String showSolution(Model model, @PathVariable Long pId, @PathVariable Long uId, HttpSession session) {
		Solution solution;
		try {
			solution = database.findSolutionByUserAndProblemNoChallenge(pId, uId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			log.error("No solution for user: " + pId);
			solution = new Solution(0L, "Not Completed", 0L, 0L, 0);
		}
		model.addAttribute("solution", solution);
		return "solution";
	}

	@RequestMapping(value = "/create")
	public String createProblem(Model model, HttpSession session) {

		if (session.getAttribute("sessionUser") == null) {
			return "redirect:/";
		}

		if (!model.containsAttribute("testcases")) {
			TestcasesWrapper testcases = new TestcasesWrapper();
			testcases.getList().add(new Testcase());
			model.addAttribute("testcases", testcases);
		}

		if (!model.containsAttribute("problem")) {
			Problem problem = new Problem();
			problem.setCreatedDate(new Date());
			model.addAttribute(problem);
		}

		return "problem_creation";
	}

	@RequestMapping(value = "/create", params = { "addTestcase" })
	public String addTestcase(@ModelAttribute(value = "testcases") TestcasesWrapper testcases, Problem problem,
			final Model model) {

		testcases.getList().add(new Testcase());
		model.addAttribute("testcases", testcases);
		return "problem_creation";
	}

	@RequestMapping(value = "/create", params = { "removeTestcase" })
	public String removeTestcase(@ModelAttribute(value = "testcases") TestcasesWrapper testcases, Problem problem,
			final Model model, @RequestParam(value = "removeTestcase") Integer testcaseToRemove) {

		testcases.getList().remove(testcaseToRemove.intValue());

		model.addAttribute(testcases);

		return "problem_creation";
	}

	@RequestMapping(value = "/create/submit", method = RequestMethod.POST)
	public String submitProblemCreation(@ModelAttribute(value = "testcases") TestcasesWrapper testcases,
			@Valid Problem problem, BindingResult problemBindingResult, HttpSession session,
			final RedirectAttributes redirectAttributes) {

		if (session.getAttribute("sessionUser") == null) {
			return "redirect:/";
		}

		if (problemBindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.problem",
					problemBindingResult);
			redirectAttributes.addFlashAttribute(problem);
			redirectAttributes.addFlashAttribute("testcases", testcases);
			return "redirect:/problems/create";

		}

		if (!problemCreationHandler.validateInputs(problem, testcases)) {
			return "problem_creation";
		}
		User user = (User) session.getAttribute("sessionUser");
		problem.setCreatedBy(user.getId());
		problemCreationHandler.saveProblemAndTestcases(problem, testcases);

		return "redirect:/problems";
	}
	
	@RequestMapping(value = "/{problemId}/results", params = { "saveComment" } , method = RequestMethod.POST)
	 public String saveSingleComment(@RequestParam(value="content", required=true) String content, @PathVariable long problemId, Model model, HttpSession session){
	 		
	 		if (session.getAttribute("sessionUser") == null) {
	 			return "redirect:/";
	 		}
	 		log.info(content);
	 		Comment comment = new Comment();
	 		comment.setContent(content);
	 		User sessionUser = (User) session.getAttribute("sessionUser");
	 		comment.setUid(sessionUser.getId());
	 		
	 		comment.setTime(new Timestamp(new Date().getTime()));
	 		comment.setPid(problemId);
	 		database.saveComment(comment);
	 		
	 		return problemResults(model, problemId, session);
	 }
}
