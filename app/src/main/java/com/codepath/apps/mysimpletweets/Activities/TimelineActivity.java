package com.codepath.apps.mysimpletweets.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.Fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.Fragments.MentionsTimelineFragment;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;

import org.parceler.Parcels;

public class TimelineActivity extends AppCompatActivity {

    HomeTimelineFragment homeTimelineFragment;
    MentionsTimelineFragment mentionsTimelineFragment;
    public static int COMPOSE_REQUEST_CODE = 1001;
    PagerSlidingTabStrip tabStrip;
    ViewPager viewPager;
    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        //Fragments
        homeTimelineFragment = new HomeTimelineFragment();
        mentionsTimelineFragment = new MentionsTimelineFragment();

        // toolbar
        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.tbTimeline);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        // Get the viewpager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        // Set the viewpager adapter for the pager
        viewPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
        // Find the pager sliding tabstrip
         tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach tabstrip and viewpager
        tabStrip.setViewPager(viewPager);

    }


    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle actionbar item clicks here
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void onProfileView(MenuItem mi) {
        // launch profile view
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("myIntent", ProfileActivity.PROFILE_INTENT);
        startActivity(i);
    }

    public void onComposeView(MenuItem item) {
        /*// launch tweet edit activity
        Intent i = new Intent(this, ComposeActivity.class);
        i.putExtra("myIntent", ComposeActivity.COMPOSE_CODE); // can the below # replace this fxnality
        startActivityForResult(i, COMPOSE_REQUEST_CODE);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //  inject the created tweet directly into the adapter for the timeline
        if (requestCode == COMPOSE_REQUEST_CODE || requestCode == ComposeActivity.REPLY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // get the tweet
                tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
                // send tweet to timeline fragment to add to adapter
                //get hometimeline fragment and call addTweet
                // pass to fragment
                homeTimelineFragment.addNewTweet(tweet);
            }
        } // if coming from a reply
        /*else if (requestCode == ComposeActivity.REPLY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // get the tweet
                tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
                // send tweet to timeline fragment to add to adapter
                //get hometimeline fragment and call addTweet
                // pass to fragment
                mentionsTimelineFragment.addNewTweet(tweet);
            }
        }*/
    }

    public void onComposeClick(View view) {
        // launch tweet edit activity
        Intent i = new Intent(this, ComposeActivity.class);
        i.putExtra("myIntent", ComposeActivity.COMPOSE_CODE);
        startActivityForResult(i, COMPOSE_REQUEST_CODE);
    }


    // Return the order of the fragments int the view pager
    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = {"Home", "Mentions"};

        // Adapter gets the manager insert or remove fragment from activity
        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // The order and creation of fragmetns w/in the pager
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return homeTimelineFragment;
            } else if (position == 1) {
                return mentionsTimelineFragment;
            } else {
                return null;
            }
        }

        // returns tab title
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        // how many pages to swipe between
        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }
}
