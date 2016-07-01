package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.Activities.ComposeActivity;
import com.codepath.apps.mysimpletweets.Activities.ProfileActivity;
import com.codepath.apps.mysimpletweets.Activities.TweetDetailsActivity;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

// taking the tweet objs. and turn them into list views displayed in the list
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    private Context context;
    private TwitterClient client = TwitterApplication.getRestClient();
    private ImageView ivFave;
    private boolean isFave;

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1, tweets);
        this.context = context;
    }

    // override and setup custom template
    // use viewholder pattern
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1. get the tweet
        final Tweet tweet = getItem(position);

        // 2. find or inflate the template
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
        }

        // 3. find subviews to fill with data
        final ImageView ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
        final ImageView ivReply = (ImageView) convertView.findViewById(R.id.ivReply);
        ivFave = (ImageView) convertView.findViewById(R.id.ivFave);
        final ImageView ivRetweet = (ImageView) convertView.findViewById(R.id.ivRetweet);
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
        TextView tvTimeAgo = (TextView) convertView.findViewById(R.id.tvTimeAgo);

        // 4. populate data into the subviews
        tvUserName.setText(tweet.getUser().getScreenName());
        tvBody.setText(tweet.getBody());
        tvBody.setLinkTextColor(TweetDetailsActivity.TWTRBLUE); // twitter blue for pretties
        tvTimeAgo.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
        ivProfileImage.setImageResource(android.R.color.transparent); // clear out the image for a recycled view
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);
        // reply view
        Picasso.with(getContext()).load(R.drawable.ic_reply).into(ivReply);
        // retweet view
        Picasso.with(getContext()).load(R.drawable.ic_retweet).into(ivRetweet);
        // if favorited
        if (tweet.isFavorited()) {
            Picasso.with(getContext()).load(R.drawable.ic_faved).into(ivFave); // make heart red
        } else {
            // favorite view
            Picasso.with(getContext()).load(R.drawable.ic_fave).into(ivFave); // make heart gray
        }

        // TODO: make hashtags and mentions blue ("hashtags": [],"user_mentions": [] JSONArrays)
            // method that finds the string in the tweet.getBody() and sets that string segment to twitter blue

        // 5. store various info in tag for access in other activities and set onItemClickListener
        ivProfileImage.setTag(R.string.image_screen_name_key, (tweet.getUser().getScreenName())); // tag holds an identifying id number and the user screen nam
        ivProfileImage.setTag(R.string.user, (tweet.getUser())); // send the user of this tweet
        ivProfileImage.setTag(R.string.uid, tweet.getUser().getUid()); // get the user id
        ivReply.setTag(R.string.user_to_reply_to, tweet.getUser().getScreenName());
        ivReply.setTag(R.string.tweet_id, tweet.getUid());
        tvBody.setTag(R.string.details_tweet, tweet);

        // Profile Click Listener
        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start intent to pass images tag to profile activity
                Intent i = new Intent(context, ProfileActivity.class);
                // package intent
                // pass in screen name
                i.putExtra("screen_name", ivProfileImage.getTag(R.string.image_screen_name_key).toString());
                // pass in user
                i.putExtra("user", Parcels.wrap((ivProfileImage.getTag(R.string.user))));
                // pass in identifier
                i.putExtra("myIntent", ProfileActivity.IMAGE_INTENT);
                // start activity
                context.startActivity(i);
            }
        });

        // Reply click Listener
        ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Reply w/ automatic @user
                Intent i = new Intent(context, ComposeActivity.class);
                // pass in the user screen name
                // TODO: Consider also adding in reply to
                // KEY --> "in_reply_to_user_id"
                i.putExtra("user_to_reply_to", "@" + ivReply.getTag(R.string.user_to_reply_to).toString());
                // pass in tweet id to set in_reply_to_user_id
                i.putExtra("tweet_id", (long) ivReply.getTag(R.string.tweet_id));
                // to tell Compose Activity what intent started it
                i.putExtra("myIntent", ComposeActivity.REPLY_REQUEST_CODE);
                // start compose activity
                context.startActivity(i);
            }

        });
        // favorite click listener
        ivFave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Favorite the tweet
                // change color to red
                Picasso.with(getContext()).load(R.drawable.ic_faved).into(ivFave);
                Toast.makeText(getContext(), String.valueOf(tweet.isFavorited()), Toast.LENGTH_SHORT).show();
                // get the tweet id
                long tweetID = tweet.getUid();

                // favorite tweet
                if (!tweet.isFavorited()) { // if it is not favorited
                    client.faveStatus(tweetID, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            // change color to red
                            Picasso.with(getContext()).load(R.drawable.ic_faved).into(ivFave);
                            tweet.setFavorited(true);
                            isFave = true;


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
                if (tweet.isFavorited()) { // if tweet is favorited
                    //TODO: Unfavorite the tweet if already favorited
                    // change color to gray
                    Picasso.with(getContext()).load(R.drawable.ic_fave).into(ivFave);
                    client.unFaveStatus(tweetID, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            //Tweet tweet = new Tweet();
                            //if (response != null) {
                                //tweet = Tweet.fromJSON(response);
                            tweet.setFavorited(false);
                            isFave = false;
                          //  } // favorite call returns the favorited tweet
//                      // change color to gray
                            Picasso.with(getContext()).load(R.drawable.ic_fave).into(ivFave);
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            // rate limit
                            // can't favorite
                            // etc.
                        }

                    });
                }

            }
        });
        // retweet
        ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        Picasso.with(getContext()).load(R.drawable.ic_retweeted).into(ivRetweet);

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
        });

        // Tweet/tweet body listener
        tvBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, TweetDetailsActivity.class);
                // send the tweet into the deatils activity
                i.putExtra("tweet", Parcels.wrap(tweet));
                context.startActivity(i);
            }
        });

        // 6. return the view to be inserted into the list
        return convertView;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    // insert the createdAt date
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (relativeDate.contains(" seconds ago")) {
            relativeDate = relativeDate.replace(" seconds ago", "s");
        } else if (relativeDate.contains(" second ago")) {
            relativeDate = relativeDate.replace(" second ago", "s");
        } else if (relativeDate.contains(" minute ago")) {
            relativeDate = relativeDate.replace(" minute ago", "m");
        }else if (relativeDate.contains(" minutes ago")) {
            relativeDate = relativeDate.replace(" minutes ago", "m");
        } else if (relativeDate.contains(" hour ago")) {
            relativeDate = relativeDate.replace(" hour ago", "h");
        } else if (relativeDate.contains(" hours ago")) {
            relativeDate = relativeDate.replace(" hours ago", "h");
        } else if (relativeDate.contains(" days ago")) {
            relativeDate = relativeDate.replace(" days ago", "d");
        } else if (relativeDate.contains(" day ago")) {
            relativeDate = relativeDate.replace(" day ago", "d");
        }
        // erase year based upon current year
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        if (relativeDate.contains(year)) {
            relativeDate = relativeDate.replace(year, "");
        }
        // Shorten months
        if (relativeDate.contains("January")) {
            relativeDate = relativeDate.replace("January", "Jan");
        }
        if (relativeDate.contains("February")) {
            relativeDate = relativeDate.replace("February", "Feb");
        }
        if (relativeDate.contains("March")) {
            relativeDate = relativeDate.replace("March", "Mar");
        }
        if (relativeDate.contains("April")) {
            relativeDate = relativeDate.replace("April", "Apr");
        }
        if (relativeDate.contains("June")) {
            relativeDate = relativeDate.replace("June", "Jun");
        }
        if (relativeDate.contains("July")) {
            relativeDate = relativeDate.replace("July", "Jul");
        }
        if (relativeDate.contains("August")) {
            relativeDate = relativeDate.replace("August", "Aug");
        }
        if (relativeDate.contains("September")) {
            relativeDate = relativeDate.replace("September", "Sep");
        }
        if (relativeDate.contains("October")) {
            relativeDate = relativeDate.replace("October", "Oct");
        }
        if (relativeDate.contains("November")) {
            relativeDate = relativeDate.replace("November", "Nov");
        }
        if (relativeDate.contains("December")) {
            relativeDate = relativeDate.replace("December", "Dec");
        }
        return relativeDate;
    }
}
