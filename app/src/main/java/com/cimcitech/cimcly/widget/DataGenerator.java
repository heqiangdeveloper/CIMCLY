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
        fragments[0] = new HomeFragment();//首页
        fragments[1] = new CustomerVisitFragment();//客户拜访
        fragments[2] = new IntentionTrackFragment();//意向跟踪
        fragments[3] = new UserFragment();//我的
        //fragments[4] = new LeaderHomeFragment();
        return fragments;
    }
}
