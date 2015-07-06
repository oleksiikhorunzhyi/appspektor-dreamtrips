package com.worldventures.dreamtrips.modules.feed.view.util;

import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class FeedItemHeaderHelper {
    @InjectView(R.id.avatar)
    SimpleDraweeView avatar;
    @InjectView(R.id.text)
    TextView text;
    @InjectView(R.id.location)
    TextView location;
    @InjectView(R.id.date)
    TextView date;


    public void set(){

    }

}
