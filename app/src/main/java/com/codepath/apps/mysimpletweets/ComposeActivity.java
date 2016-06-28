package com.codepath.apps.mysimpletweets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {
    TwitterClient client;
    EditText etTweet;
    Button btnTweet;
    ArrayList<Tweet> tweets;
    TweetsArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        etTweet = (EditText) findViewById(R.id.etTweet);
        btnTweet = (Button) findViewById(R.id.btnTweet);
        client = TwitterApplication.getRestClient(); // what is happening her?

    }

    public void onClickPost(View view) {
        // gather text in etTweet
        final String tweetText = etTweet.getText().toString();
        // package as parameter
        // call new client to POST
        client.updateStatus(tweetText, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) { //
              // pass tweet to timeline intent
                Tweet tweet = Tweet.fromJSON(response);
                Log.d("mytweet", tweet.getBody().toString());
                Intent data = new Intent();
                data.putExtra("tweet", Parcels.wrap(tweet));
                setResult(RESULT_OK, data);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                Log.d("failure", jsonObject.toString());
                // statusCode == duplicate tweet
                    // this is a duplicate tweet
                // statusCode == no internet
                    // no internet connection
            }

        });

    }


}
