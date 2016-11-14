package com.codevscode.problem;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codevscode.common.Database;

@Component
public class ProblemCreationHandler {

	Logger log = LoggerFactory.getLogger(ProblemCreationHandler.class);

	@Autowired
	private Database database;
	
	@Autowired
	private SphereEngineApiWrapper apiWrapper;

	public void saveProblemAndTestcases(Problem problem, TestcasesWrapper testcases) {
		problem.setDescription(problem.getDescription().trim());
		problem.setTitle(problem.getTitle().trim());
		saveProblem(problem);
		apiWrapper.createProblem(problem.getSphereEngineCode(), problem.getTitle(), problem.getDescription());
		saveTestcases(problem, testcases);
		
		// log.info("Saved: ");
		// log.info(problem.getTitle() + "\n" + problem.getDescription());
		// for (Testcase testcase : testcases.getList()) {
		// log.info(testcase.getInput() + "\n" + testcase.getOutput() + "\n" +
		// testcase.getTimelimit());
		// }
	}
	
	public void saveProblem(Problem problem) {
		//problem.getCreatedBy(); //TODO: needs session to be done first
		
		problem.setCreatedDate(new Date());
		//problem.setCreatedBy((long) 1); //PLACEHOLDER ONLY
		problem.setSphereEngineCode("");
		
		database.saveProblem(problem);
	}
	
	public void saveTestcases(Problem problem, TestcasesWrapper testcases) {
		List<Testcase> list = testcases.getList();
		
		for (Testcase testcase : list) {
			apiWrapper.createProblemTestcase(problem.getSphereEngineCode(), testcase.getInput(), testcase.getOutput(), testcase.getTimelimit());
		}
	}
	
//	private String generateSphereEngineCode() { //TODO: lolo so hacky
//		Random random = new Random();
//		String code;
//		Problem conflict = null;
//		do {
//			code = String.valueOf(random.nextInt(10000000));
//			try {
//				conflict = database.findProblemBySphereEngineCode(code);
//			} catch (EmptyResultDataAccessException e) {
//				//is ok lol so bad code
//			}
//		} while (conflict != null);
//		
//		return code;
//	}

	public boolean validateInputs(Problem problem, TestcasesWrapper testcases) {
		if (problem.getTitle().length() == 0 || problem.getDescription().length() == 0) {
			return false;
		}

		if (testcases.getList().size() <= 0) {
			return false;
		}

		return true;
	}

}
