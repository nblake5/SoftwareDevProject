package com.softwaredev.socialhub;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.Post;
import org.springframework.social.twitter.api.MediaEntity;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SocialController {
	
	@Autowired
	private Twitter twitter;
	
	private Facebook facebook;
	
	private ConnectionRepository connectionRepository;

	public SocialController(Twitter twitter,Facebook facebook, ConnectionRepository connectionRepository) {
		this.twitter = twitter;
		this.facebook = facebook;
		this.connectionRepository = connectionRepository;
	}
	
	@GetMapping("/home")
	public String springHome()
	{
		return "home";
	}
	
	@GetMapping("accounts")
	public String accounts()
	{
		return "accounts";
	}
	
	
	@GetMapping("/profile")
	public String getAllFeeds(Model model)
	{
		/*
		if(connectionRepository.getPrimaryConnection(Twitter.class) == null && connectionRepository.getPrimaryConnection(Facebook.class)==null)
		{
			return "home";
		}
		/*
		if(connectionRepository.getPrimaryConnection(Twitter.class)==null)
		{
			return "redirect:/connect/twitter";
		}
		if(connectionRepository.getPrimaryConnection(Facebook.class)==null)
		{
			return "redirect:/connect/facebook";
		}
		*/
		PagedList<Post> post = facebook.feedOperations().getFeed();
		List<Tweet> feed = twitter.timelineOperations().getHomeTimeline(30);
		model.addAttribute("feed", feed);
		model.addAttribute("post", post);
		model.addAttribute("profileNameT", twitter.userOperations().getScreenName());
		model.addAttribute("profileNameF", post.get(0).getFrom().getName());
		return "profile";
	}
	
	
	

}
