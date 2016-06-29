package com.codepath.apps.mysimpletweets.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.Fragments.UserTimelineFragment;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {
    public static int PROFILE_INTENT = 5005;
    public static int IMAGE_INTENT = 6006;
    TwitterClient client;
    public String screenName;
    int myIntent;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        myIntent = getIntent().getIntExtra("myIntent", 0);
        if (user != null) {
            if (myIntent == ProfileActivity.IMAGE_INTENT) {
                getSupportActionBar().setTitle("@" + user.getScreenName());
                populateProfileHeader(user);
            }
        }
        // get access to client
        client = TwitterApplication.getRestClient();
        // Get account info
        client.getUserInfo(new JsonHttpResponseHandler() { //

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // if I am the user (uid == myUid) ... but user is null from profile guy click
                // So if the profile person intent started me
                    // if (getIntent == the profile person intent)
                // do this

                if (myIntent == ProfileActivity.PROFILE_INTENT) {
                    user = User.fromJSON(response);
                    // my current user account info
                    getSupportActionBar().setTitle("@" + user.getScreenName());
                    populateProfileHeader(user);
                }

            }


        });

        // Get the screen name
        // populateUserTimeline
        screenName = getIntent().getStringExtra("screen_name");
        // Create the user timeline fragment
        if (savedInstanceState == null) {
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(screenName);
            // Display user fragment w/in activity (dynamically)
            // a fragment transaction is simply a statement of intent to change the fragment on screen
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fragmentUserTimeline);
            ft.commit(); // commit the transaction
        }
    }


    private void populateProfileHeader(User user) {
        //find views
        TextView tvFullName = (TextView) findViewById(R.id.tvFullName);
        TextView tvTagline = (TextView) findViewById(R.id.tvTagline);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        // set views
        tvFullName.setText(user.getName());
        tvTagline.setText(user.getTagline());
        tvFollowers.setText(user.getFollowersCount() + getString(R.string.followers));
        tvFollowing.setText(user.getFollowingsCount() + getString(R.string.following));
        Picasso.with(this).load(user.getProfileImageUrl()).into(ivProfileImage);
    }

}
