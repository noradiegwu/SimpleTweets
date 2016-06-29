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
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    TweetsArrayAdapter adapter;
    TextView tvCharacterCount;
    ArrayList<Tweet> tweets;
    TwitterClient client;
    int characterCount;
    EditText etTweet;
    Button btnTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        etTweet = (EditText) findViewById(R.id.etTweet);
        tvCharacterCount = (TextView) findViewById(R.id.tvCharacterCount);
        btnTweet = (Button) findViewById(R.id.btnTweet);
        client = TwitterApplication.getRestClient();

        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Fires right as the text is being changed (even supplies the range of text)
                characterCount = 140 - s.length();
                // can't deal with/catch auto complete!

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // Fires right before text is changing
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



    public void onClickPost(View view) {
        // gather text in etTweet
        final String tweetText = etTweet.getText().toString();
        // package as parameter
        // call new client to POST
        client.updateStatus(tweetText, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) { //
              // pass tweet to timeline intent
                Tweet tweet = new Tweet();
                if (response != null) {
                    tweet = Tweet.fromJSON(response);
                }
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

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        setResult(RESULT_CANCELED, data);
        finish();
    }
}
