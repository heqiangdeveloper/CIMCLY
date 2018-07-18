package com.cimcitech.cimcly.widget;

import android.support.v4.app.Fragment;

import com.cimcitech.cimcly.activity.home.customer_visit.CustomerVisitFragment;
import com.cimcitech.cimcly.activity.home.HomeFragment;
import com.cimcitech.cimcly.activity.home.intention_track.IntentionTrackFragment;
import com.cimcitech.cimcly.activity.user.UserFragment;

/**
 * Created by zhouwei on 17/4/23.
 */

public class DataGenerator {
    public static Fragment[] getFragments() {
        Fragment fragments[] = new Fragment[4];
        fragments[0] = new HomeFragment();
        fragments[1] = new CustomerVisitFragment();
        fragments[2] = new IntentionTrackFragment();
        fragments[3] = new UserFragment();
        //fragments[4] = new LeaderHomeFragment();
        return fragments;
    }
}
