package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

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
        Tweet tweet = getItem(position);
        // 2. find or inflate the template
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
        }
        // 3. find subviews to fill with data
        final ImageView ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
        // 4. populate data into the subviews
        tvUserName.setText(tweet.getUser().getScreenName());
        tvBody.setText(tweet.getBody());
        ivProfileImage.setImageResource(android.R.color.transparent); // clear out the image for a recycled view
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);
        // 5. store user screen name in tag for access in profile activity and set onItemClickListener
        ivProfileImage.setTag(R.string.image_screen_name_key, (tweet.getUser().getScreenName())); // tag holds an identifying id number and the user screen nam
        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start intent to pass images tag to profile activity
                Intent i = new Intent(context, ProfileActivity.class);
                // package intent
                i.putExtra("screen_name", ivProfileImage.getTag(R.string.image_screen_name_key).toString());
                // start activity
                context.startActivity(i);
            }
        });
        // 6. return the view to be inserted into the list
        return convertView;
    }
}
