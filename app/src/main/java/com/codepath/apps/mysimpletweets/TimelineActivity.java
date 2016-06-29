package com.codepath.apps.mysimpletweets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.Fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.Fragments.MentionsTimelineFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;

import org.parceler.Parcels;

public class TimelineActivity extends AppCompatActivity {

    HomeTimelineFragment homeTimelineFragment;
    MentionsTimelineFragment mentionsTimelineFragment;
    int COMPOSE_REQUEST_CODE = 1001;
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

        // Get the viewpager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        // Set the viewpager adapter for the pager
        viewPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
        // Find the pager sliding tabstrip
         tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach tabstrip and viewpager
        tabStrip.setViewPager(viewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        // launch tweet edit activity
        Intent i = new Intent(this, ComposeActivity.class);
        startActivityForResult(i, COMPOSE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //  inject the created tweet directly into the adapter for the timeline
        if (requestCode == COMPOSE_REQUEST_CODE) {
            // get the tweet
            tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            // send tweet to timeline fragment to add to adapter
            //get hometimeline fragment and call addTweet
            // pass to fragment
            homeTimelineFragment.addNewTweet(tweet);
            }
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
