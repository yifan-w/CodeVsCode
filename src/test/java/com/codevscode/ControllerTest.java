  package com.codevscode;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.codevscode.problem.ProblemsController;
import com.codevscode.user.User;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebCodeProblemsApplication.class)
@WebAppConfiguration
public class ControllerTest {
	
	private MockMvc mockMvc;
	
	@Autowired
	private ProblemsController controller;
	
	@Autowired
	private MockHttpSession session;
	
	@Autowired
	private WebApplicationContext context;
	
	private final long problemId = 37;
	
	@Before
	public void setup() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
		session.putValue("sessionUser", new User(8L, "test", "test@test.com"));
	}
	
	@Test
	public void testProblemsList() throws Exception {
		mockMvc.perform(get("/problems"))
			.andExpect(status().isOk())
			.andExpect(view().name("problems"))
			.andExpect(model().attribute("problems", not(hasSize(0))));
	}
	
	@Test
	public void testProblem() throws Exception {
		
		
		mockMvc.perform(get("/problems/" + problemId).session(session))
			.andExpect(status().isOk())
			.andExpect(view().name("problem_ide"))
			.andExpect(model().attribute("solution", allOf(
					hasProperty("problemId", is(37L)),
					hasProperty("source"),
					hasProperty("user", is(8L)))))
			.andExpect(model().attribute("problem", allOf(
					hasProperty("id", is(problemId)),
					hasProperty("title", is("Print 5")),
					hasProperty("description", is("print 5")))));
	}
	
	@Test
	public void testNoSessionUser() throws Exception {
		mockMvc.perform(get("/problems/" + problemId))
			.andExpect(status().is3xxRedirection());
		mockMvc.perform(get("/problems/create"))
			.andExpect(status().is3xxRedirection());
	}
	
	@Test
	public void testCreate() throws Exception {
		mockMvc.perform(get("/problems/create").session(session))
			.andExpect(status().isOk())
			.andExpect(view().name("problem_creation"));
	}
	
	
}
