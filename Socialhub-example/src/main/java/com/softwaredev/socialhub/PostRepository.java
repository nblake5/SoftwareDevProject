package com.softwaredev.socialhub;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface PostRepository extends JpaRepository<Post, Long> {
	@Query(value = "SELECT i FROM Post i")
	
	List<Post> findAllPosts();
	List<Post> findByPostTime(Date postTime);
	Post findPostById(Long id);

}
