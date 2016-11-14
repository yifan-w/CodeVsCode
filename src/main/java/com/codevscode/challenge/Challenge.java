package com.codevscode.challenge;

import java.util.ArrayList;
import java.util.List;

import com.codevscode.common.Comment;
import com.codevscode.user.User;

public class Challenge {

	public static final int PUBLIC = 1;
	public static final int PRIVATE = 2;
	public static final int UNLISTED = 3;

	private static final String[] permissionStrings = { "NONE", "Public", "Private", "Unlisted" };

	private long id; // challenge id
	private long pid; // problem id
	private long crt_id; // creator id
	private int permission;
	private List<Long> uids; // participants' ids

	public Challenge() {
		super();
	}
	
	public Challenge (long id, long pid, long crt_id, int permission, List<Long> uids) {
		this();
		this.id = id;
		this.pid = pid;
		this.crt_id = crt_id;
		this.permission = permission;
		this.uids = uids;
	}

	public Long getId() {
		return id;
	}

	public long getPid() {
		return pid;
	}

	public long getCrtId() {
		return crt_id;
	}

	public int getPermission() {
		return permission;
	}

	public String getPermissionString() {
		return permissionStrings[permission];
	}

	public List<Long> getUsers() {
		return uids;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setPid(long pid) {
		this.pid = pid;
	}

	public void setCrtId(long crt_id) {
		this.crt_id = crt_id;
	}

	public void setPermission(int permission) {
		this.permission = permission;
	}

	public void setUsers(List<Long> uids) {
		this.uids = uids;
	}

	public void addUser(Long uid) {
		this.uids.add(uid);
	}

	public void delUser(Long uid) {
		for (Long l : this.uids) {
			if (l.longValue() == uid.longValue()) {
				this.uids.remove(l);
				return;
			}
		}
	}

	public boolean userInChallenge(User user) {
		for (Long uid : this.uids) {
			if (uid.longValue() == user.getId().longValue()) {
				return true;
			}
		}
		return false;
	}

	public boolean userHasAccess(User user, User creator) {
		if (this.permission == PUBLIC) {
			return true;
		} else if (this.permission == PRIVATE) {
			if (user.equals(creator)) {
				return true;
			}
			if (user.checkIfFriends(this.crt_id) && creator.checkIfFriends(user.getId())) {
				return true;
			} else {
				return false;
			}
		} else if (this.permission == UNLISTED) {
			return true;
		}
		return false;
	}
}
