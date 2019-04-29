package com.softwaredev.socialhub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramUserFeedRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.Post;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.TweetData;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.apache.commons.codec.binary.Base64;

@Controller
public class SocialController {
	
	@Autowired
	private Twitter twitter;
	
	private Facebook facebook;
	
	private Instagram4j instagram = Instagram4j.builder().username("ITCS4155Project").password("Shiro-Hime1").build();
	
	private ConnectionRepository connectionRepository;
	
	private PostRepository postRepository;
	

	public SocialController(Twitter twitter,Facebook facebook, ConnectionRepository connectionRepository, PostRepository postRepository) {
		this.twitter = twitter;
		this.facebook = facebook;
		this.connectionRepository = connectionRepository;
		this.postRepository = postRepository;
	}
	
	//Display home screen when no social accounts are linked. Redirects to profile page if accounts connected
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
	
	//Shows linked accounts or allows user to link account if social account not linked
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
	
	//Displays Facebook, Twitter and Instagram feeds. 
	//Due to facebook privacy policy we are limited to what feed data we can pull. With increased permission the code to pull information is the same.
	@GetMapping("/profile")
	public String getAllFeeds(Model model) throws ClientProtocolException, IOException
	{
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
		model.addAttribute("profileImage" , Base64.encodeBase64String(facebook.userOperations().getUserProfileImage()));
		model.addAttribute("feed", feed);
		model.addAttribute("post", post);
		model.addAttribute("profileNameT", twitter.userOperations().getScreenName());
		model.addAttribute("profileNameF", post.get(0).getFrom().getName());
		return "profile";
	}
	
	//Display New Post Form
	@GetMapping("/newPost")
	public String getNewPost(Model model, com.softwaredev.socialhub.Post post)
	{
		model.addAttribute("post", post);
		return "newPost";
	}
	
	//Due to facebook and instagram's change in privacy policy we are unable to post content to Facebook and Instagram social media accounts.
	//The needed code to do so will be added put commented out.
	@PostMapping("/newPost")
	public String createPost(com.softwaredev.socialhub.Post post, RedirectAttributes ra) throws IOException {
		
		if(post.getTwitter()==true && post.getFacebook()==false && post.getInstagram()==false) {
			if(post.getMessage().length() > 280)
			{
				ra.addFlashAttribute("flashMessage", "The post exceededs Twitters 280 character limit!!");
				return "redirect:/newPost";
			}
			else if(post.getMessage() =="")
			{
				ra.addFlashAttribute("flashMessage", "You Did Not Enter A Message To Post!! Try Again.");
				return "redirect:/newPost";
			}
			else
			{
				if(!post.getMedia().exists())
				{
					TweetData data = new TweetData(post.getMessage());
					twitter.timelineOperations().updateStatus(data);
				}
				else
				{
					TweetData withMedia = new TweetData(post.getMessage()).withMedia(post.getMedia());
					twitter.timelineOperations().updateStatus(withMedia);
				}
			}
		}
		else if (post.getFacebook()==true||post.getInstagram()==true)
		{
			ra.addFlashAttribute("flashMessage","Sorry, Cant Post To Facebook Or Instagram At This Time!!");
			return "redirect:/newPost";
		}
		else
		{
			ra.addFlashAttribute("flashMessage","You Did Not Say Where You Would Like to Post!!");
			return "redirect:/newPost";
		}
		
		/*
		 * Code need to post to facebook and instagram if granted access to permissions
		if(!post.getMedia().exists())
		{
			//facebook.feedOperations().updateStatus(post.getMessage());
		}
		else 
		{
			FileSystemResource f = new FileSystemResource(post.getMedia());
			facebook.mediaOperations().postPhoto(f, post.getMessage());
			instagram.sendRequest(new InstagramUploadPhotoRequest(new File(post.getMedia().getPath()),post.getMessage()));
		}
		*/
		return "redirect:/profile";
	}
	
	//Used to strip out image url from return json for instagram 
	private String getImageUrl(String candidates) {
        int ind = candidates.indexOf("url=");
        String retorno = candidates.substring(ind + 4);
        ind = retorno.indexOf("}");
        retorno = retorno.substring(0, ind);
        return retorno;
    }
	
	
	

}
