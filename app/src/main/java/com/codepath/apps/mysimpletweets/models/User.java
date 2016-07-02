package com.codepath.apps.mysimpletweets.models;


import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {
    /*
    "user": {
      "name": "OAuth Dancer",
      "profile_sidebar_fill_color": "DDEEF6",
      "profile_background_tile": true,
      "profile_sidebar_border_color": "C0DEED",
      "profile_image_url": "http://a0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg",
      "created_at": "Wed Mar 03 19:37:35 +0000 2010",
      "location": "San Francisco, CA",
      }
     */

    // list the attr.
    private String name;
    private String screenName;
    private String profileImageUrl;
    private String tagline;
    private int followersCount;
    private int followingsCount;
    private long uid;

    public boolean isFollowing() {
        return following;
    }

    private boolean following;

    public String getName() {
        return name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getScreenName() {
        return screenName;
    }

    public long getUid() {
        return uid;
    }

    public String getTagline() {
        return tagline;
    }

    public int getFollowingsCount() {
        return followingsCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public User() {}

    // deserialize into user
    public static User fromJSON(JSONObject json) {
        User u = new User();
        // extract and fill the values

        try {
            u.name = json.getString("name");
            u.uid = json.optLong("id");
            u.screenName = json.optString("screen_name");
            u.profileImageUrl = json.optString("profile_image_url");
            u.tagline = json.optString("description");
            u.followersCount = json.optInt("followers_count");
            u.followingsCount = json.optInt("friends_count");
            u.following = json.optBoolean("following");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return u;
    }

}
