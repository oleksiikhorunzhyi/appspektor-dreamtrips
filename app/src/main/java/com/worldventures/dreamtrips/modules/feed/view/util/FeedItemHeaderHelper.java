package com.worldventures.dreamtrips.modules.feed.view.util;

import android.net.Uri;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;

import butterknife.InjectView;
import timber.log.Timber;

public class FeedItemHeaderHelper {
    @InjectView(R.id.feed_header_avatar)
    SimpleDraweeView avatar;
    @InjectView(R.id.feed_header_text)
    TextView text;
    @InjectView(R.id.feed_header_location)
    TextView location;
    @InjectView(R.id.feed_header_date)
    TextView date;


    public void set(BaseFeedModel feedModel) {
        try {
            User user = feedModel.getUsers()[0];
            avatar.setImageURI(Uri.parse(user.getAvatar().getThumb()));
            text.setText(feedModel.infoText());
            location.setText(feedModel.getEntities()[0].place());
            date.setText(feedModel.getEntities()[0].date(date.getContext()));
        } catch (Exception e) {
            Timber.e(e, "Feed header error");
        }
    }

}
