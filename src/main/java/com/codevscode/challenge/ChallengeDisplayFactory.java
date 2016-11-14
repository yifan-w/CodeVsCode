package com.codevscode.challenge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.codevscode.common.Database;
import com.codevscode.problem.Problem;
import com.codevscode.user.User;

@Component
public class ChallengeDisplayFactory {

	private Logger log = LoggerFactory.getLogger(ChallengeDisplayFactory.class);
	
	public static final int defaultMaxUsersGenerated = 5;

	@Autowired
	private Database database;

	/**
	 * Factory method to return a new ChallengProfileDisplay filled with the
	 * information of the supplied challenge
	 * 
	 * @param challenge
	 *            Challenge the display is generated from
	 * @param maxUsersGenerated
	 *            max number of users to be generated in userString
	 * @return generated display
	 */
	public ChallengeDisplay getChallengeDisplay(Challenge challenge, int maxUsersGenerated) {
		ChallengeDisplay display = new ChallengeDisplay();
		display.setUsersString(generateUserString(challenge, maxUsersGenerated));
		display.setProblemTitle(generateProblemTitle(challenge));
		display.setcId(challenge.getId());
		return display;
	}
	
	public ChallengeDisplay getChallengeDisplay(Challenge challenge) {
		return getChallengeDisplay(challenge, defaultMaxUsersGenerated);
	}

	/**
	 * Generates the displayed users string. If participating users exceed
	 * maxUsersGenerated, then it generate maxUsersGenerated users and append
	 * "..."
	 * 
	 * @param challenge
	 *            Challenge the display is generated from
	 * @return generated user string
	 */
	private String generateUserString(Challenge challenge, int maxUsersGenerated) {
		StringBuilder builder = new StringBuilder();
		if (challenge.getUsers().size() <= maxUsersGenerated) {
			for (int i = 0; i < challenge.getUsers().size(); i++) {
				try {
					User user = database.findUserById(challenge.getUsers().get(i));
					builder.append(user.getName());
					builder.append(", ");
				} catch (DataAccessException e) {
					// wat
					log.info("user not found: " + challenge.getUsers().get(i));
				}

				
			}
			builder.delete(builder.length() - 2, builder.length());
		} else {
			for (int i = 0; i < maxUsersGenerated; i++) {
				try {
					User user = database.findUserById(challenge.getUsers().get(i));
					builder.append(user.getName());
					builder.append(", ");
				} catch (DataAccessException e) {
					// wat
					log.info("user not found: " + challenge.getUsers().get(i));
				}
			}

			builder.delete(builder.length() - 2, builder.length());
			builder.append("...");
		}
		
		return builder.toString();
	}

	/**
	 * Generates the problem title from the challenge supplied
	 * 
	 * @param challenge
	 *            Challenge the display is generated from
	 * @return generated problem title
	 */
	private String generateProblemTitle(Challenge challenge) {
		Problem problem = database.findProblemById(challenge.getPid());
		return problem.getTitle();
	}
}
