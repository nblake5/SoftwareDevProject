package com.softwaredev.socialhub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.InstagramUserFeedRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
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
	
	private Instagram4j instagram = Instagram4j.builder().username("ITCS4155Project").password("Shiro-Hime1").build();
	
	private ConnectionRepository connectionRepository;

	public SocialController(Twitter twitter,Facebook facebook, ConnectionRepository connectionRepository) {
		this.twitter = twitter;
		this.facebook = facebook;
		this.connectionRepository = connectionRepository;
	}
	
	@GetMapping("/home")
	public String springHome()
	{
		if(connectionRepository.findPrimaryConnection(Facebook.class) == null && connectionRepository.findPrimaryConnection(Twitter.class) == null) {
			return "home";
		}
		else {
			return "redirect:/profile";
		}
	}
	
	@GetMapping("/accounts")
	public String accounts(Model model)
	{
		if(connectionRepository.findPrimaryConnection(Facebook.class) == null && connectionRepository.findPrimaryConnection(Twitter.class) == null)
		{
			return "accounts";
		}
		
		else if (connectionRepository.findPrimaryConnection(Facebook.class) != null && connectionRepository.findPrimaryConnection(Twitter.class) == null) {
			model.addAttribute("facebook", connectionRepository.findPrimaryConnection(Facebook.class));
			model.addAttribute("userNameF",facebook.feedOperations().getFeed().get(0).getFrom().getName());
			return "accounts";
		}
		else {
			model.addAttribute("facebook", connectionRepository.findPrimaryConnection(Facebook.class));
			model.addAttribute("userNameF",facebook.feedOperations().getFeed().get(0).getFrom().getName());
			model.addAttribute("twitter", connectionRepository.findPrimaryConnection(Twitter.class));
			model.addAttribute("userNameT", twitter.userOperations().getScreenName());
			model.addAttribute("userNameI", instagram.getUsername());
			return "accounts";
		}
			
	}
	
	
	@GetMapping("/profile")
	public String getAllFeeds(Model model) throws ClientProtocolException, IOException
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
		List<Tweet> feed = twitter.timelineOperations().getHomeTimeline(50);
		instagram.setup();
		instagram.login();
		InstagramFeedResult userResult = instagram.sendRequest(new InstagramUserFeedRequest(instagram.getUserId()));
		userResult.getItems();
		List<String> urls = new ArrayList<String>() ;
		for(int i =0; i < userResult.getNum_results(); i++)
		{
			urls.add(getImageUrl(userResult.getItems().get(i).getImage_versions2().get("candidates").toString()));
		}
		System.out.println(urls.get(2).toString());
		model.addAttribute("Ifeed", urls);
		model.addAttribute("profileNameI" ,instagram.getUsername());
		model.addAttribute("feed", feed);
		model.addAttribute("post", post);
		model.addAttribute("profileNameT", twitter.userOperations().getScreenName());
		model.addAttribute("profileNameF", post.get(0).getFrom().getName());
		return "profile";
	}
	
	private String getImageUrl(String candidates) {
        int ind = candidates.indexOf("url=");
        String retorno = candidates.substring(ind + 4);
        ind = retorno.indexOf("}");
        retorno = retorno.substring(0, ind);
        return retorno;
    }
	
	
	

}
