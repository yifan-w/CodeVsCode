package com.codevscode.common;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

//import org.hibernate.validator.internal.util.logging.Log_.logger;
//import org.hibernate.validator.internal.util.logging.Log_.logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Component;

import com.codevscode.challenge.Challenge;
import com.codevscode.problem.Problem;
import com.codevscode.problem.Result;
import com.codevscode.problem.Solution;
import com.codevscode.user.User;

/*
 * JDBC query result row mapper for Problem
 */
final class ProblemMapper implements RowMapper<Problem> {
	public Problem mapRow(ResultSet rs, int rowNum) throws SQLException {
		Problem problem = new Problem((long) rs.getInt("id"), rs.getString("titl"), rs.getString("description"));
		problem.setSphereEngineCode(rs.getString("sphEngCode"));
		problem.setCreatedBy((long) rs.getInt("uid"));
		problem.setCreatedDate(rs.getDate("crt_date"));
		problem.setDifficulty(rs.getInt("difficulty"));
		problem.setUserName(rs.getString("userName"));
		problem.setNum_Success(rs.getInt("num_success"));
		problem.setNum_Total(rs.getInt("num_total"));
		return problem;
	}
}

/*
 * JDBC query result row mapper for Challenge
 */
final class ChallengeMapper implements RowMapper<Challenge> {
	public Challenge mapRow(ResultSet rs, int rowNum) throws SQLException {
		List<Long> uids = new LinkedList<Long>();
		List<String> list = Arrays.asList(rs.getString("uids").replaceAll("[^-?0-9]+", " ").trim().split(" "));
		for (String s: list) {
			if (s.length() > 0)
				uids.add((long) Integer.parseInt(s));
		}
		Challenge challenge = new Challenge((long) rs.getInt("id"), (long)rs.getInt("pid"), (long)rs.getInt("crt_id"),
				rs.getInt("permission"), uids);
		return challenge;
	}
}

/*
 * JDBC query result row mapper for User
 */
final class UserMapper implements RowMapper<User> {
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		User user = new User(rs.getLong("id"), rs.getString("name"), rs.getString("email"));
		user.setPasswordHash(rs.getString("passwordHash"));
		List<Long> friends = new LinkedList<Long>();
		List<String> list = Arrays.asList(rs.getString("friends").replaceAll("[^-?0-9]+", " ").trim().split(" "));
		for (String s : list) {
			if (s.length() > 0) {
				friends.add((long) Integer.parseInt(s));
			}
		}
		user.setFriends(friends);
		user.setFid(rs.getString("fid"));
		user.setNum_Success(rs.getInt("num_success"));
		user.setNum_Total(rs.getInt("num_total"));
		
		Blob blob = rs.getBlob("pic");
		
		if (blob == null || blob.length() == 0)
			return user;

		byte[] pic = new byte[(int)blob.length()];
		try {
			blob.getBinaryStream().read(pic);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		user.setProfilePic(pic);

		return user;
	}
}

/*
 * JDBC query result row mapper for Solution
 */
final class SolutionMapper implements RowMapper<Solution> {
	public Solution mapRow(ResultSet rs, int rowNum) throws SQLException {
		Solution solution = new Solution((long) rs.getInt("id"), rs.getString("source"), (long) rs.getInt("uid"),
				(long) rs.getInt("pid"), rs.getInt("language"), rs.getInt("elapsedtime"), (long)rs.getInt("cid"));
		solution.setResultId((long)rs.getInt("resultId"));
		return solution;
	}
}

/*
 * JDBC query result row mapper for Result
 */
final class ResultMapper implements RowMapper<Result> {
	public Result mapRow(ResultSet rs, int rowNum) throws SQLException {
		Result result = new Result(rs.getInt("elapsedTime"), rs.getDouble("executionTime"), rs.getString("status"),
				rs.getInt("memory"), rs.getInt("score"), null, rs.getString("stdout"), rs.getString("stderr"), 
				rs.getString("cmperr"));
		result.setId(rs.getInt("id"));
		return result;
	}
}

/*
 * JDBC query result row mapper for Comment
 */
final class CommentMapper implements RowMapper<Comment> {
	public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
		Comment comment = new Comment((long)rs.getInt("uid"), (long) rs.getInt("pid"), (long) rs.getInt("cid"), rs.getTimestamp("time"), rs.getString("content"));
		comment.setId((long)rs.getInt("id"));
		return comment;
	}
}

@Component
public class Database {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	String SQL;

	/**
	 * Finds all problems stored in database
	 * 
	 * @return a List of problems
	 */
	public List<Problem> listProblems() {
		SQL = "select * from problems";
		List<Problem> problems = jdbcTemplate.query(SQL, new ProblemMapper());

		return problems;
	}

	/**
	 * Finds a problem by unique ID
	 * 
	 * @param id
	 *            Unique ID of the problem
	 * @return Problem that matches the ID
	 */
	public Problem findProblemById(Long id) {

		SQL = "select * from problems where id = ?";
		Problem problem = (Problem) jdbcTemplate.queryForObject(SQL, new Object[] { id }, new ProblemMapper());
		return problem;
	}

	/**
	 * Finds a problem by Sphere Engine problem code
	 * 
	 * @param code
	 *            Sphere Engine Code of the problem
	 * @return Problem that matches the code
	 */
	public Problem findProblemBySphereEngineCode(String code) {
		SQL = "select * from problems where sphEngCode = ?";
		Problem problem = (Problem) jdbcTemplate.queryForObject(SQL, new Object[] { code }, new ProblemMapper());
		return problem;
	}

	/**
	 * Finds a user by unique ID
	 * 
	 * @param id
	 *            unique ID of user
	 * @return a User
	 */
	public User findUserById(Long id) {
		SQL = "select * from users where id = ?";
		User user = (User) jdbcTemplate.queryForObject(SQL, new Object[] { id }, new UserMapper());
		return user;
	}

	/**
	 * Finds a user by the user's name
	 * 
	 * @param name
	 *            user's name (aka username)
	 * @return a User
	 */
	public User findUserByName(String name) {
		SQL = "select * from users where name = ?";
		User user = (User) jdbcTemplate.queryForObject(SQL, new Object[] { name }, new UserMapper());
		return user;
	}
	
	/**
	 * Finds users who did a problem
	 * 
	 * @param pid
	 *            the problem's id
	 * @return a list of uids
	 */
	public List<Long> findUserByPid(Long pid) {
		SQL = "select * from solutions where pid = ? and cid = 0";
		List<Solution> solutions = (List<Solution>) jdbcTemplate.query(SQL, new Object[] { pid }, new SolutionMapper());
		List<Long> uids = new ArrayList<Long>();
		for (Solution solution: solutions)
			if (!uids.contains(solution.getUser()))
				uids.add(solution.getUser());
		return uids;
	}
	
	/**
	 * Finds a user by the user's facebook id
	 * 
	 * @param fid
	 *            user's facebook id
	 * @return a User
	 */
	public User findUserByFid(String fid) {
		SQL = "select * from users where fid = ?";
		User user = null;
		try {
			user = (User) jdbcTemplate.queryForObject(SQL, new Object[] { fid }, new UserMapper());
		} catch (DataAccessException e) {
			return null;
		}
		return user;
	}

	/**
	 * Finds a solution by unique ID
	 * 
	 * @param id
	 *            solution ID
	 * @return a Solution
	 */
	public Solution findSolutionById(Long id) {
		SQL = "select * from solutions where id = ?";
		Solution solution = (Solution) jdbcTemplate.queryForObject(SQL, new Object[] { id }, new SolutionMapper());
		return solution;
	}

	/**
	 * Finds a solution by using a problem and a user
	 * 
	 * @param problem
	 *            a Problem
	 * @param user
	 *            a User
	 * @return a Solution
	 */
	public Solution findSolutionByUserAndProblem(Long pid, Long uid) {
		SQL = "select * from solutions where pid = ? and uid = ?";
		Solution solution = (Solution) jdbcTemplate.queryForObject(SQL, new Object[] { pid, uid },
				new SolutionMapper());
		return solution;
	}
	
	public Solution findSolutionByUserAndProblemNoChallenge(Long pid, Long uid) {
		SQL = "select * from solutions where pid = ? and uid = ? and cid = 0";
		Solution solution = (Solution) jdbcTemplate.queryForObject(SQL, new Object[] { pid, uid },
				new SolutionMapper());
		return solution;
	}
	
	/**
	 * Finds a solution by using a challenge and a user
	 * 
	 * @param challenge
	 *            a Challenge
	 * @param user
	 *            a User
	 * @return a Solution
	 */
	public Solution findSolutionByUserAndChallenge(Long cid, Long uid) {
		SQL = "select * from solutions where cid = ? and uid = ?";
		Solution solution = (Solution) jdbcTemplate.queryForObject(SQL, new Object[] { cid, uid },
				new SolutionMapper());
		return solution;
	}
	
	/**
	 * Finds a challenge by challenge id
	 * 
	 * @param cid
	 *            a Challenge id
	 * @return a Challenge
	 */
	public Challenge findChallengeById(Long cid) {
		SQL = "select * from challenges where id = ?";
		Challenge challenge = (Challenge) jdbcTemplate.queryForObject(SQL, new Object[] { cid },
				new ChallengeMapper());
		return challenge;
	}
	
	
	/**
	 * Finds a list of challenges the user is participating in
	 * 
	 * @param uid
	 * @return list of challenges
	 */
	public List<Challenge> findChallengesByUid(long uid) {
		SQL = "select * from challenges";
		List<Challenge> list = jdbcTemplate.query(SQL, new ChallengeMapper());
		List<Challenge> list_return = new ArrayList<>();
		for (Challenge challenge: list)
			if (challenge.getUsers().contains(uid))
				list_return.add(challenge);
		return list_return;
	}
	
	/**
	 * Finds participating users in a challenge by challenge id
	 * 
	 * @param cid
	 *            a Challenge id
	 * @return a list of User ids
	 */
	public List<Long> findUsersInChallenge(Long cid) {
		Challenge challenge = findChallengeById(cid);
		List<Long> uids = challenge.getUsers();
		return uids;
	}
	
	/**
	 * Finds a result by id
	 */
	public Result findResultById(Long id) {
		SQL = "select * from results where id = ?";
		Result result = (Result) jdbcTemplate.queryForObject(SQL, new Object[] { id },
				new ResultMapper());
		return result;
	}
	
	
	/**
	 * Saves a Result to the database
	 */
	public long saveResult(Result result) {
		if (result.getId() == 0) {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			SQL = "insert into results(elapsedTime, executionTime, status, memory, score, "
					+ "stdout, stderr, cmperr) values(?, ?, ?, ?, ?, ?, ?, ?)";
			jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(SQL,
							Statement.RETURN_GENERATED_KEYS);
					ps.setInt(1, result.getElapsedTime());
					ps.setDouble(2, result.getExecutionTime());
					ps.setString(3, result.getStatus());
					ps.setInt(4, result.getMemory());
					ps.setInt(5, result.getScore());
					ps.setString(6,  result.getRuntimeInfo().getStdout());
					ps.setString(7,  result.getRuntimeInfo().getStderr());
					ps.setString(8,  result.getRuntimeInfo().getCmperr());
					return ps;
				}
			}, keyHolder);
			result.setId(keyHolder.getKey().intValue());
		}
		else {
			SQL = "update results set elapsedTime = ?, executionTime = ?, status = ?, memory = ?, score = ?, "
					+ "stdout = ?, stderr = ?, cmperr = ? where id = ?";
			jdbcTemplate.update(SQL, result.getElapsedTime(), result.getExecutionTime(), result.getStatus(), result.getMemory(),
					result.getScore(), result.getRuntimeInfo().getStdout(), result.getRuntimeInfo().getStderr(),
					result.getRuntimeInfo().getCmperr(), result.getId());
		}
		return result.getId();
	}

	/**
	 * Saves a Problem to the database
	 * 
	 * If it is the first save, will set a problem's id and sphereEngineCode
	 * accordingly
	 * 
	 * @param problem
	 *            Problem to be saved
	 */
	public void saveProblem(Problem problem) {
		if (problem.getId() == null) {
			SQL = "insert into problems (sphEngCode, titl, description, crt_date, uid, difficulty, userName, num_success, num_total) "
					+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			KeyHolder keyHolder = new GeneratedKeyHolder();

			final PreparedStatementCreator psc = new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
					final PreparedStatement ps = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, problem.getSphereEngineCode());
					ps.setString(2, problem.getTitle());
					ps.setString(3, problem.getDescription());
					ps.setDate(4, new Date(problem.getCreatedDate().getTime()));
					ps.setLong(5, problem.getCreatedBy());
					ps.setInt(6,  problem.getDifficulty());
					ps.setString(7,  findUserById(problem.getCreatedBy()).getName());
					ps.setInt(8,  (int)problem.getNum_Success());
					ps.setInt(9, (int)problem.getNum_Total());
					return ps;
				}
			};
			jdbcTemplate.update(psc, keyHolder);
			problem.setId(keyHolder.getKey().longValue());
			problem.setSphereEngineCode("CVC" + problem.getId());
			SQL = "UPDATE problems SET sphEngCode = ? WHERE id = ?";
			jdbcTemplate.update(SQL, problem.getSphereEngineCode(), problem.getId());
		} else {
			SQL = "UPDATE problems SET sphEngCode = ?, titl = ?, description = ?, crt_date = ?, uid = ?, difficulty = ?, "
					+ " num_success = ?, num_total = ? WHERE id = ?";
			jdbcTemplate.update(SQL, problem.getSphereEngineCode(), problem.getTitle(), problem.getDescription(),
					problem.getCreatedDate(), problem.getCreatedBy(), problem.getDifficulty(), 
					problem.getNum_Success(), problem.getNum_Total(), problem.getId());
		}
	}

	/**
	 * Saves a User to the database
	 * 
	 * @param user
	 *            User to be saved
	 */
	public void saveUser(User user) {
		if (user.getId() == null) {
			SQL = "insert into users (name, email, passwordHash, friends, fid, pic, num_success, num_total) values (?, ?, ?, ?, ?, ?, ?, ?)";
			String friends = "";
			for (Long tmp : user.getFriends()) {
				friends = friends + tmp + " ";
			}
			if (user.getProfilePic() == null) {
				jdbcTemplate.update(SQL, user.getName(), user.getEmail(), user.getPasswordHash(), friends, user.getFid(),
						null, user.getNum_Success(), user.getNum_Total());
			}
			else {
				final InputStream imageIs = new ByteArrayInputStream(user.getProfilePic());   
				LobHandler lobHandler = new DefaultLobHandler(); 
				jdbcTemplate.update(SQL, new Object[] {user.getName(), user.getEmail(), user.getPasswordHash(), friends, user.getFid(),
						new SqlLobValue(imageIs, user.getProfilePic().length, lobHandler), user.getNum_Success(), user.getNum_Total()},
						new int[] {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, 
								Types.VARCHAR, Types.BLOB, Types.INTEGER, Types.INTEGER});
			}
		} else {
			SQL = "update users set name = ?, email = ?, passwordHash = ?, friends = ?, fid = ?, "
					+ "pic = ?, num_success = ?, num_total = ? WHERE id = ?";
			String friends = "";
			for (Long tmp : user.getFriends()) {
				friends = friends + tmp + " ";
			}
			if (user.getProfilePic() == null) {
				jdbcTemplate.update(SQL, user.getName(), user.getEmail(), user.getPasswordHash(), friends, user.getFid(),
						null, user.getNum_Success(), user.getNum_Total(), user.getId());
			}
			else {
				final InputStream imageIs = new ByteArrayInputStream(user.getProfilePic());   
				LobHandler lobHandler = new DefaultLobHandler(); 
				jdbcTemplate.update(SQL, new Object[] {user.getName(), user.getEmail(), user.getPasswordHash(), friends, user.getFid(),
						new SqlLobValue(imageIs, user.getProfilePic().length, lobHandler), 
						user.getNum_Success(), user.getNum_Total(), user.getId()},
						new int[] {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, 
								Types.VARCHAR, Types.VARCHAR, Types.BLOB, Types.INTEGER, Types.INTEGER, Types.INTEGER});
			}

		}
	}

	/**
	 * Saves a Solution to the database
	 * 
	 * @param solution
	 *            Solution to be saved
	 */
	public void saveSolution(Solution solution) {
		boolean exists = true;
		try {
			if (solution.getCid() == 0) {
				Solution tmp = findSolutionByUserAndProblemNoChallenge(solution.getProblemId(), solution.getUser());
				SQL = "DELETE from results where id = ?";
				jdbcTemplate.update(SQL, tmp.getResultId());
			} else {
				Solution tmp = findSolutionByUserAndChallenge(solution.getCid(), solution.getUser());
				SQL = "DELETE from results where id = ?";
				jdbcTemplate.update(SQL, tmp.getResultId());
			}
		} catch (DataAccessException e) {
			exists=false;
		}
		if (!exists) {
			SQL = "insert into solutions (source, uid, pid, cid, language, elapsedtime, resultId) values (?, ?, ?, ?, ?, ?, ?)";
			jdbcTemplate.update(SQL, solution.getSource(), solution.getUser(), solution.getProblemId(),
					solution.getCid(), solution.getLanguage(), solution.getElapsedTime(), solution.getResultId());
		} else {
			//update
			SQL = "UPDATE solutions SET source = ?, language = ?, elapsedtime = ?, uid = ?, pid = ?, cid = ?, resultId = ? WHERE id = ?";
			jdbcTemplate.update(SQL, solution.getSource(), solution.getLanguage(), solution.getElapsedTime(), solution.getUser(), solution.getProblemId(), solution.getCid(), solution.getResultId(), solution.getId());

		}
	}
	
	/**
	 * Saves a Challenge to the database
	 * 
	 * @param challenge
	 *            Challenge to be saved
	 */
	
	public void saveChallenge(Challenge challenge) {
		String uids = "";
		if (challenge.getId() == 0) {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			SQL = "insert into challenges (pid, crt_id, uids, permission) values (?, ?, ?, ?)";
			for (long uid: challenge.getUsers())
				uids = uids + uid + " ";
			final String uids_insert = uids;
			jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(SQL,
							Statement.RETURN_GENERATED_KEYS);
					ps.setLong(1, challenge.getPid());
					ps.setLong(2, challenge.getCrtId());
					ps.setString(3, uids_insert);
					ps.setInt(4, challenge.getPermission());
					return ps;
				}
			}, keyHolder);
			challenge.setId(keyHolder.getKey().intValue());
		} else {
			SQL = "UPDATE challenges SET uids = ?, permission = ? WHERE id = ?";
			for (long uid: challenge.getUsers())
				uids = uids + uid + " ";
			jdbcTemplate.update(SQL, uids, challenge.getPermission(), challenge.getId());
		}
	}
	/**
	 * Delete a Challenge to the database
	 *
	 * @param challenge
	 *            Challenge to be deleted
	 */
	public void deleteChallenge(Challenge challenge) {
		List<Long> users = challenge.getUsers();
		//delete all results in challenge
		for(long user : users) {
			try{
			Solution tmp = findSolutionByUserAndChallenge(challenge.getId(), user);
			SQL = "DELETE from results where id = ?";
			jdbcTemplate.update(SQL, tmp.getResultId());
			} catch (DataAccessException e) {
				
			}

			//delete challenge on each users' challenge list
			User participant= findUserById(user);
			participant.deleteChallenge(challenge.getId());
			saveUser(participant);
		}
		
		//delete solutions in challenge
		SQL = "DELETE from solutions WHERE cid = ?";
		jdbcTemplate.update(SQL, challenge.getId());
		

		//delete challenge
		SQL = "DELETE from challenges WHERE id = ?";
		jdbcTemplate.update(SQL, challenge.getId());
	}
	
	public void leaveChallenge(Challenge challenge, Long user_id) {
		//Delete user_id's result in Challenge
		try{
			Solution tmp = findSolutionByUserAndChallenge(challenge.getId(), user_id);
			SQL = "DELETE from results where id = ?";
			jdbcTemplate.update(SQL, tmp.getResultId());
		} catch (DataAccessException e) {	
			
		}
		//Delete challenge in user_id's challenge list
		User participant = findUserById(user_id);
		participant.deleteChallenge(challenge.getId());
		saveUser(participant);
		//Delete user_id's solution
		SQL = "DELETE from solutions WHERE cid = ? AND uid = ?";
		jdbcTemplate.update(SQL,challenge.getId(),user_id);
		
		//remove user from challenge
		removeUserFromChallenge(user_id,challenge.getId());			
		
	}
	/**
	 * Add a participating user into a challenge
	 * 
	 * @param uid
	 * 			Id of the user being added
	 * @param cid
	 * 			Id of the challenge
	 */
	public void addUserToChallenge(Long uid, Long cid) {
		Challenge challenge = findChallengeById(cid);
		List<Long> uids = challenge.getUsers();
		uids.add(uid);
		challenge.setUsers(uids);
		saveChallenge(challenge);
	}
	
	/**
	 * Remove a participating user from a challenge
	 * 
	 * @param uid
	 * 			Id of the user being removed
	 * @param cid
	 * 			Id of the challenge
	 */
	public void removeUserFromChallenge(Long uid, Long cid) {
		Challenge challenge = findChallengeById(cid);
		List<Long> uids = challenge.getUsers();
		uids.remove(uid);
		challenge.setUsers(uids);
		saveChallenge(challenge);
	}
	
	/**
	 * Saves a Comment to the database
	 */
	public long saveComment(Comment comment) {
		if (comment.getId() == null) {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			SQL = "insert into comments(uid, pid, cid, time, content) values(?, ?, ?, ?, ?)";
			jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(SQL,
							Statement.RETURN_GENERATED_KEYS);
					ps.setInt(1, comment.getUid().intValue());
					ps.setInt(2, comment.getPid().intValue());
					ps.setInt(3, comment.getCid().intValue());
					ps.setTimestamp(4, comment.getTime());
					ps.setString(5, comment.getContent());
					return ps;
				}
			}, keyHolder);
			comment.setId((long)keyHolder.getKey().intValue());
		}
		else {
			SQL = "update comments set uid = ?, pid = ?, cid = ?, time = ?, content = ? where id = ? ";
			jdbcTemplate.update(SQL, comment.getUid(), comment.getPid(), comment.getCid(), comment.getTime(), comment.getContent());
		}
		return comment.getId();
	}
	
	/**
	 * Finds a comment by unique ID
	 * 
	 * @param id
	 *            unique ID of comment
	 * @return a Comment
	 */
	public Comment findCommentById(Long id) {
		SQL = "select * from comments where id = ?";
		Comment comment = (Comment) jdbcTemplate.queryForObject(SQL, new Object[] { id }, new CommentMapper());
		return comment;
	}
	
	public List<Comment> findCommentByPid(Long pid) {
		SQL = "select * from comments where pid = ?";
		Object[] args = {pid};
		List<Comment> comments = jdbcTemplate.query(SQL, args, new CommentMapper());
		return comments;
	}
	
	public List<Comment> findCommentByCid(Long cid) {
		SQL = "select * from comments where cid = ?";
		Object[] args = {cid};
		List<Comment> comments = jdbcTemplate.query(SQL, args, new CommentMapper());
		return comments;
	}
}
