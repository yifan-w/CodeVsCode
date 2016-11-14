package com.codevscode.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class User {
	
	private static final long submissionTimeLimit = 5;

	private Long id;
	

	@NotNull(message = "Username cannot be null")
	@Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters long")
	@Pattern(regexp = "[\\w]*", message = "Username contains invalid characters")
	private String name;
	
	@NotNull(message = "Email must not be null")
	@NotEmpty(message = "Email must not be empty")
	@Size(max = 254, message = "Email exceeded max length")
	@Email(message = "Not a valid email")
	private String email;
	
	@NotNull(message = "Password must not be null")
	@NotEmpty(message = "Password must not be empty")
	private String passwordHash;
	
	private String fid;
	
	private List<Long> friends;
	private AtomicLong lastSubmission;
	private List<Long> challenges;
	
	private byte[] profilePic;
	private boolean hasProfilePic;
	
	private long num_success;
	private long num_total;

	public User(Long id, String name, String email) {
		this();
		this.id = id;
		this.name = name;
		this.email = email;
		this.profilePic = null;
		this.hasProfilePic = false;
	}

	public User() {
		super();
		this.friends = new ArrayList<>();
		this.lastSubmission = new AtomicLong(0L);
		this.challenges = new ArrayList<>();
		this.profilePic = null;
		this.hasProfilePic = false;
		this.num_success = 0;
		this.num_total = 0;
	}
	
	public void setNum_Success(long num_success) {
		this.num_success = num_success;
	}
	
	public long getNum_Success() {
		return num_success;
	}
	
	public void setNum_Total(long num_total) {
		this.num_total = num_total;
	}
	
	public long getNum_Total() {
		return num_total;
	}

	public long getLastSubmission() {
		return lastSubmission.get();
	}

	public void setLastSubmission(long lastSubmission) {
		this.lastSubmission.set(lastSubmission);
	}
	
	public void setLastSubmissionNow() {
		this.lastSubmission.set(System.currentTimeMillis());
	}
	
	public long getSecondsSinceLastSubmission() {
		return (System.currentTimeMillis() - lastSubmission.get()) / 1000L;
	}
	
	public synchronized boolean checkAndUpdateSubmissionTime() {
		if (getSecondsSinceLastSubmission() < submissionTimeLimit) {
			return false;
		}
		
		setLastSubmissionNow();
		return true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public List<Long> getFriends() {
		return friends;
	}
	
	public List<Long> getChallenges() {
		return challenges;
	}
	
	public void deleteChallenge(Long cid) {
		for (Long id: challenges) {
			if (id == cid) {
				friends.remove(cid);
				return;
			}
		}
	}

	public void addFriend(Long friend) {
		friends.add(friend);
	}
	
	public void delFriend(Long friend) {
		for (Long uid: friends) {
			if (uid == friend) {
				friends.remove(uid);
				return;
			}
		}
	}

	public void setFriends(List<Long> friends) {
		this.friends = friends;
	}
	
	public boolean checkIfFriends(Long otherUser) {
		return this.friends.contains(otherUser);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getFid() {
		return fid;
	}
	
	public void setFid(String fid) {
		this.fid = fid;
	}
	
	public byte[] getProfilePic() {
		if (!hasProfilePic)
			return null;
		return profilePic;
	}
	
	public void setProfilePic(byte[] pic) {
		this.profilePic = pic;
		this.hasProfilePic = true;
	}
	
	public boolean hasPic() {
		return hasProfilePic;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}