package com.codepath.apps.mysimpletweets.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class TweetDetailsActivity extends AppCompatActivity {

    private Tweet tweet;
    private ImageView ivLikeIt;
    private ImageView ivReply;
    private ImageView ivRetweet;
    private boolean isFaved;
    private boolean isRetweeted;
    private boolean areFollowing;
    private TwitterClient client;
    int likes;
    @ColorInt
    public static int TWTRBLUE = 0xff55acee;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet")); // counts are 0, zero, zilch, zip, nada, jack shit
        likes = getIntent().getIntExtra("likes", 0);
        isFaved = tweet.isFavorited(); // true or false dep on tweet liked or not
        isRetweeted = tweet.isRetweeted(); // retweeted?
        areFollowing = tweet.getUser().isFollowing();
        client = TwitterApplication.getRestClient();
        // toolbar
        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.tbTimeline);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        // get views
        getSupportActionBar().setTitle("@" + tweet.getUser().getScreenName());
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        ImageView ivFollow = (ImageView) findViewById(R.id.ivFollow);
        ivRetweet = (ImageView) findViewById(R.id.ivRetweetInDetails);
        ivReply = (ImageView) findViewById(R.id.ivReplyInDetails);
        ivLikeIt = (ImageView) findViewById(R.id.ivFaveInDetails);
        TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
        TextView tvRetweets = (TextView) findViewById(R.id.tvRetweets);
        TextView tvLikes = (TextView) findViewById(R.id.tvFavorites);
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvBody = (TextView) findViewById(R.id.tvBody);
        TextView tvTime = (TextView) findViewById(R.id.tvTime);
        TextView tvDate = (TextView) findViewById(R.id.tvDate);

        // load views
        // TODO: set time and date according to twitter styling
        tvBody.setText(tweet.getBody());
        tvBody.setLinkTextColor(TWTRBLUE);
        tvName.setText(tweet.getUser().getName());
        tvLikes.setText(String.valueOf(likes));
        //tvLikes.setText(String.valueOf(tweet.getLikesCount())); // both are 0, zero, zilch, zip, nada, jack shit
        tvRetweets.setText(String.valueOf(tweet.getRetweetsCount()));
        tvUserName.setText("@" + tweet.getUser().getScreenName());
        Picasso.with(this).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);
        if (ivLikeIt.isSelected()) {
            Picasso.with(this).load(R.drawable.ic_fave).into(ivLikeIt);
        } else if (!ivLikeIt.isSelected()) {
            Picasso.with(this).load(R.drawable.ic_faved).into(ivLikeIt);
        }
        if (!isRetweeted) {
            Picasso.with(this).load(R.drawable.ic_retweet).into(ivRetweet);
        } else if (isRetweeted) {
            Picasso.with(this).load(R.drawable.ic_retweeted).into(ivRetweet);
        }
        Picasso.with(this).load(R.drawable.ic_reply).into(ivReply);

        // TODO: make hashtags and mentions blue ("hashtags": [],"user_mentions": [] JSONArrays)
            // method that finds the string in the tweet.getBody() and sets that string segment to twitter blue
    }

    public void onProfileClick(View view) {
        // TODO: go to profile page
    }

    public void onUserHeaderClick(View view) {
        // start intent to pass images tag to profile activity
        Intent i = new Intent(this, ProfileActivity.class);
        // package intent
        // pass in screen name
        i.putExtra("screen_name", tweet.getUser().getScreenName());
        // pass in user
        i.putExtra("user", Parcels.wrap(tweet.getUser()));
        // pass in identifier
        i.putExtra("myIntent", ProfileActivity.IMAGE_INTENT);
        // start activity
        startActivity(i);
    }

    public void onReplyClick(View view) {
        //TODO: Reply w/ automatic @user
        Intent i = new Intent(this, ComposeActivity.class);
        // pass in the user screen name
        // TODO: Consider also adding in reply to
        // KEY --> "in_reply_to_user_id"
        i.putExtra("user_to_reply_to", "@" + tweet.getUser().getScreenName());
        // pass in tweet id to set in_reply_to_user_id
        i.putExtra("tweet_id", tweet.getUid());
        // to tell Compose Activity what intent started it
        i.putExtra("myIntent", ComposeActivity.REPLY_REQUEST_CODE);
        // start compose activity
        startActivity(i);
    }

    public void onLikeClick(View view) {
        //TODO: Favorite the tweet
        // get the tweet id
        long tweetID = tweet.getUid();

        // favorite tweet
        if (!isFaved) { // if it is not favorited
            client.faveStatus(tweetID, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    //Tweet tweet = new Tweet();
                    //if (response != null) {
                    //tweet = Tweet.fromJSON(response);
                    tweet.setFavorited(true);
                    ivLikeIt.setSelected(true);
                    //} // favorite call returns the favorited tweet

//                      // change color to red
                    Picasso.with(getBaseContext()).load(R.drawable.ic_faved).into(ivLikeIt);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    // rate limit
                    // can't favorite
                    // etc.
                }
            });
        }
        // unfavorite it
        if (isFaved) { // if tweet is favorited
            //TODO: Unfavorite the tweet if already favorited
            client.unFaveStatus(tweetID, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    //Tweet tweet = new Tweet();
                    //if (response != null) {
                    //tweet = Tweet.fromJSON(response);
                    tweet.setFavorited(false);
                    ivLikeIt.setSelected(false);
                    //  } // favorite call returns the favorited tweet
//                      // change color to gray
                    Picasso.with(getBaseContext()).load(R.drawable.ic_fave).into(ivLikeIt);
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    // rate limit
                    // can't favorite
                    // etc.
                    Log.d("DDD", "GFFF");
                }

            });
        }

    }

    public void onRetweetClick(View view) {

        //TODO: send retweet request with id
        long tweetID = tweet.getUid();
        client.retweetStatus(tweetID, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) { //
                // make tweet
                Tweet tweet = new Tweet();
                if (response != null) {
                    tweet = Tweet.fromJSON(response);
                }
                ivRetweet.setSelected(true);
                Picasso.with(getBaseContext()).load(R.drawable.ic_retweeted).into(ivRetweet);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("failure", errorResponse.toString());
                // statusCode == duplicate tweet
                // this is a duplicate tweet
                // statusCode == no internet
                // no internet connection
            }
        });

    }


    public void onFollowClick(View view) {
        long tweetID = tweet.getUid();
        String message = ("You unfollowed" + " @" + tweet.getUser().getScreenName().toString());
        Toast.makeText(TweetDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
        client.unfollowUser(tweetID, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(TweetDetailsActivity.this, "hi", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }
        });
    }

}
