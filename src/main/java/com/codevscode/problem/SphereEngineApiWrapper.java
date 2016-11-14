package com.codevscode.problem;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import com.codevscode.problem.Result.RuntimeInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;;

@Component
public class SphereEngineApiWrapper {

	private static final String accessToken = "8b9f2d384b5fb4503168e1158aa4f9fc6579b9d4";
	private static final String url = "http://problems.sphere-engine.com/api/v3/";
	public static final Logger log = LoggerFactory.getLogger(SphereEngineApiWrapper.class);
	
	/**
	 * Method for testing connection Sphere Engine API servers
	 * 
	 * @return whether or not connection was successful
	 */
	public boolean testConnection() {
		boolean connected = false;
		RestTemplate template = new RestTemplate();
		UriComponentsBuilder builder= UriComponentsBuilder.fromHttpUrl(url)
				.pathSegment("test")
				.queryParam("access_token", accessToken);
		Connection connection = null;
		
		log.info("sent test request with url : " + builder.toUriString());
		
		try {
			connection = template.getForObject(builder.toUriString(), Connection.class);
			connected = true;
		} catch( RestClientException e) {
			connection = new Connection();
			connection.setMessage(e.getMessage());
			log.error(e.toString());
		}
		
		if(connected) return true;
		return false;
	}

	/**
	 * Creates a problem using the Sphere Engine Problems API
	 * 
	 * @param code
	 *            Sphere Engine Problem code
	 * @param name
	 *            Title given to the problem that is being created
	 * @param body
	 *            Description given to the problem that is being created
	 * @return the Sphere Engine Problem code of the problem that has been
	 *         created
	 */
	public String createProblem(String code, String name, String body) {
		
		RestTemplate template = new RestTemplate();
		UriComponentsBuilder builder= UriComponentsBuilder.fromHttpUrl(url)
				.pathSegment("problems")
				.queryParam("access_token", accessToken);
		template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        template.getMessageConverters().add(new StringHttpMessageConverter());
        
        ProblemAPI createProblem = new ProblemAPI();
        ProblemAPI response = null;
        createProblem.setCode(code);
        createProblem.setName(name);
        createProblem.setBody(body);
        
        log.info("sent create a new problem request with url : " + builder.toUriString());
        
		try {
			response = template.postForObject(builder.toUriString(),
					createProblem, ProblemAPI.class);
			
		} catch (HttpClientErrorException e) {
			response = new ProblemAPI();
			response.setCode(e.getMessage());
			log.error(e.toString());
		}
		
		log.info(response.getCode());
		return response.getCode();
	}

	/**
	 * Updates a problem on the Sphere Engine Problems API
	 * 
	 * @param code
	 *            Sphere Engine Problem code
	 * @param name
	 *            New title given to the problem
	 * @param body
	 *            New description given to the problem
	 */
	public void updateProblem(String code, String name, String body) {
		RestTemplate template = new RestTemplate();
		
		UriComponentsBuilder builder= UriComponentsBuilder.fromHttpUrl(url)
				.pathSegment("problems")
				.pathSegment(code)
				.queryParam("access_token", accessToken);
		
		template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        template.getMessageConverters().add(new StringHttpMessageConverter());
        
        ProblemAPI updateProblem = new ProblemAPI();
        updateProblem.setCode(code);
        updateProblem.setName(name);
        updateProblem.setBody(body);
        
        log.info("sent update an existing problem request with url : " + builder.toUriString());
        
		try {
			template.put(builder.toUriString(),
					updateProblem);
			
		} catch (RestClientException e) {
			
			log.error(e.toString());
		}
	}

	/**
	 * Creates a testcase for the specified problem
	 * 
	 * @param code
	 *            Sphere Engine Problem code
	 * @param input
	 *            Input data of the testcase
	 * @param output
	 *            Expected output data of the testcase
	 * @param timelimit
	 *            Timelimit of the testcase in seconds
	 * @return Test case number, starting from 0
	 */
	public int createProblemTestcase(String code, String input, String output, Double timelimit) {
		RestTemplate template = new RestTemplate();
		
		UriComponentsBuilder builder= UriComponentsBuilder.fromHttpUrl(url)
				.pathSegment("problems")
				.pathSegment(code)
				.pathSegment("testcases")
				.queryParam("access_token", accessToken);
		
		template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        template.getMessageConverters().add(new StringHttpMessageConverter());
        
        Testcase createTestcase = new Testcase();
        createTestcase.setInput(input);
        createTestcase.setOutput(output);
        createTestcase.setTimelimit(timelimit);
        
        Testcase testcaseNumber = null;
        
        log.info("sent create a problem testcase request with url : " + builder.toUriString());
        
		try {
			testcaseNumber = template.postForObject(builder.toUriString(),
					createTestcase, Testcase.class);
			
		} catch (HttpClientErrorException e) {
			
			testcaseNumber = new Testcase();
			testcaseNumber.setTestcaseNumber(-1);
			log.error(e.toString());
		}
		
		return testcaseNumber.getTestcaseNumber();
	}

	/**
	 * Updates a testcase based on the specified problem code and testcase number
	 * 
	 * @param code
	 *            Sphere Engine Problem code
	 * @param number
	 *            Testcase number
	 * @param input
	 *            Input data of the testcase
	 * @param output
	 *            Expected output data of the testcase
	 * @param timelimit
	 *            Timelimit of the testcase in seconds
	 */
	public void updateProblemTestcase(String code, int number, String input, String output, Double timelimit) {
		RestTemplate template = new RestTemplate();
		
		UriComponentsBuilder builder= UriComponentsBuilder.fromHttpUrl(url)
				.pathSegment("problems")
				.pathSegment(code)
				.pathSegment("testcases")
				.pathSegment(""+number)
				.queryParam("access_token", accessToken);
        
        log.info("sent update the problem testcase request with url : " + builder.toUriString());
        
		try {
			
			template.put(builder.toUriString(),null);
			
		} catch ( RestClientException e) {

			log.error(e.toString());
		}
		
	}

	/**
	 * Retrieves a list of testcases for a problem
	 * 
	 * @param code
	 *            Sphere Engine Problem code
	 * @return a List of all testcases for specified problem
	 */
	public List<Testcase> retrieveListOfTestcases(String code) {
		// TODO: still needs to be implemented
		
		RestTemplate template = new RestTemplate();
		
		UriComponentsBuilder builder= UriComponentsBuilder.fromHttpUrl(url)
				.pathSegment("problems")
				.pathSegment(code)
				.pathSegment("testcases")
				.queryParam("access_token", accessToken);
        
        log.info("sent update the problem testcase request with url : " + builder.toUriString());
        try {
        	
        } catch ( RestClientException e) { 
        	
        }
		return null;
	}

	/**
	 * Creates a new submission for the specified problem
	 * 
	 * @param code
	 *            Sphere Engine Problem code
	 * @param compilerId
	 *            ID of compiler to be used when executing this submission
	 * @param source
	 *            Source code of the submission
	 * @return Submission ID
	 */
	public int createSubmission(String code, int compilerId, String source) {
		RestTemplate template = new RestTemplate();
		
		UriComponentsBuilder builder= UriComponentsBuilder.fromHttpUrl(url)
				.pathSegment("submissions")
				.queryParam("access_token", accessToken);
		
		template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        template.getMessageConverters().add(new StringHttpMessageConverter());
        Submission createSubmission = new Submission();
        createSubmission.setProblemCode(code);
        createSubmission.setCompilerId(compilerId);
        createSubmission.setSource(source);
        
        ID id = null;
        
        log.info("sent create a new submission request with url : " + builder.toUriString());
        
		try {
			id = template.postForObject(builder.toUriString(),
					createSubmission, ID.class);
			
		} catch (HttpClientErrorException e) {
	
			id = new ID();
			id.setID(-1);
			log.error(e.toString());
		}
		log.info("ID is " + id.getID());
		return id.getID();
	}

	/**
	 * Fetches submission results
	 * 
	 * @param submissionId
	 *            Submission ID
	 * @return Result class containing submission results
	 */
	public Result fetchSubmissionDetails(int submissionId) {
		RestTemplate template = new RestTemplate();
		
		UriComponentsBuilder builder= UriComponentsBuilder.fromHttpUrl(url)
				.pathSegment("submissions")
				.pathSegment("" + submissionId)
				.queryParam("access_token", accessToken);
        
        log.info("sent fetch submission details request with url : " + builder.toUriString());
        
        ResultResponse result = new ResultResponse() ;
        try {
        	result = template.getForObject(builder.toUriString(), ResultResponse.class);
        } catch ( RestClientException e ) { 
        	log.error( e.getMessage());
        }
        
        for (TestcaseResult tr : result.getTestcaseResults()) {
        	if (!"accepted".equals(tr.getStatusDescription())) {
        		result.getResult().setFailedTestcase(new Testcase(tr.getId().intValue()));
        		break;
        	}
        }
        
        RuntimeInfo r = result.getResult().getRuntimeInfo();
        
        
        if (!(r != null && (!r.getCmperr().isEmpty() || (!r.getStderr().isEmpty()) && !r.getStdout().isEmpty()))) {
        	if (r != null) {
        		log.info("fuck sphere engine");
        	}
        	result.getResult().setStatus("waiting...");
        }
        
		return result.getResult();
	}
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	static class Connection {
		private String message;
		public String getMessage() { return message; }
		public void setMessage(String message) { this.message= message;}
		public Connection() {}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	static class ProblemAPI {
		private String code;
		private String name;
		private String body;
		public String getBody() { return body; }
		public void setBody(String body) { this.body = body; }
		public String getCode() {return code;}
		public void setCode(String code) { this.code = code; }
		public String getName() {return name;}
		public void setName(String name) { this.name = name; }
		public ProblemAPI(){}
	}
	@JsonIgnoreProperties(ignoreUnknown = true)
	static class TestCase {
		private String input;
		private String output;
		private Double timelimit;
		public String getInput() { return input; }
		public void setInput( String input ) { this.input = input; }
		public String getOutput() { return output; }
		public void setOutput( String output) { this.output = output; } 
		public Double getTimelimit() { return timelimit; }
		public void setTimelimit( Double timelimit) { this.timelimit = timelimit; }
		public TestCase() {}
	}
	@JsonIgnoreProperties(ignoreUnknown = true)
	static class Number {
		private Integer number;
		public Integer getNumber() { return number; }
		public void setNumber( Integer number ) { this.number = number; }
		public Number(){}
	}
	@JsonIgnoreProperties(ignoreUnknown = true)
	static class Submission {
		private String problemCode;
		private Integer compilerId;
		private String source;
		public String getProblemCode() { return problemCode; }
		public void setProblemCode( String problemCode ) { this.problemCode = problemCode; }
		public Integer getCompilerId() { return compilerId; }
		public void setCompilerId( Integer compilerId ) { this.compilerId = compilerId; }
		public String getSource() { return source; }
		public void setSource(String source) { this.source = source; } 
		public Submission() {}
	}
	@JsonIgnoreProperties(ignoreUnknown = true)
	static class ID {
		private Integer id;
		public Integer getID() { return id; }
		public void setID( Integer id ) { this.id = id; }
		public ID(){}
	}	
	@JsonIgnoreProperties(ignoreUnknown = true)
	static class ResultResponse {
		private Result result;
		@JsonProperty("result_array")
		private TestcaseResult[] testcaseResults;
		public TestcaseResult[] getTestcaseResults() {
			return testcaseResults;
		}
		public void setTestcaseResults(TestcaseResult[] testcaseResults) {
			this.testcaseResults = testcaseResults;
		}
		public void setResult(Result result) {this.result = result;}
		public Result getResult() {return result;}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	static class TestcaseResult {
		private Long id;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getStatusDescription() {
			return statusDescription;
		}
		public void setStatusDescription(String statusDescription) {
			this.statusDescription = statusDescription;
		}
		private String statusDescription;
	}

}