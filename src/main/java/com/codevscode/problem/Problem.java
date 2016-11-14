package com.codevscode.problem;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.codevscode.common.Comment;

public class Problem {
	private Long id;
	private String sphereEngineCode;
	
	@NotNull(message = "Title must not be null")
	@NotEmpty(message = "Title must not be empty")
	private String title;
	
	@NotNull(message = "Description must not be null")
	@NotEmpty(message = "Description must not be empty")
	private String description;
	
	private Date createdDate;
	private Long createdBy;
	private String userName;
	
	private long num_success;
	private long num_total;
	
	private int difficulty;
	
	public Problem() {
		super();
	}

	public Problem(Long id, String title, String description) {
		this();
		this.id = id;
		this.title = title;
		this.description = description;
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

	public String getSphereEngineCode() {
		return sphereEngineCode;
	}

	public void setSphereEngineCode(String sphereEngineCode) {
		this.sphereEngineCode = sphereEngineCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = Character.toUpperCase(title.charAt(0)) + title.substring(1, title.length());
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	public int getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
