package com.codevscode;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.codevscode.challenge.Challenge;
import com.codevscode.common.Database;
import com.codevscode.problem.Problem;
import com.codevscode.problem.Result;
import com.codevscode.problem.Solution;
import com.codevscode.user.User;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebCodeProblemsApplication.class)
@WebAppConfiguration
public class DatabaseTests {
	
	@Autowired
	private Database database;
	
	
	@Test
	public void testFindProblemById() {
		Problem problem = database.findProblemById(35L);
		assertEquals("Test1", problem.getTitle());
		
		problem = database.findProblemById(36L);
		assertEquals("Test2", problem.getTitle());
		
		problem = database.findProblemById(37L);
		assertEquals("Print 5", problem.getTitle());
	}
	
	@Test
	public void testFindProblemBySphereEngineCode() {
		Problem problem = database.findProblemBySphereEngineCode("CVC35");
		assertEquals("Test1", problem.getTitle());
		
		problem = database.findProblemBySphereEngineCode("CVC36");
		assertEquals("Test2", problem.getTitle());
		
		problem = database.findProblemBySphereEngineCode("CVC37");
		assertEquals("Print 5", problem.getTitle());
	}
	
	@Test
	public void testFindUserById() {
		User user = database.findUserById(48L);
		assertEquals("Rohan Ramakrishnan", user.getName());
		
		user = database.findUserById(3L);
		assertEquals("aylmao", user.getName());
		
		user = database.findUserById(8L);
		assertEquals("test", user.getName());
	}
	
	@Test
	public void testFindUserByName() {
		User user = database.findUserByName("Rohan Ramakrishnan");
		assertEquals(48, user.getId().intValue());
		
		user = database.findUserByName("aylmao");
		assertEquals(3, user.getId().intValue());
		
		user = database.findUserByName("test");
		assertEquals(8, user.getId().intValue());
	}
	
	@Test
	public void testFindUserByFid() {
		User user = database.findUserByFid("1043935402345946");
		assertEquals("Jun Park", user.getName());
		
		user = database.findUserByFid("10206068278132668");
		assertEquals("Frank Yi", user.getName());
		
		user = database.findUserByFid("1146491765411492");
		assertEquals("Rohan Ramakrishnan", user.getName());
	}
	
	@Test
	public void testFindSolutionById() {
		Solution solution = database.findSolutionById(44L);
		assertEquals(13, solution.getProblemId().intValue());
		
		solution = database.findSolutionById(45L);
		assertEquals(14, solution.getProblemId().intValue());
		
		solution = database.findSolutionById(61L);
		assertEquals(37, solution.getProblemId().intValue());
	}
	
	@Test
	public void testFindSolutionByUserAndProblem() {
		Solution solution = database.findSolutionByUserAndProblem(13L, 18L);
		assertEquals(47, solution.getId().intValue());
		
		solution = database.findSolutionByUserAndProblem(14L, 17L);
		assertEquals(48, solution.getId().intValue());
		
		solution = database.findSolutionByUserAndProblem(37L, 15L);
		assertEquals(61, solution.getId().intValue());
	}
	
	@Test
	public void testFindSolutionByUserAndChallenge() {
		Solution solution = database.findSolutionByUserAndChallenge(154L, 8L);
		assertEquals(60, solution.getId().intValue());
		
		solution = database.findSolutionByUserAndChallenge(123L, 8L);
		assertEquals(58, solution.getId().intValue());
		
		solution = database.findSolutionByUserAndChallenge(123L, 47L);
		assertEquals(56, solution.getId().intValue());
	}
	
	@Test
	public void testFindChallengeById() {
		Challenge challenge = database.findChallengeById(154L);
		assertEquals(1, challenge.getPermission());
		
		challenge = database.findChallengeById(155L);
		assertEquals(2, challenge.getPermission());
	}
	
	@Test
	public void testFindChallengesByUid() {
		List<Challenge> challenges = database.findChallengesByUid(8L);
		assertEquals(2, challenges.size());
	}
	
	@Test
	public void testFindUsersInChallenge() {
		List<Long> list = database.findUsersInChallenge(154L);
		assertEquals(1, list.size());
	}
	
	@Test
	public void testFindResultById() {
		Result result = database.findResultById(1L);
		assertEquals(2, result.getScore());
		
		result = database.findResultById(10L);
		assertEquals(2, result.getScore());
		
		result = database.findResultById(11L);
		assertEquals(2, result.getScore());
	}
}
