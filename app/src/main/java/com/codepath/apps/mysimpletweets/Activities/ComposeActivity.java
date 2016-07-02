package com.codepath.apps.mysimpletweets.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {
    public static int REPLY_REQUEST_CODE = 2002;
    public static int COMPOSE_CODE = 3003;
    static int TXT_LIMIT = 140;
    String replyToUser = "";
    TweetsArrayAdapter adapter;
    TextView tvCharacterCount;
    ArrayList<Tweet> tweets;
    private TwitterClient client;
    int characterCount;
    EditText etTweet;
    Button btnTweet;
    int myIntent;
    ImageView ivMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        final ImageView ivMe = (ImageView) findViewById(R.id.ivMe);
        Picasso.with(this).load("https://pbs.twimg.com/profile_images/721496192400678912/_Q9BInMY.jpg").into(ivMe);
        etTweet = (EditText) findViewById(R.id.etTweet);
        tvCharacterCount = (TextView) findViewById(R.id.tvCharacterCount);
        btnTweet = (Button) findViewById(R.id.btnTweet);
        client = TwitterApplication.getRestClient();

        // sets an intent code to use to tell what intent started this activity: (REPLY or COMPOSE)
        myIntent = getIntent().getIntExtra("myIntent", 0);

        // If i am replying, automatically put user's @ in the text box
        if (myIntent == REPLY_REQUEST_CODE) {
            // puts @screenName in text box
            replyToUser = getIntent().getStringExtra("user_to_reply_to");
            etTweet.setText(replyToUser);
            etTweet.setSelection(replyToUser.length());
            // set the value of the in_reply_to_status_id here or at finish
            //tvCharacterCount.setText(TXT_LIMIT - replyToUser.length());
        }

        // text change listener for 140 char count
        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Fires right as the text is being changed (even supplies the range of text)
                characterCount = TXT_LIMIT - s.length();
                // can't deal with/catch auto complete!

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Fires right after the text has changed
                if (characterCount >= 0) {
                    tvCharacterCount.setText(String.valueOf(characterCount));
                    tvCharacterCount.setTextColor(Color.BLACK);
                } else {
                    tvCharacterCount.setTextColor(Color.RED);
                    tvCharacterCount.setText(String.valueOf(characterCount));
                }
            }
        });
    }
    // when i click the send tweet button
    public void onClickPost(View view) {
        // gather text in etTweet
        final String tweetText = etTweet.getText().toString();
        // package as parameter
        // call new client to POST

        // if I composed a new tweet
        if (myIntent == COMPOSE_CODE) {
            client.updateStatus(tweetText, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) { //
                    // make tweet
                    Tweet tweet = new Tweet();
                    if (response != null) {
                        tweet = Tweet.fromJSON(response);
                    }
                    // pass back new tweet to home timeline
                    Intent data = new Intent();
                    data.putExtra("tweet", Parcels.wrap(tweet));
                    setResult(RESULT_OK, data);
                    finish();
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

        // if I replied to a tweet
        if (myIntent == REPLY_REQUEST_CODE) {
            // get tweet id
            long inReplyToTweetID = getIntent().getLongExtra("tweet_id", 0L);
            // reply to the tweet
            client.replyToStatus(tweetText, inReplyToTweetID, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // pass tweet to timeline intent
                    Tweet tweet = new Tweet();
                    if (response != null) {
                        tweet = Tweet.fromJSON(response);
                    } /*else { Toast.makeText(this, "too few characters!", Toast.LENGTH_SHORT).show();} // let user know they didn't type anything*/

                    // pass back new tweet to home timeline
                    Intent data = new Intent();
                    data.putExtra("tweet", Parcels.wrap(tweet));
                    setResult(RESULT_OK, data);
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        setResult(RESULT_CANCELED, data);
        finish();
    }
}
