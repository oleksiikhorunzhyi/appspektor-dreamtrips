package com.worldventures.dreamtrips.modules.feed.view.util;

import android.net.Uri;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;

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


    public void set(BaseFeedModel feedModel) {
        User user = feedModel.getUsers()[0];
        avatar.setImageURI(Uri.parse(user.getAvatar().getThumb()));
        text.setText(feedModel.infoText());
        //TODO location.setText();
        //TODO date.setText();
    }

}
