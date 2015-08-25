package com.worldventures.dreamtrips.modules.feed.view.util;

import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import butterknife.InjectView;
import butterknife.Optional;
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

    @Optional
    @InjectView(R.id.comments_count)
    TextView commentsCount;
    @Optional
    @InjectView(R.id.comments)
    ImageView comments;
    @Optional
    @InjectView(R.id.likes_count)
    TextView likesCount;
    @Optional
    @InjectView(R.id.likes)
    ImageView likes;

    public void set(BaseEventModel feedModel, Context context) {
        try {
            User user = feedModel.getLinks().getUsers().get(0);
            avatar.setImageURI(Uri.parse(user.getAvatar().getThumb()));
            text.setText(Html.fromHtml(feedModel.infoText(context.getResources())));

            if (TextUtils.isEmpty(feedModel.getItem().place())) {
                location.setVisibility(View.GONE);
            } else {
                location.setVisibility(View.VISIBLE);
                location.setText(feedModel.getItem().place());
            }


            date.setText(DateTimeUtils.convertDateToString(feedModel.getCreatedAt(),
                    DateTimeUtils.FEED_DATE_FORMAT));

            if (likesCount != null) {
                if (feedModel.getItem().getLikesCount() > 0) {
                    likesCount.setVisibility(View.VISIBLE);
                    likesCount.setText(context.getString(R.string.likes, feedModel.getItem().getLikesCount()));
                } else likesCount.setVisibility(View.GONE);
            }

            if (commentsCount != null) {
                if (feedModel.getItem().getCommentsCount() > 0) {
                    commentsCount.setVisibility(View.VISIBLE);
                    commentsCount.setText(context.getString(R.string.comments, feedModel.getItem().getCommentsCount()));
                } else commentsCount.setVisibility(View.GONE);
            }

            if (likes != null) {
                likes.setEnabled(true);
                likes.setImageResource(feedModel.getItem().isLiked() ?
                        R.drawable.ic_feed_thumb_up_blue :
                        R.drawable.ic_feed_thumb_up);
            }

            if (comments != null) {
                comments.setEnabled(true);
            }

        } catch (Exception e) {
            Timber.e(e, "Feed header error");
        }
    }

}
