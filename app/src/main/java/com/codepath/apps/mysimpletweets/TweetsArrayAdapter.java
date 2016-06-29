package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.Activities.ProfileActivity;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

// taking the tweet objs. and turn them into list views displayed in the list
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    private Context context;

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
        ImageView ivReply = (ImageView) convertView.findViewById(R.id.ivReply);
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
        TextView tvTimeAgo = (TextView) convertView.findViewById(R.id.tvTimeAgo);

        // 4. populate data into the subviews
        tvUserName.setText(tweet.getUser().getScreenName());
        tvBody.setText(tweet.getBody());
        tvTimeAgo.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
        ivProfileImage.setImageResource(android.R.color.transparent); // clear out the image for a recycled view
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);
        Picasso.with(getContext()).load(R.drawable.ic_reply).into(ivReply);

        // 5. store user screen name in tag for access in profile activity and set onItemClickListener
        ivProfileImage.setTag(R.string.image_screen_name_key, (tweet.getUser().getScreenName())); // tag holds an identifying id number and the user screen nam
        ivProfileImage.setTag(R.string.user, (tweet.getUser())); // send the user of this tweet
        ivProfileImage.setTag(R.string.uid, tweet.getUser().getUid()); // get the user id

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

        ivReply.setOnClickListener(new View.OnClickListener() {
            //TODO: Reply w/ automatic @user
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
