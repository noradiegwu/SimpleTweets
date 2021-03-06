package com.codepath.apps.mysimpletweets.models;

/*


 */


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

//parse the JSON and store the data
// Encapsalate state logic or display logic
@Parcel
public class Tweet {
    // List out the attributes
    private String body;
    private String createdAt;
    private boolean favorited;
    private boolean retweeted;
    private int retweetsCount;
    private int likesCount;
    private long inReplyToUserID;
    private long uid; // unique id for the tweet
    private User user; // store embedded user object


    public boolean isFavorited() {
        return favorited;
    }


    public boolean isRetweeted() {
        return retweeted;
    }

    public int getRetweetsCount() {
        return retweetsCount;
    }


    public int getLikesCount() {
        return likesCount;
    }

    public User getUser() { return user; }

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public long getUid() {
        return uid;
    }

    public long getInReplyToUserID() {
        return inReplyToUserID;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }


    public Tweet() {}

    // Deerialize the json
    // Tweet.fromJSON("{...}") => <Tweet>
    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        // extract the values from the json, store them
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.likesCount = jsonObject.getInt("favorite_count");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            tweet.favorited = jsonObject.optBoolean("favorited");
            tweet.inReplyToUserID = jsonObject.optLong("in_reply_to_user_id");
            tweet.retweetsCount = jsonObject.optInt("retweet_count");
            tweet.retweeted = jsonObject.optBoolean("retweeted");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Return the tweet object
        return tweet;
    }

    public static Tweet fromStatus(String status) {
        Tweet tweet = new Tweet();
        tweet.body = status;
        //tweet.uid = ...
        //tweet.createdAt = ...
        //tweet.user = ...
        return tweet;
    }

    // Tweet.fromJSONArray([{...}, {...}, ...]) => list of tweets
    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        // Iterate the json array and create tweets
        for (int i = 0; i < jsonArray.length() ; i++) {
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetJson);
                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        // return the finished list
        return tweets;

    }


}
