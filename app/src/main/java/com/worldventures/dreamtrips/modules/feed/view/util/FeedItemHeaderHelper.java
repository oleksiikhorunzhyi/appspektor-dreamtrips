package com.worldventures.dreamtrips.modules.feed.view.util;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.innahema.collections.query.queriables.Queryable;
import com.linearlistview.LinearListView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;
import com.worldventures.dreamtrips.modules.feed.view.adapter.CommentLinearAdapter;

import java.util.Collections;

import butterknife.InjectView;
import butterknife.OnClick;
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
    @InjectView(R.id.comments_count)
    TextView commentsCount;
    @InjectView(R.id.commentsList)
    LinearListView comments;
    @InjectView(R.id.likes_count)
    TextView likesCount;
    @InjectView(R.id.likes)
    ImageView likes;

    public void set(BaseFeedModel feedModel, Context context) {
        try {
            User user = feedModel.getLinks().getUsers().get(0);
            avatar.setImageURI(Uri.parse(user.getAvatar().getThumb()));
            text.setText(feedModel.infoText(context.getResources()));

            if (TextUtils.isEmpty(feedModel.getItem().place())) {
                location.setVisibility(View.GONE);
            } else {
                location.setVisibility(View.VISIBLE);
                location.setText(feedModel.getItem().place());
            }


            date.setText(DateTimeUtils.convertDateToString(feedModel.getCreatedAt(),
                    DateTimeUtils.FULL_SCREEN_PHOTO_DATE_FORMAT));

            likesCount.setText(context.getString(R.string.likes, feedModel.getItem().likesCount()));
            commentsCount.setText(context.getString(R.string.comments, feedModel.getItem().commentsCount()));

            likes.setImageResource(feedModel.getItem().isLiked() ?
                    R.drawable.ic_feed_thumb_up_blue :
                    R.drawable.ic_feed_thumb_up);

            if (feedModel.getItem().getComments() != null) {
                Collections.reverse(feedModel.getItem().getComments());
                comments.setAdapter(new CommentLinearAdapter(Queryable.from(feedModel.getItem().getComments())
                        .take(2)
                        .toList(), context));
            }

        } catch (Exception e) {
            Timber.e(e, "Feed header error");
        }
    }

}
