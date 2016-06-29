package com.codepath.apps.mysimpletweets;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1/"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "D6Pi22jDa8EZU2BtLCc5MdU71";       // Change this
	public static final String REST_CONSUMER_SECRET = "AjYUyJGJ7yKKDmV5t23tdcMKkqOW6kfYXFiFFeRbEBkAmx0YNS"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}


	//METHOD = ENDPOINT
	//////////////////////
	//Hometimeline - Gets us the home timeline
	/*
	- Get the home timeline for the user
	$ GET statuses/home_timeline
	$ https://api.twitter.com/1.1/statuses/home_timeline.json
	$ count = 25
	$ since_id = 1 (returns all tweets)
	*/
	public void getHomeTimeline(AsyncHttpResponseHandler handler) {
		String apiurl = getApiUrl("statuses/home_timeline.json");
		// Specify params
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("since_id", 1);
		// Execute request
		getClient().get(apiurl, params, handler);
	}

	public void getMentionsTimeline(AsyncHttpResponseHandler handler) {
		String apiurl = getApiUrl("statuses/mentions_timeline.json");
		// Specify params
		RequestParams params = new RequestParams();
		params.put("count", 25);
		// Execute request
		getClient().get(apiurl, params, handler);
	}

    public void getUserTimeline(String screenName, AsyncHttpResponseHandler handler) {
        String apiurl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put("screen_name", screenName);
        // Execute request
        getClient().get(apiurl, params, handler);

    }

    public void getUserInfo(AsyncHttpResponseHandler handler) {
        String apiurl = getApiUrl("account/verify_credentials.json");
        // Execute request
		// if null, it uses verified user
        getClient().get(apiurl, null, handler);

    }

	//COMPOSE TWEET METHOD
    // a "tweet" is a status
    public void updateStatus(String status, JsonHttpResponseHandler handler) {
        String apiurl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", status);
        //execute request
        getClient().post(apiurl, params, handler);
    }

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */


}