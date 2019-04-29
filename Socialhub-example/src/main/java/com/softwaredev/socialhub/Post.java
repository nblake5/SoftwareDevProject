package com.softwaredev.socialhub;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.springframework.core.io.FileSystemResource;


@Entity
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String message;
	@OneToOne(targetEntity = Post.class)
	private FileSystemResource media;
	private Date postTime;
	private Date created_at;
	private Boolean twitter = false;
	private Boolean facebook = false;
	private Boolean instagram = false;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public FileSystemResource getMedia() {
		return media;
	}
	public void setMedia(FileSystemResource media) {
		this.media = media;
	}
	public Date getPostTime() {
		return postTime;
	}
	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	public Boolean getTwitter() {
		return twitter;
	}
	public void setTwitter(Boolean twitter) {
		this.twitter = twitter;
	}
	public Boolean getFacebook() {
		return facebook;
	}
	public void setFacebook(Boolean facebook) {
		this.facebook = facebook;
	}
	public Boolean getInstagram() {
		return instagram;
	}
	public void setInstagram(Boolean instagram) {
		this.instagram = instagram;
	}
	

}
