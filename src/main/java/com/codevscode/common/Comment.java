package com.codevscode.common;

import java.sql.Timestamp;

public class Comment {
	private Long id;
	private Long pid;
	private Long cid;
	private Long uid;
	private Timestamp time;
	private String content;
	
	public Comment() {
		super();
		this.pid = 0L;
		this.cid = 0L;
	}
	
	public Comment(Long uid, Long pid, Long cid, Timestamp time, String content) {
		this();
		this.uid = uid;
		this.pid = pid;
		this.time = time;
		this.content = content;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setUid(Long uid) {
		this.uid = uid;
	}
	
	public Long getUid() {
		return uid;
	}
	
	public void setTime(Timestamp time) {
		this.time = time;
	}
	
	public Timestamp getTime() {
		return time;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getContent() {
		return content;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}
}
