package com.worldventures.dreamtrips.modules.feed.view.util;

import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
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


    public void set(BaseFeedModel feedModel, Resources resources) {
        try {
            User user = feedModel.getUsers()[0];
            avatar.setImageURI(Uri.parse(user.getAvatar().getThumb()));
            text.setText(feedModel.infoText(resources));
            if (TextUtils.isEmpty(feedModel.getEntities()[0].place())) {
                location.setVisibility(View.GONE);
            } else {
                location.setVisibility(View.VISIBLE);
                location.setText(feedModel.getEntities()[0].place());
            }
            date.setText(DateTimeUtils.convertDateToString(feedModel.getPostedAt(),
                    DateTimeUtils.FULL_SCREEN_PHOTO_DATE_FORMAT));
        } catch (Exception e) {
            Timber.e(e, "Feed header error");
        }
    }

}
